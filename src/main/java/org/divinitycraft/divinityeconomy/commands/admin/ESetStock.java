package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.enchants.MarketableEnchant;
import org.divinitycraft.divinityeconomy.utils.Converter;
import org.bukkit.entity.Player;

/**
 * A command for setting the stock of a material
 */
public class ESetStock extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public ESetStock(DEPlugin app) {
        super(app, "esetstock", true, Setting.COMMAND_E_SET_STOCK_ENABLE_BOOLEAN);
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
        int stock;
        switch (args.length) {
            case 2:
                enchantData = getMain().getEnchMan().getEnchant(args[0]);
                stock = Converter.getInt(args[1]);
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (enchantData == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidItemName.logLevel, LangEntry.MARKET_InvalidEnchantName.get(getMain()), args[0]);
            return true;
        }

        // Ensure stock is greater than 0
        if (stock < 0) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidStockAmount.logLevel, LangEntry.MARKET_InvalidStockAmount.get(getMain()), stock, 0);
            return true;
        }


        int previousStock = enchantData.getQuantity();
        double previousValue = getMain().getEnchMan().getBuyPrice(enchantData.getQuantity());
        getMain().getEnchMan().setQuantity(enchantData, stock);
        getMain().getConsole().send(sender, LangEntry.STOCK_CountChanged.logLevel, LangEntry.STOCK_CountChanged.get(getMain()), previousStock, getMain().getConsole().formatMoney(previousValue), stock, getMain().getConsole().formatMoney(getMain().getEnchMan().getBuyPrice(enchantData.getQuantity())));

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
