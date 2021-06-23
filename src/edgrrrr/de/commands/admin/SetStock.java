package edgrrrr.de.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import edgrrrr.de.materials.MaterialData;
import edgrrrr.de.math.Math;
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
        MaterialData materialData = null;
        int stock = -1;
        switch (args.length) {
            case 2:
                materialData = this.app.getMaterialManager().getMaterial(args[0]);
                stock = Math.getInt(args[1]);
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

        // Ensure stock is greater than 0
        if (stock < 0) {
            this.app.getConsole().send(sender, CommandResponse.InvalidStockAmount.defaultLogLevel, String.format(CommandResponse.InvalidStockAmount.message, stock, 0));
            return true;
        }


        int previousStock = materialData.getQuantity();
        double previousValue = this.app.getMaterialManager().getUserPrice(materialData.getQuantity());
        this.app.getMaterialManager().setQuantity(materialData, stock);
        this.app.getConsole().send(sender, CommandResponse.StockCountChanged.defaultLogLevel, String.format(CommandResponse.StockCountChanged.message, previousStock, this.app.getConsole().formatMoney(previousValue), stock, this.app.getConsole().formatMoney(this.app.getMaterialManager().getUserPrice(materialData.getQuantity()))));

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
