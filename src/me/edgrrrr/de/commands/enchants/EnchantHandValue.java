package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.items.enchants.EnchantValueResponse;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import me.edgrrrr.de.player.PlayerManager;
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
            this.getMain().getConsole().usage(sender, CommandResponse.InvalidItemHeld.message, this.help.getUsages());
            return true;
        }

        // Ensure item is enchanted
        if (!this.getMain().getEnchMan().isEnchanted(heldItem)) {
            this.getMain().getConsole().usage(sender, CommandResponse.InvalidItemHeld.message, this.help.getUsages());
            return true;
        }


        // Get evr1
        EnchantValueResponse evr1 = this.getMain().getEnchMan().getBuyValue(heldItem, 0);
        if (evr1.isFailure()) {
            this.getMain().getConsole().warn(sender, "Couldn't determine buy value of %d enchants( %s ) because %s", evr1.getQuantity(), evr1.listNames(), evr1.getErrorMessage());
        }

        this.getMain().getConsole().info(sender, "Buy: %d enchants costs %s", evr1.getQuantity(), this.getMain().getConsole().formatMoney(evr1.getValue()));
        for (MarketableToken token1 : evr1.getTokens()) {
            MarketableEnchant enchant1 = (MarketableEnchant) token1;
            this.getMain().getConsole().info(sender, "  -Buy: %d %s costs %s", evr1.getQuantity(enchant1), enchant1.getCleanName(), this.getMain().getConsole().formatMoney(evr1.getValue(enchant1)));
        }

        EnchantValueResponse evr2 = this.getMain().getEnchMan().getSellValue(heldItem, 0);
        if (evr2.isFailure()) {
            this.getMain().getConsole().warn(sender, "Couldn't determine sell value of %d enchants( %s ) because %s", evr2.getQuantity(), evr2.listNames(), evr2.getErrorMessage());
        }

        this.getMain().getConsole().info(sender, "Sell: %d enchants costs %s", evr2.getQuantity(), this.getMain().getConsole().formatMoney(evr2.getValue()));
        for (MarketableToken token2 : evr2.getTokens()) {
            MarketableEnchant enchant2 = (MarketableEnchant) token2;
            this.getMain().getConsole().info(sender, "  -Sell: %d %s costs %s", evr2.getQuantity(enchant2), enchant2.getCleanName(), this.getMain().getConsole().formatMoney(evr2.getValue(enchant2)));
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
