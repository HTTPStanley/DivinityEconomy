package edgrrrr.dce.commands.money;

import edgrrrr.dce.main.DCEPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command executor class for replying to /balance
 */
public class Balance implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/balance | /balance <username>";

    public Balance(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure sender is player or return
        if (!(sender instanceof Player)) {
            return true;
        }

        // Create player object
        Player from = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComBalance))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command - returns the callers balance.
        // command <username> - returns the usernames balance.
        Player to;
        OfflinePlayer toOff = null;

        switch (args.length) {
            case 1:
                // Get online player
                to = this.app.getServer().getPlayer(args[0]);
                // If they aren't online or don't exist. Do the dirty offline call.
                if (to == null) {
                    toOff = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                }
                break;

            default:
                // any number of args.. just return their own.
                to = from;
                break;
        }

        if (to == null && toOff == null) {
            this.app.getConsoleManager().usage(from, "Invalid player name.", usage);
            return true;
        }

        double balance;
        if (to != null) {
            balance = this.app.getEconomyManager().round(this.app.getEconomyManager().getBalance(to));
            if (!(from == to)) {
                this.app.getConsoleManager().info(from, to.getName() + "'s Balance: £" + balance);
            } else {
                this.app.getConsoleManager().info(from, "Balance: £" + balance);
            }
        } else {
            balance = this.app.getEconomyManager().round(this.app.getEconomyManager().getBalance(toOff));
            this.app.getConsoleManager().info(from, toOff.getName() + "'s Balance: £" + balance);
        }
        return true;
    }
}
