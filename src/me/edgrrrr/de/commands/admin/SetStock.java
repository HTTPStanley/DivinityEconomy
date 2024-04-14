package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;

/**
 * A command for setting the stock of a material
 */
public class SetStock extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public SetStock(DEPlugin app) {
        super(app, "setstock", true, Setting.COMMAND_SET_STOCK_ENABLE_BOOLEAN);
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
        MarketableMaterial marketableMaterial;
        int stock;
        switch (args.length) {
            case 2:
                marketableMaterial = getMain().getMarkMan().getItem(args[0]);
                stock = Converter.getInt(args[1]);
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (marketableMaterial == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidItemName.logLevel, LangEntry.MARKET_InvalidItemName.get(getMain()), args[0]);
            return true;
        }

        // Ensure stock is greater than 0
        if (stock < 0) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidStockAmount.logLevel, LangEntry.MARKET_InvalidStockAmount.get(getMain()), stock, 0);
            return true;
        }


        int previousStock = marketableMaterial.getQuantity();
        double previousValue = marketableMaterial.getManager().getBuyPrice(marketableMaterial.getQuantity());
        marketableMaterial.getManager().setQuantity(marketableMaterial, stock);
        getMain().getConsole().send(sender, LangEntry.STOCK_CountChanged.logLevel, LangEntry.STOCK_CountChanged.get(getMain()), previousStock, getMain().getConsole().formatMoney(previousValue), stock, getMain().getConsole().formatMoney(marketableMaterial.getManager().getBuyPrice(marketableMaterial.getQuantity())));

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
