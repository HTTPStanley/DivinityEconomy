package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.items.ItemManager;
import me.edgrrrr.de.market.items.enchants.EnchantValueResponse;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for valuing the item in the users hand
 */
public class HandValue extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public HandValue(DEPlugin app) {
        super(app, "handvalue", false, Setting.COMMAND_HAND_VALUE_ENABLE_BOOLEAN);
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
        int amount = 1;
        boolean valueAll = false;
        boolean valueHand = false;
        switch (args.length) {
            case 0:
                valueHand = true;
                break;

            case 1:
                String firstArg = args[0].toLowerCase();
                if (LangEntry.W_max.is(getMain(), firstArg)) {
                    valueAll = true;
                } else {
                    amount = Converter.getInt(firstArg);
                }
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure amount is greater than 0
        if (amount < 0) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()));
            return true;
        }

        ItemStack heldItem = PlayerManager.getHeldItem(sender);

        // Ensure user is holding an item
        if (heldItem == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidItemHeld.logLevel, LangEntry.MARKET_InvalidItemHeld.get(getMain()));
            return true;
        }

        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(heldItem);
        ItemStack[] buyStacks;
        ItemStack[] sellStacks;
        ItemStack[] itemStacks = marketableMaterial.getMaterialSlots(sender);

        if (valueHand) {
            amount = heldItem.getAmount();
            buyStacks = marketableMaterial.getItemStacks(amount);
            sellStacks = new ItemStack[1];
            sellStacks[0] = heldItem;
        } else if (valueAll) {
            amount = ItemManager.getMaterialCount(itemStacks);
            sellStacks = itemStacks;
            buyStacks = marketableMaterial.getItemStacks(amount);
        } else {
            sellStacks = marketableMaterial.getItemStacks(amount);
            buyStacks = sellStacks;
        }

        ValueResponse buyResponse = marketableMaterial.getManager().getBuyValue(buyStacks);
        ValueResponse sellResponse = marketableMaterial.getManager().getSellValue(sellStacks);

        if (buyResponse.isSuccess()) {
            getMain().getConsole().info(sender, LangEntry.VALUE_BuyResponse.get(getMain()), amount, marketableMaterial.getName(), getMain().getConsole().formatMoney(buyResponse.getValue()));

            EnchantValueResponse evr = getMain().getEnchMan().getBuyValue(heldItem, 0);
            if (evr.isSuccess()) {
                getMain().getConsole().info(sender, LangEntry.PURCHASE_ValueEnchantSummary.get(getMain()), evr.getQuantity(), getMain().getConsole().formatMoney(evr.getValue()));
                for (MarketableToken token1 : evr.getTokens()) {
                    MarketableEnchant enchant1 = (MarketableEnchant) token1;
                    getMain().getConsole().info(sender, LangEntry.PURCHASE_ValueEnchant.get(getMain()), evr.getQuantity(enchant1), enchant1.getName(), getMain().getConsole().formatMoney(evr.getValue(enchant1)));
                }
            }

        } else {
            getMain().getConsole().info(sender, LangEntry.VALUE_BuyFailedResponse.get(getMain()), amount, marketableMaterial.getName(), buyResponse.getErrorMessage());
        }

        if (sellResponse.isSuccess()) {
            getMain().getConsole().info(sender, LangEntry.VALUE_SellResponse.get(getMain()), amount, marketableMaterial.getName(), getMain().getConsole().formatMoney(sellResponse.getValue()));

            EnchantValueResponse evr = getMain().getEnchMan().getSellValue(heldItem, 0);
            if (evr.isSuccess()) {
                getMain().getConsole().info(sender, LangEntry.SALE_ValueEnchantSummary.get(getMain()), evr.getQuantity(), getMain().getConsole().formatMoney(evr.getValue()));
                for (MarketableToken token2 : evr.getTokens()) {
                    MarketableEnchant enchant2 = (MarketableEnchant) token2;
                    getMain().getConsole().info(sender, LangEntry.SALE_ValueEnchant.get(getMain()), evr.getQuantity(enchant2), enchant2.getName(), getMain().getConsole().formatMoney(evr.getValue(enchant2)));
                }
            }

        } else {
            getMain().getConsole().info(sender, LangEntry.VALUE_SellFailedResponse.get(getMain()), amount, marketableMaterial.getName(), sellResponse.getErrorMessage());
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
