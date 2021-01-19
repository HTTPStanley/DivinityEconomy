package EDGRRRR.DCE.Main.commands;

import EDGRRRR.DCE.Main.App;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A simple ping pong! command
 */
public class SpawnItem implements CommandExecutor {
    private App app;

    public SpawnItem(App app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }
        
        // Get player
        Player player = (Player) sender;

        // Ensure two args
        if (!(args.length == 2)) {
            app.getCon().warn(player, "Incorrect usage, see¬");
            return false;
        }

        

        return true;
    }
}
