package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialValueResponse;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for selling items in the users hand
 */
public class HandSell extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public HandSell(DEPlugin app) {
        super(app, "handsell", false, Setting.COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN);
    }

    /**
     * ###To be overridden by the actual command
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        int amountToSell = 1;
        boolean sellAll = false;

        switch (args.length) {
            case 0:
                break;

            case 1:
                String arg = args[0];
                if (arg.equalsIgnoreCase("max")) {
                    sellAll = true;
                } else {
                    amountToSell = Converter.getInt(args[0]);
                }
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount is above 0
        if (amountToSell < 1) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }


        ItemStack heldItem = PlayerManager.getHeldItem(sender);

        // Ensure item held is not null
        if (heldItem == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidItemHeld.defaultLogLevel, CommandResponse.InvalidItemHeld.message);
            return true;
        }

        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(heldItem);
        int materialCount = marketableMaterial.getMaterialCount(sender);

        if (sellAll) {
            amountToSell = materialCount;
        }

        // Ensure player inventory has enough
        if (materialCount < amountToSell) {
            this.getMain().getConsole().logFailedSale(sender, amountToSell, marketableMaterial.getCleanName(), String.format(CommandResponse.InvalidInventoryStock.message, materialCount, amountToSell));
            return true;
        }

        // Get item stacks to remove
        // Clone item stacks in case they need to be refunded
        // Get the value
        MaterialValueResponse response = marketableMaterial.getManager().getSellValue(marketableMaterial.getMaterialSlotsToCount(sender, amountToSell));

        // Check for removed items
        if (response.getItemStacks().size() == 0) {
            this.getMain().getConsole().logFailedSale(sender, response.getQuantity(), marketableMaterial.getCleanName(), CommandResponse.NothingToSellAfterSkipping.message.toLowerCase());
            return true;
        }


        // If response was unsuccessful, return
        if (response.isFailure()) {
            this.getMain().getConsole().logFailedSale(sender, response.getQuantity(), marketableMaterial.getCleanName(), response.getErrorMessage());
            return true;
        }


        // Remove items from player inventory and add cash
        PlayerManager.removePlayerItems(response.getItemStacksAsArray());
        EconomyResponse economyResponse = this.getMain().getEconMan().addCash(sender, response.getValue());

        // if response was unsuccessful, refund items
        if (!economyResponse.transactionSuccess()) {
            PlayerManager.addPlayerItems(sender, response.getClonesAsArray());
            // Handles console, player message and mail
            this.getMain().getConsole().logFailedSale(sender, response.getQuantity(), marketableMaterial.getCleanName(), economyResponse.errorMessage);
            return true;
        }


        // Edit the quantity of the item and send the player a message
        marketableMaterial.getManager().editQuantity(marketableMaterial, response.getQuantity());
        this.getMain().getConsole().logSale(sender, response.getQuantity(), response.getValue(), marketableMaterial.getCleanName());
        return true;
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        return false;
    }
}
