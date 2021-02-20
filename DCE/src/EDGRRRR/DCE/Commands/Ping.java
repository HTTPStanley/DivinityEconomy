package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Main.DCEPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A simple ping pong! command
 */
public class Ping implements CommandExecutor {
    private final DCEPlugin app;

    public Ping(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConf().strComPing))) {
            this.app.getCon().severe(player, "This command is not enabled.");
            return true;
        }

        this.app.getCon().info(player, "Pong!");
        return true;
    }
}
