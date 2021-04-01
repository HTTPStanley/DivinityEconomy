package edgrrrr.dce.commands.enchants;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.help.Help;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for getting information about enchants
 */
public class EnchantInfo implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public EnchantInfo(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("einfo");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_E_INFO_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_ENCHANTS_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "The enchant market is not enabled.");
            return true;
        }

        String enchantName;
        switch (args.length) {
            case 1:
                enchantName = args[0];
                break;

            default:
                this.app.getConsole().usage(player, "Invalid number of arguments.", this.help.getUsages());
                return true;
        }

        EnchantData enchantData = this.app.getEnchantmentManager().getEnchant(enchantName);
        if (enchantData == null) {
            this.app.getConsole().usage(player, "Unknown Item: " + enchantName, this.help.getUsages());
        } else {
            this.app.getConsole().info(player, "==[Information for " + enchantData.getCleanName() + "]==");
            this.app.getConsole().info(player, "ID: " + enchantData.getID());
            this.app.getConsole().info(player, "Current Quantity: " + enchantData.getQuantity());
            this.app.getConsole().info(player, "Is Banned: " + !(enchantData.getAllowed()));
        }

        return true;
    }
}
