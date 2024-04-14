package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.enchants.EnchantValueResponse;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.response.Response;
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
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
            }
        }

        // Ensure amount is more than one
        if (enchantLevels < 1) {
            getMain().getConsole().warn(sender, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()));
            return true;
        }

        // Ensure user is holding an item
        ItemStack heldItem = PlayerManager.getHeldItem(sender);
        if (heldItem == null) {
            getMain().getConsole().warn(sender, LangEntry.MARKET_InvalidItemHeld.get(getMain()));
            return true;
        }

        // Ensure held item is only one
        if (heldItem.getAmount() > 1) {
            getMain().getConsole().warn(sender, LangEntry.MARKET_EnchantsInvalidItemAmount.get(getMain()));
            return true;
        }

        // Ensure item valuation was successful
        EnchantValueResponse evr = getMain().getEnchMan().getBuyValue(heldItem, enchantName, enchantLevels);
        if (evr.isFailure()) {
            getMain().getConsole().logFailedPurchase(sender, enchantLevels, enchantName, evr.getErrorMessage());
            return true;
        }

        // Ensure given enchant exists
        MarketableEnchant enchantData = getMain().getEnchMan().getEnchant(enchantName);
        if (enchantData == null) {
            getMain().getConsole().logFailedPurchase(sender, enchantLevels, enchantName, String.format(LangEntry.MARKET_InvalidEnchantName.get(getMain()), enchantName));
            return true;
        }


        // Ensure user has enough money
        double startingBalance = getMain().getEconMan().getBalance(sender);
        EconomyResponse economyResponse = getMain().getEconMan().remCash(sender, evr.getValue());

        if (!economyResponse.transactionSuccess()) {
            getMain().getConsole().logFailedPurchase(sender, enchantLevels, enchantData.getName(), economyResponse.errorMessage);
            getMain().getEconMan().setCash(sender, startingBalance);
            return true;
        }

        // Was a success


        // Add enchant to item
        Response response = getMain().getEnchMan().addEnchantToItem(heldItem, enchantData.getEnchantment(), enchantLevels);

        // Handle failure
        if (response.isFailure()) {
            getMain().getConsole().logFailedPurchase(sender, enchantLevels, enchantData.getName(), response.getErrorMessage());
            getMain().getEconMan().setCash(sender, startingBalance);
            return true;
        }

        // Success
        getMain().getConsole().logPurchase(sender, enchantLevels, evr.getValue(), enchantData.getName());
        getMain().getEnchMan().editLevelQuantity(enchantData, -enchantLevels);
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
