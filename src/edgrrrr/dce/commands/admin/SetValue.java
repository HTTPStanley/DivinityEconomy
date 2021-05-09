package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommand;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
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
    public SetValue(DCEPlugin app) {
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
        this.app.getConsole().send(sender, CommandResponse.StockValueChanged.defaultLogLevel, String.format(CommandResponse.StockValueChanged.message, previousValue, previousStock, value, materialData.getQuantity()));

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
