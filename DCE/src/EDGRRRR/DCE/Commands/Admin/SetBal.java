package EDGRRRR.DCE.Commands.Admin;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for editing (adding or removing) cash to a player
 */
public class SetBal implements CommandExecutor {
    private final DCEPlugin app;
    // The command usage
    private final String usage = "/setbal <username> <amount> | /setbal <amount>";

    /**
     * Constructor
     * @param app - The main class
     */
    public SetBal(DCEPlugin app) {
        this.app = app;
    }

    /**
     * Called everytime the command is called
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }

        // The command sender
        Player player1 = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComSetBal))) {
            this.app.getConsoleManager().severe(player1, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        OfflinePlayer player2;
        double amount;

        switch (args.length) {
            case 1:
                // use case #1
                player2 = player1;
                amount = Math.getDouble(args[0]);
                break;

            case 2:
                // use case #2
                player2 = this.app.getServer().getPlayer(args[0]);
                amount = Math.getDouble(args[1]);
                if (player2 == null) {
                    player2 = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                }
                break;

            default:
                // Incorrect number of args
                this.app.getConsoleManager().usage(player1, "Incorrect number of arguments.", usage);
                return true;
        }

        // Ensure to player exists
        if (player2 == null) {
            this.app.getConsoleManager().usage(player1, "Invalid player name.", usage);

        } else {
            EconomyResponse response = this.app.getEconomyManager().setCash(player2, amount);
            double startingBalance = this.app.getEconomyManager().getBalance(player2);

            // Response messages
            if (response.transactionSuccess()) {
                // Handles console, player and mail
                this.app.getConsoleManager().logBalance(player1, player2, response.balance, startingBalance, String.format("balance set by %s", player1.getName()));
            } else {
                // Handles console, player and mail
                this.app.getConsoleManager().logFailedBalance(player1, player2, startingBalance, response.balance, response.errorMessage);
            }
        }
        return true;
    }
}

