package org.divinitycraft.divinityeconomy.commands.enchants;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandEnchant;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.ItemManager;
import org.divinitycraft.divinityeconomy.market.items.enchants.EnchantValueResponse;
import org.divinitycraft.divinityeconomy.market.items.enchants.MarketableEnchant;
import org.divinitycraft.divinityeconomy.player.PlayerManager;
import org.divinitycraft.divinityeconomy.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for selling enchants on held items
 */
public class EnchantHandSell extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandSell(DEPlugin app) {
        super(app, "esell", false, Setting.COMMAND_E_SELL_ENABLE_BOOLEAN);
        this.checkEnchantMarketEnabled = true;
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
        // The name of the enchant
        // The number of levels to sell
        // If all levels should be sold
        String enchantName;
        int enchantLevels = 1;
        boolean sellAllLevels = false;
        boolean sellAllEnchants = false;

        switch (args.length) {
            case 1:
                enchantName = args[0];
                if (LangEntry.W_max.is(getMain(), enchantName)) {
                    sellAllEnchants = true;
                    sellAllLevels = true;
                }
                break;


            // If user enters name and level
            // Sell enchant level times
            case 2:
                enchantName = args[0];
                enchantLevels = Converter.getInt(args[1]);
                break;

            // If wrong number of arguments
            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // get the item the user is holding.
        // ensure it is not null
        ItemStack heldItem = PlayerManager.getHeldItem(sender);
        if (heldItem == null) {
            getMain().getConsole().usage(sender, LangEntry.MARKET_InvalidItemHeld.get(getMain()), this.help.getUsages());
            return true;
        }

        // Ensure held item is only one
        if (heldItem.getAmount() > 1) {
            getMain().getConsole().warn(sender, LangEntry.MARKET_EnchantsInvalidItemAmount.get(getMain()));
            return true;
        }

        // Ensure item is enchanted
        if (!getMain().getEnchMan().isEnchanted(heldItem)) {
            getMain().getConsole().usage(sender, LangEntry.MARKET_InvalidItemHeld.get(getMain()), this.help.getUsages());
            return true;
        }

        // Create clone in-case of reimburse error
        ItemStack heldItemCopy = ItemManager.clone(heldItem);

        // If sell all enchants is true
        // Then use MultiValueResponse and use getSellValue of entire item
        // Then add quantity of each enchant / remove enchant from item
        // Then add cash
        // if cash add fails - reimburse old item
        if (sellAllEnchants) {
            EnchantValueResponse evr = getMain().getEnchMan().getSellValue(new ItemStack[]{heldItem});

            // If failed to get value
            if (evr.isFailure()) {
                getMain().getConsole().logFailedSale(sender, evr.getQuantity(), LangEntry.MARKET_EnchantList.get(getMain(), evr.listNames()), evr.getErrorMessage());
                return true;
            }


            // If successful
            for (String enchantID : evr.getTokenIds()) {
                MarketableEnchant enchantmentData = getMain().getEnchMan().getEnchant(enchantID);
                getMain().getEnchMan().editLevelQuantity(enchantmentData, evr.getQuantity(enchantID));
                getMain().getEnchMan().removeEnchantLevelsFromItem(heldItem, enchantmentData.getEnchantment(), evr.getQuantity(enchantID));
            }

            // Add the cash
            EconomyResponse economyResponse = getMain().getEconMan().addCash(sender, evr.getValue());

            // If failed to add cash
            if (!economyResponse.transactionSuccess()) {
                PlayerManager.replaceItemStack(sender, heldItem, heldItemCopy);
                getMain().getConsole().logFailedSale(sender, evr.getQuantity(), LangEntry.MARKET_EnchantList.get(getMain(), evr.listNames()), economyResponse.errorMessage);
                return true;
            }


            getMain().getConsole().logSale(sender, evr.getQuantity(), evr.getValue(), LangEntry.MARKET_EnchantList.get(getMain(), evr.listNames()));
            return true;
        }


        // If only handling one enchant
        // Ensure enchant exists
        MarketableEnchant enchantData = getMain().getEnchMan().getEnchant(enchantName);
        if (enchantData == null) {
            getMain().getConsole().usage(sender, String.format(LangEntry.MARKET_InvalidEnchantName.get(getMain()), enchantName), this.help.getUsages());
            return true;
        }

        // Update enchantLevels to the max if sellAllEnchants is true
        //noinspection ConstantConditions
        if (sellAllLevels) {
            enchantLevels = heldItem.getEnchantmentLevel(enchantData.getEnchantment());
        }

        // Get value
        EnchantValueResponse evr = getMain().getEnchMan().getSellValue(heldItem, enchantData.getID(), enchantLevels);

        // Ensure valuation was successful
        if (evr.isFailure()) {
            getMain().getConsole().logFailedSale(sender, enchantLevels, enchantData.getName(), evr.getErrorMessage());
            return true;
        }

        // Remove enchants, add quantity and add cash
        getMain().getEnchMan().removeEnchantLevelsFromItem(heldItem, enchantData.getEnchantment(), enchantLevels);
        EconomyResponse economyResponse = getMain().getEconMan().addCash(sender, evr.getValue());


        // If failed to add cash
        if (!economyResponse.transactionSuccess()) {
            PlayerManager.replaceItemStack(sender, heldItem, heldItemCopy);
            getMain().getConsole().logFailedSale(sender, enchantLevels, enchantData.getName(), economyResponse.errorMessage);
            return true;
        }

        // Edit enchant quantity & log
        getMain().getEnchMan().editLevelQuantity(enchantData, enchantLevels);
        getMain().getConsole().logSale(sender, enchantLevels, evr.getValue(), enchantData.getName());

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
