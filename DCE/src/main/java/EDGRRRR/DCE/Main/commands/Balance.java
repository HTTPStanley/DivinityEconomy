package EDGRRRR.DCE.Main.commands;

import static EDGRRRR.DCE.Main.App.getCon;
import static EDGRRRR.DCE.Main.App.getEco;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command executor class for replying to /balance
 */
public class Balance implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure sender is player or return
        if (!(sender instanceof Player)) {
            return true;
        }

        // Create player object
        Player player = (Player) sender;

        // Reply to player with their balance.
        getCon().info(player, "Balance: £" + getEco().getBalance(player));
        // Graceful exit
        return true;
    }
}
