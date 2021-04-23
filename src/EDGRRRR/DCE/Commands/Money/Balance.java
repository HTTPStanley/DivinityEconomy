package edgrrrr.dce.commands.money;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for getting player's balances
 */
public class Balance implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;
    private final Help consoleHelp;

    public Balance(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("balance");
        this.consoleHelp = new Help("balance", help.getDescription(), help.getAliases(), help.getPermissionNode(), new String[] {"/balance <playername>"});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure sender is player or return
        if (sender instanceof Player) {

            // Create player object
            Player player1 = (Player) sender;

            // Ensure command is enabled
            if (!(this.app.getConfig().getBoolean(Setting.COMMAND_BALANCE_ENABLE_BOOLEAN.path))) {
                this.app.getConsole().severe(player1, "This command is not enabled.");
                return true;
            }

            // Use case scenarios
            // command - returns the callers balance.
            // command <username> - returns the usernames balance.
            OfflinePlayer player2 = null;

            switch (args.length) {
                case 1:
                    // Get online player
                    player2 = this.app.getServer().getPlayer(args[0]);
                    // If they aren't online or don't exist. Do the dirty offline call.
                    if (player2 == null) {
                        player2 = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                    }
                    break;

                default:
                    // any number of args.. just return their own.
                    player2 = player1;
                    break;
            }

            if (player2 == null) {
                this.app.getConsole().usage(player1, "Invalid player name.", this.help.getUsages());
                return true;
            }

            double balance = this.app.getEconomyManager().getBalance(player2);
            if (!(player1 == player2)) {
                this.app.getConsole().info(player1, String.format("%s's balance: £%,.2f", player2.getName(), balance));
            } else {
                this.app.getConsole().info(player1, String.format("Balance: £%,.2f", balance));
            }
        } else {
            // Ensure command is enabled
            if (!(this.app.getConfig().getBoolean(Setting.COMMAND_BALANCE_ENABLE_BOOLEAN.path))) {
                this.app.getConsole().severe("This command is not enabled.");
                return true;
            }

            OfflinePlayer player;
            switch (args.length) {
                case 1:
                    player = this.app.getServer().getPlayer(args[0]);
                    if (player == null) {
                        this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                    }
                    break;

                default:
                    player = null;
                    break;
            }

            if (player == null) {
                this.app.getConsole().warn("Invalid player name.");
                return true;
            }

            this.app.getConsole().info(String.format("%s's balance: £%,.2f", player.getName(), this.app.getEconomyManager().getBalance(player)));
        }
        return true;
    }
}
