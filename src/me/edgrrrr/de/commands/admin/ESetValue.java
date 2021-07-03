package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.enchants.EnchantData;
import me.edgrrrr.de.math.Math;
import org.bukkit.entity.Player;

/**
 * A command for setting the value of an item
 */
public class ESetValue extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public ESetValue(DEPlugin app) {
        super(app, "esetvalue", true, Setting.COMMAND_E_SET_VALUE_ENABLE_BOOLEAN);
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
        double value = -1;
        switch (args.length) {
            case 2:
                enchantData = this.getMain().getEnchantmentManager().getEnchant(args[0]);
                value = Math.getDouble(args[1]);
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (enchantData == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidEnchantName.defaultLogLevel, CommandResponse.InvalidItemName.message, args[0]);
            return true;
        }

        if (value < 0) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message, value, 0);
            return true;
        }

        int previousStock = enchantData.getQuantity();
        double previousValue = this.getMain().getEnchantmentManager().getUserPrice(enchantData.getQuantity());
        this.getMain().getEnchantmentManager().setPrice(enchantData, value);
        this.getMain().getConsole().send(sender, CommandResponse.StockValueChanged.defaultLogLevel, CommandResponse.StockValueChanged.message, this.getMain().getConsole().formatMoney(previousValue), previousStock, this.getMain().getConsole().formatMoney(value), enchantData.getQuantity());

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
