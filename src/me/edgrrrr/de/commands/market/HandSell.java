package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.Constants;
import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
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
        this.checkItemMarketEnabled = true;
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
                if (LangEntry.W_max.is(getMain(), arg)) {
                    sellAll = true;
                } else {
                    amountToSell = Converter.getInt(args[0]);
                }
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure amount is within constraints
        if (amountToSell > Constants.MAX_VALUE_AMOUNT || amountToSell < Constants.MIN_VALUE_AMOUNT) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()));
            return true;
        }


        ItemStack heldItem = PlayerManager.getHeldItem(sender);

        // Ensure item held is not null
        if (heldItem == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidItemHeld.logLevel, LangEntry.MARKET_InvalidItemHeld.get(getMain()));
            return true;
        }

        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(heldItem);

        // Ensure marketable material is not null
        if (marketableMaterial == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidItemHeld.logLevel, LangEntry.MARKET_InvalidItemHeld.get(getMain()));
            return true;
        }

        int materialCount = marketableMaterial.getMaterialCount(sender);

        if (sellAll) {
            amountToSell = materialCount;
        }

        // Ensure the material is allowed to be bought and sold
        if (!marketableMaterial.getAllowed()) {
            getMain().getConsole().logFailedSale(sender, amountToSell, marketableMaterial.getName(), LangEntry.MARKET_ItemIsBanned.get(getMain(), marketableMaterial.getName()));
            return true;
        }

        // Ensure player inventory has enough
        if (materialCount < amountToSell) {
            getMain().getConsole().logFailedSale(sender, amountToSell, marketableMaterial.getName(), String.format(LangEntry.MARKET_InvalidInventoryStock.get(getMain()), materialCount, amountToSell));
            return true;
        }

        // Get item stacks to remove
        // Clone item stacks in case they need to be refunded
        // Get the value
        MaterialValueResponse response = marketableMaterial.getManager().getSellValue(marketableMaterial.getMaterialSlotsToCount(sender, amountToSell));

        // Check for removed items
        if (response.getItemStacks().size() == 0) {
            getMain().getConsole().logFailedSale(sender, response.getQuantity(), marketableMaterial.getName(), LangEntry.MARKET_NothingToSellAfterSkipping.get(getMain()).toLowerCase());
            return true;
        }


        // If response was unsuccessful, return
        if (response.isFailure()) {
            getMain().getConsole().logFailedSale(sender, response.getQuantity(), marketableMaterial.getName(), response.getErrorMessage());
            return true;
        }


        // Remove items from player inventory and add cash
        PlayerManager.removePlayerItems(response.getItemStacksAsArray());
        EconomyResponse economyResponse = getMain().getEconMan().addCash(sender, response.getValue());

        // if response was unsuccessful, refund items
        if (!economyResponse.transactionSuccess()) {
            PlayerManager.addPlayerItems(sender, response.getClonesAsArray());
            // Handles console, player message and mail
            getMain().getConsole().logFailedSale(sender, response.getQuantity(), marketableMaterial.getName(), economyResponse.errorMessage);
            return true;
        }


        // Edit the quantity of the item and send the player a message
        marketableMaterial.getManager().editQuantity(marketableMaterial, response.getQuantity());
        getMain().getConsole().logSale(sender, response.getQuantity(), response.getValue(), marketableMaterial.getName());
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
