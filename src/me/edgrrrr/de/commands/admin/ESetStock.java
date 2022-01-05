package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import me.edgrrrr.de.utils.Converter;
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
                enchantData = this.getMain().getEnchMan().getEnchant(args[0]);
                stock = Converter.getInt(args[1]);
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (enchantData == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, CommandResponse.InvalidEnchantName.message, args[0]);
            return true;
        }

        // Ensure stock is greater than 0
        if (stock < 0) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidStockAmount.defaultLogLevel, CommandResponse.InvalidStockAmount.message, stock, 0);
            return true;
        }


        int previousStock = enchantData.getQuantity();
        double previousValue = this.getMain().getEnchMan().getBuyPrice(enchantData.getQuantity());
        this.getMain().getEnchMan().setQuantity(enchantData, stock);
        this.getMain().getConsole().send(sender, CommandResponse.StockCountChanged.defaultLogLevel, CommandResponse.StockCountChanged.message, previousStock, this.getMain().getConsole().formatMoney(previousValue), stock, this.getMain().getConsole().formatMoney(this.getMain().getEnchMan().getBuyPrice(enchantData.getQuantity())));

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
