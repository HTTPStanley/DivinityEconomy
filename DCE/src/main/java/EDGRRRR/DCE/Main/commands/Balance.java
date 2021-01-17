package EDGRRRR.DCE.Main.commands;

import static EDGRRRR.DCE.Main.App.getCon;
import static EDGRRRR.DCE.Main.App.getEco;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;

public class Balance implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        getCon().info(sender, "Balance: £" + getEco().getBalance(player));
        return true;
    }  
}
