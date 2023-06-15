package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.response.Response;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for buying enchants for the item held in a users hand
 */
public class EnchantHandBuy extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandBuy(DEPlugin app) {
        super(app, "ebuy", false, Setting.COMMAND_E_BUY_ENABLE_BOOLEAN);
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
        String enchantName;
        int enchantLevels = 1;
        // How to use
        switch (args.length) {
            case 1 -> enchantName = args[0];
            case 2 -> {
                enchantName = args[0];
                enchantLevels = Converter.getInt(args[1]);
            }
            default -> {
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
            }
        }

        // Ensure amount is more than one
        if (enchantLevels < 1) {
            this.getMain().getConsole().warn(sender, CommandResponse.InvalidAmountGiven.message);
            return true;
        }

        // Ensure user is holding an item
        ItemStack heldItem = PlayerManager.getHeldItem(sender);
        if (heldItem == null) {
            this.getMain().getConsole().warn(sender, CommandResponse.InvalidItemHeld.message);
            return true;
        }

        // Ensure item valuation was successful
        ValueResponse valueResponse = this.getMain().getEnchMan().getBuyValue(heldItem, enchantName, enchantLevels);
        if (valueResponse.isFailure()) {
            this.getMain().getConsole().logFailedPurchase(sender, enchantLevels, enchantName, valueResponse.errorMessage);
            return true;
        }

        // Ensure given enchant exists
        MarketableEnchant enchantData = this.getMain().getEnchMan().getEnchant(enchantName);
        if (enchantData == null) {
            this.getMain().getConsole().logFailedPurchase(sender, enchantLevels, enchantName, String.format(CommandResponse.InvalidEnchantName.message, enchantName));
            return true;
        }


        double startingBalance = this.getMain().getEconMan().getBalance(sender);
        EconomyResponse economyResponse = this.getMain().getEconMan().remCash(sender, valueResponse.value);

        if (!economyResponse.transactionSuccess()) {
            this.getMain().getConsole().logFailedPurchase(sender, enchantLevels, enchantData.getCleanName(), economyResponse.errorMessage);
            this.getMain().getEconMan().setCash(sender, startingBalance);
        } else {
            Response response = this.getMain().getEnchMan().addEnchantToItem(heldItem, enchantData.getEnchantment(), enchantLevels);
            if (response.isFailure()) {
                this.getMain().getConsole().logFailedPurchase(sender, enchantLevels, enchantData.getCleanName(), response.errorMessage);
                this.getMain().getEconMan().setCash(sender, startingBalance);
            } else {
                this.getMain().getConsole().logPurchase(sender, enchantLevels, valueResponse.value, enchantData.getCleanName());
                this.getMain().getEnchMan().editLevelQuantity(enchantData, -enchantLevels);
            }
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
