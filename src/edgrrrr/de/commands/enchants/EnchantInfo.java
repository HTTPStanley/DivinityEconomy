package edgrrrr.de.commands.enchants;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandEnchant;
import edgrrrr.de.config.Setting;
import edgrrrr.de.enchants.EnchantData;
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
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        EnchantData enchantData = this.app.getEnchantmentManager().getEnchant(enchantName);
        if (enchantData == null) {
            this.app.getConsole().usage(sender, "Unknown Item: " + enchantName, this.help.getUsages());
        } else {
            this.app.getConsole().info(sender, "==[Information for " + enchantData.getCleanName() + "]==");
            this.app.getConsole().info(sender, "ID: " + enchantData.getID());
            this.app.getConsole().info(sender, "Current Quantity: " + enchantData.getQuantity());
            this.app.getConsole().info(sender, "Is Banned: " + !(enchantData.getAllowed()));
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
