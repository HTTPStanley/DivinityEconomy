package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;

/**
 * A command for setting the value of an item
 */
public class SetValue extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public SetValue(DEPlugin app) {
        super(app, "setvalue", true, Setting.COMMAND_SET_VALUE_ENABLE_BOOLEAN);
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
        double value;
        switch (args.length) {
            case 2:
                marketableMaterial = this.getMain().getMarkMan().getItem(args[0]);
                value = Converter.getDouble(args[1]);
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (marketableMaterial == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, String.format(CommandResponse.InvalidItemName.message, args[0]));
            return true;
        }

        if (value < 0) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, String.format(CommandResponse.InvalidAmountGiven.message, value, 0));
            return true;
        }

        int previousStock = marketableMaterial.getQuantity();
        double previousValue = marketableMaterial.getManager().getBuyPrice(marketableMaterial.getQuantity());
        marketableMaterial.getManager().setPrice(marketableMaterial, value);
        this.getMain().getConsole().send(sender, CommandResponse.StockValueChanged.defaultLogLevel, String.format(CommandResponse.StockValueChanged.message, this.getMain().getConsole().formatMoney(previousValue), previousStock, this.getMain().getConsole().formatMoney(value), marketableMaterial.getQuantity()));

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
