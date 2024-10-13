package org.divinitycraft.divinityeconomy.commands.enchants;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandEnchant;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.divinitycraft.divinityeconomy.market.items.enchants.EnchantValueResponse;
import org.divinitycraft.divinityeconomy.market.items.enchants.MarketableEnchant;
import org.divinitycraft.divinityeconomy.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for valuing enchants
 */
public class EnchantHandValue extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandValue(DEPlugin app) {
        super(app, "ehandvalue", false, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN);
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

        // If sell all enchants is true
        // Then use MultiValueResponse and use getSellValue of entire item
        // get the item the user is holding.
        // ensure it is not null
        ItemStack heldItem = PlayerManager.getHeldItem(sender);
        if (heldItem == null) {
            getMain().getConsole().usage(sender, LangEntry.MARKET_InvalidItemHeld.get(getMain()), this.help.getUsages());
            return true;
        }

        // Ensure item is enchanted
        if (!getMain().getEnchMan().isEnchanted(heldItem)) {
            getMain().getConsole().usage(sender, LangEntry.MARKET_InvalidItemHeld.get(getMain()), this.help.getUsages());
            return true;
        }


        // Get evr1
        EnchantValueResponse evr1 = getMain().getEnchMan().getBuyValue(heldItem, 0);
        if (evr1.isFailure()) {
            getMain().getConsole().warn(sender, LangEntry.PURCHASE_ValueEnchantFailure.get(getMain()), evr1.getQuantity(), evr1.listNames(), evr1.getErrorMessage());
        }

        getMain().getConsole().info(sender, LangEntry.PURCHASE_ValueEnchantSummary.get(getMain()), evr1.getQuantity(), getMain().getConsole().formatMoney(evr1.getValue()));
        for (MarketableToken token1 : evr1.getTokens()) {
            MarketableEnchant enchant1 = (MarketableEnchant) token1;
            getMain().getConsole().info(sender, LangEntry.PURCHASE_ValueEnchant.get(getMain()), evr1.getQuantity(enchant1), enchant1.getName(), getMain().getConsole().formatMoney(evr1.getValue(enchant1)));
        }

        EnchantValueResponse evr2 = getMain().getEnchMan().getSellValue(heldItem, 0);
        if (evr2.isFailure()) {
            getMain().getConsole().warn(sender, LangEntry.SALE_ValueEnchantFailure.get(getMain()), evr2.getQuantity(), evr2.listNames(), evr2.getErrorMessage());
        }

        getMain().getConsole().info(sender, LangEntry.SALE_ValueEnchantSummary.get(getMain()), evr2.getQuantity(), getMain().getConsole().formatMoney(evr2.getValue()));
        for (MarketableToken token2 : evr2.getTokens()) {
            MarketableEnchant enchant2 = (MarketableEnchant) token2;
            getMain().getConsole().info(sender, LangEntry.SALE_ValueEnchant.get(getMain()), evr2.getQuantity(enchant2), enchant2.getName(), getMain().getConsole().formatMoney(evr2.getValue(enchant2)));
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
