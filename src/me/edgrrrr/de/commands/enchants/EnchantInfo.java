package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import org.bukkit.entity.Player;

/**
 * A command for getting information about enchants
 */
public class EnchantInfo extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantInfo(DEPlugin app) {
        super(app, "einfo", true, Setting.COMMAND_E_INFO_ENABLE_BOOLEAN);
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
        String enchantName;
        switch (args.length) {
            case 1:
                enchantName = args[0];
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        MarketableEnchant enchantData = this.getMain().getEnchMan().getEnchant(enchantName);
        if (enchantData == null) {
            this.getMain().getConsole().usage(sender, String.format("Unknown Item: %s", enchantName), this.help.getUsages());
        } else {
            this.getMain().getConsole().info(sender, "==[Information for %s]==", enchantData.getCleanName());
            this.getMain().getConsole().info(sender, "ID: %s", enchantData.getID());
            this.getMain().getConsole().info(sender, "Current Quantity: %s", enchantData.getQuantity());
            this.getMain().getConsole().info(sender, "Is Banned: %s", !(enchantData.getAllowed()));
        }

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
