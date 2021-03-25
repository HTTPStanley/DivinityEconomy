package edgrrrr.dce.commands.admin;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.help.Help;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for saving the materials
 */
public class SaveMaterials implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public SaveMaterials(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("savematerials");
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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_SAVE_MATERIALS_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        this.app.getMaterialManager().saveMaterials();
        DCEPlugin.CONSOLE.info(player, "Saved Materials");
        return true;
    }
}
