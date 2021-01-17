package EDGRRRR.DCE.Main.commands;

import static EDGRRRR.DCE.Main.App.getCon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;

public class Ping implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        getCon().info(sender, "Pong!");
        return true;
    }  
}
