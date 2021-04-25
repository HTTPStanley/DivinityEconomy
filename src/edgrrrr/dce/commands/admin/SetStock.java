package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommand;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
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
    public SetStock(DCEPlugin app) {
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
        MaterialData materialData = null;
        int stock = -1;
        switch (args.length) {
            case 2:
                materialData = this.app.getMaterialManager().getMaterial(args[0]);
                stock = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                break;
        }

        // Ensure material exists
        if (materialData == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, String.format(CommandResponse.InvalidItemName.message, args[0]));
            return true;
        }

        // Ensure stock is greater than 0
        if (stock < 0) {
            this.app.getConsole().send(sender, CommandResponse.InvalidStockAmount.defaultLogLevel, String.format(CommandResponse.InvalidStockAmount.message, stock, 0));
            return true;
        }


        int previousStock = materialData.getQuantity();
        double previousValue = materialData.getUserPrice();
        materialData.setQuantity(stock);
        this.app.getConsole().send(sender, CommandResponse.InvalidStockAmount.defaultLogLevel, String.format("Changed stock level from %d(£%,.2f) to %d(£%,.2f).", previousStock, previousValue, stock, materialData.getUserPrice()));

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
        MaterialData materialData = null;
        int stock = -1;
        switch (args.length) {
            case 2:
                materialData = this.app.getMaterialManager().getMaterial(args[0]);
                stock = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsole().send(CommandResponse.InvalidNumberOfArguments.defaultLogLevel, CommandResponse.InvalidNumberOfArguments.message);
                break;
        }

        // Ensure material exists
        if (materialData == null) {
            this.app.getConsole().send(CommandResponse.InvalidItemName.defaultLogLevel, String.format(CommandResponse.InvalidItemName.message, args[0]));
            return true;
        }

        // Ensure stock is greater than 0
        if (stock < 0) {
            this.app.getConsole().send(CommandResponse.InvalidStockAmount.defaultLogLevel, String.format(CommandResponse.InvalidStockAmount.message, stock, 0));
            return true;
        }


        int previousStock = materialData.getQuantity();
        double previousValue = materialData.getUserPrice();
        materialData.setQuantity(stock);
        this.app.getConsole().info(String.format("Changed stock level from %d(£%,.2f) to %d(£%,.2f).", previousStock, previousValue, stock, materialData.getUserPrice()));

        return true;
    }
}
