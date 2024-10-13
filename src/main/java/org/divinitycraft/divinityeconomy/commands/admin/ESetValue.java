package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.enchants.MarketableEnchant;
import org.divinitycraft.divinityeconomy.utils.Converter;
import org.bukkit.entity.Player;

/**
 * A command for setting the value of an item
 */
public class ESetValue extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public ESetValue(DEPlugin app) {
        super(app, "esetvalue", true, Setting.COMMAND_E_SET_VALUE_ENABLE_BOOLEAN);
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
        MarketableEnchant enchantData;
        double value;
        switch (args.length) {
            case 2:
                enchantData = getMain().getEnchMan().getEnchant(args[0]);
                value = Converter.getDouble(args[1]);
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (enchantData == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidEnchantName.logLevel, LangEntry.MARKET_InvalidItemName.get(getMain()), args[0]);
            return true;
        }

        if (value < 0) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()), value, 0);
            return true;
        }

        int previousStock = enchantData.getQuantity();
        double previousValue = getMain().getEnchMan().getBuyPrice(enchantData.getQuantity());
        getMain().getEnchMan().setPrice(enchantData, value);
        getMain().getConsole().send(sender, LangEntry.STOCK_ValueChanged.logLevel, LangEntry.STOCK_ValueChanged.get(getMain()), getMain().getConsole().formatMoney(previousValue), previousStock, getMain().getConsole().formatMoney(value), enchantData.getQuantity());

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
        return this.onPlayerCommand(null, args);
    }
}
