package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.math.Math;
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
                marketableMaterial = this.getMain().getMarkMan().getItem(args[0]);
                stock = Math.getInt(args[1]);
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (marketableMaterial == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, CommandResponse.InvalidItemName.message, args[0]);
            return true;
        }

        // Ensure stock is greater than 0
        if (stock < 0) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidStockAmount.defaultLogLevel, CommandResponse.InvalidStockAmount.message, stock, 0);
            return true;
        }


        int previousStock = marketableMaterial.getQuantity();
        double previousValue = marketableMaterial.getManager().getBuyPrice(marketableMaterial.getQuantity());
        marketableMaterial.getManager().setQuantity(marketableMaterial, stock);
        this.getMain().getConsole().send(sender, CommandResponse.StockCountChanged.defaultLogLevel, CommandResponse.StockCountChanged.message, previousStock, this.getMain().getConsole().formatMoney(previousValue), stock, this.getMain().getConsole().formatMoney(marketableMaterial.getManager().getBuyPrice(marketableMaterial.getQuantity())));

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
