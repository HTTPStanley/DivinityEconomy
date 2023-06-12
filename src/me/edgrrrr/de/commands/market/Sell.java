package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for selling items to the market
 */
public class Sell extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public Sell(DEPlugin app) {
        super(app, "sell", false, Setting.COMMAND_SELL_ITEM_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        String materialName;
        int amountToSell = 1;
        boolean sellAll = false;

        switch (args.length) {
            // Just material, used default amount of 1
            case 1:
                materialName = args[0];
                break;

            // Material & Amount
            case 2:
                materialName = args[0];
                String arg = args[1];
                if (arg.equalsIgnoreCase("max")) {
                    sellAll = true;
                } else {
                    amountToSell = Converter.getInt(args[1]);
                }
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Check amount is greater than  0
        if (amountToSell < 1) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }


        // Check material given exists
        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(materialName);
        if (marketableMaterial == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, CommandResponse.InvalidItemName.message, materialName);
            return true;
        }

        // Ensure player has enough of the material to sell.
        int materialCount = marketableMaterial.getMaterialCount(sender);
        if (sellAll) {
            amountToSell = materialCount;
        }
        if (materialCount < amountToSell) {
            this.getMain().getConsole().logFailedSale(sender, amountToSell, marketableMaterial.getCleanName(), String.format(CommandResponse.InvalidInventoryStock.message, materialCount, amountToSell));
            return true;
        }

        // Get item stacks
        // Remove enchanted items
        // Clone incase need to be refunded
        // Get valuation
        ItemStack[] allStacks = marketableMaterial.getMaterialSlotsToCount(sender, amountToSell);
        ItemStack[] itemStacks = MarketableMaterial.removeEnchantedItems(allStacks);
        ItemStack[] itemStacksClone = MarketableMaterial.cloneItems(itemStacks);

        // Check for removed items
        if (itemStacks.length != allStacks.length) {
            this.getMain().getConsole().logFailedSale(sender, amountToSell, marketableMaterial.getCleanName(), CommandResponse.EnchantedItemsRemoved.message.toLowerCase());
            return true;
        }

        ValueResponse response = marketableMaterial.getManager().getSellValue(itemStacks);

        if (response.isSuccess()) {
            PlayerManager.removePlayerItems(itemStacks);

            EconomyResponse economyResponse = this.getMain().getEconMan().addCash(sender, response.value);
            if (!economyResponse.transactionSuccess()) {
                PlayerManager.addPlayerItems(sender, itemStacksClone);
                // Handles console, player message and mail
                this.getMain().getConsole().logFailedSale(sender, amountToSell, marketableMaterial.getCleanName(), economyResponse.errorMessage);

            } else {
                marketableMaterial.getManager().editQuantity(marketableMaterial, amountToSell);
                // Handles console, player message and mail
                this.getMain().getConsole().logSale(sender, amountToSell, response.value, marketableMaterial.getCleanName());
            }
        } else {
            // Handles console, player message and mail
            this.getMain().getConsole().logFailedSale(sender, amountToSell, marketableMaterial.getCleanName(), response.errorMessage);
        }

        return true;
    }

    /**
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
