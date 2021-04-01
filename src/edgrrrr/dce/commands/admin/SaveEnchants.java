package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for saving the materials
 */
public class SaveEnchants implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public SaveEnchants(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("saveenchants");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_SAVE_ENCHANTS_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "This command is not enabled.");
            return true;
        }

        this.app.getEnchantmentManager().saveEnchants();
        this.app.getConsole().info(player, "Saved Enchants");
        return true;
    }
}
