package edgrrrr.de.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import edgrrrr.de.enchants.EnchantData;
import edgrrrr.de.math.Math;
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
        EnchantData enchantData = null;
        int stock = -1;
        switch (args.length) {
            case 2:
                enchantData = this.app.getEnchantmentManager().getEnchant(args[0]);
                stock = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (enchantData == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, String.format(CommandResponse.InvalidEnchantName.message, args[0]));
            return true;
        }

        // Ensure stock is greater than 0
        if (stock < 0) {
            this.app.getConsole().send(sender, CommandResponse.InvalidStockAmount.defaultLogLevel, String.format(CommandResponse.InvalidStockAmount.message, stock, 0));
            return true;
        }


        int previousStock = enchantData.getQuantity();
        double previousValue = this.app.getEnchantmentManager().getUserPrice(enchantData.getQuantity());
        this.app.getEnchantmentManager().setQuantity(enchantData, stock);
        this.app.getConsole().send(sender, CommandResponse.StockCountChanged.defaultLogLevel, String.format(CommandResponse.StockCountChanged.message, previousStock, previousValue, stock, this.app.getEnchantmentManager().getUserPrice(enchantData.getQuantity())));

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
