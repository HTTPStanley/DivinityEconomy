package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for reloading the materials
 */
public class ReloadMaterials implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public ReloadMaterials(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("reloadmaterials");
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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_RELOAD_MATERIALS_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "This command is not enabled.");
            return true;
        }

        this.app.getMaterialManager().loadAliases();
        this.app.getMaterialManager().loadMaterials();
        this.app.getConsole().info(player, "Reloaded Materials");
        return true;
    }
}
