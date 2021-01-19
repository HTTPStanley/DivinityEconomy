package EDGRRRR.DCE.Main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import EDGRRRR.DCE.Main.App;

/**
 * A command executor class for replying to /balance
 */
public class Balance implements CommandExecutor {
    private App app;

    public Balance(App app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure sender is player or return
        if (!(sender instanceof Player)) {
            return true;
        }

        // Create player object
        Player player = (Player) sender;

        // Reply to player with their balance.
        app.getCon().info(player, "Balance: £" + app.getEco().getBalance(player));
        // Graceful exit
        return true;
    }
}
