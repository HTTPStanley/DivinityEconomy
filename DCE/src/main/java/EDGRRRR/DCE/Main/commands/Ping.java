package EDGRRRR.DCE.Main.commands;

import static EDGRRRR.DCE.Main.App.getCon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A simple ping pong! command
 */
public class Ping implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        getCon().info(player, "Pong!");
        return true;
    }
}
