package edgrrrr.dce.commands.enchants;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.enchants.EnchantData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for getting information about enchants
 */
public class EnchantInfo implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/einfo <enchantName>";

    public EnchantInfo(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_E_INFO_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_ENCHANTS_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "The enchant market is not enabled.");
            return true;
        }

        String enchantName;
        switch (args.length) {
            case 1:
                enchantName = args[0];
                break;

            default:
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        EnchantData enchantData = this.app.getEnchantmentManager().getEnchant(enchantName);
        if (enchantData == null) {
            DCEPlugin.CONSOLE.usage(player, "Unknown Item: " + enchantName, this.usage);
        } else {
            DCEPlugin.CONSOLE.info(player, "==[Information for " + enchantData.getCleanName() + "]==");
            DCEPlugin.CONSOLE.info(player, "ID: " + enchantData.getID());
            DCEPlugin.CONSOLE.info(player, "Current Quantity: " + enchantData.getQuantity());
            DCEPlugin.CONSOLE.info(player, "Is Banned: " + !(enchantData.getAllowed()));
        }

        return true;
    }
}
