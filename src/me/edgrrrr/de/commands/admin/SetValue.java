package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.materials.MaterialData;
import me.edgrrrr.de.math.Math;
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
        MaterialData materialData = null;
        double value = -1;
        switch (args.length) {
            case 2:
                materialData = this.app.getMaterialManager().getMaterial(args[0]);
                value = Math.getDouble(args[1]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (materialData == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, String.format(CommandResponse.InvalidItemName.message, args[0]));
            return true;
        }

        if (value < 0) {
            this.app.getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, String.format(CommandResponse.InvalidAmountGiven.message, value, 0));
            return true;
        }

        int previousStock = materialData.getQuantity();
        double previousValue = this.app.getMaterialManager().getUserPrice(materialData.getQuantity());
        this.app.getMaterialManager().setPrice(materialData, value);
        this.app.getConsole().send(sender, CommandResponse.StockValueChanged.defaultLogLevel, String.format(CommandResponse.StockValueChanged.message, this.app.getConsole().formatMoney(previousValue), previousStock, this.app.getConsole().formatMoney(value), materialData.getQuantity()));

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
