package EDGRRRR.DCE.Main.commands;

import EDGRRRR.DCE.Main.App;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A simple ping pong! command
 */
public class Ping implements CommandExecutor {
    private App app;

    public Ping(App app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(app.getConfig().getBoolean(app.getConf().strComPing))) {
            app.getCon().severe(player, "This command is not enabled.");
            return true;
        }

        app.getCon().info(player, "Pong!");
        return true;
    }
}
