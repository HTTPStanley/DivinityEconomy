package edgrrrr.dce.commands.admin;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for editing (adding or removing) cash to a player
 */
public class EditBal implements CommandExecutor {
    private final DCEPlugin app;
    // This is the usage for this command
    private final String usage = "/editbal <username> <amount> | /editbal <amount>";

    /**
     * Constructor
     * @param app - The main class
     */
    public EditBal(DCEPlugin app) {
        this.app = app;
    }

    /**
     * Called whenever the command is called
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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_EDIT_BALANCE_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player1, "This command is not enabled.");
            return true;
        }


        // The command receiver
        OfflinePlayer player2 = null;
        double amount = 0;

        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
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

                // If player is offline, get offline player and flag player as offline.
                if (player2 == null) {
                    player2 = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                }
                break;

            default:
                // Incorrect number of args
                DCEPlugin.CONSOLE.usage(player1, "Incorrect number of arguments.", usage);
                return true;
        }

        // Ensure to player exists
        if (player2 == null) {
            DCEPlugin.CONSOLE.usage(player1, "Invalid player name.", usage);
        } else {

            // Edit cash
            EconomyResponse response;
            double startingBalance = this.app.getEconomyManager().getBalance(player2);
            if (amount > 0) {
                response = this.app.getEconomyManager().addCash(player2, amount);
            } else {
                response = this.app.getEconomyManager().remCash(player2, -amount);
            }

            // Handles sender, receiver, message, mail and console log
            if (response.transactionSuccess()) {
                DCEPlugin.CONSOLE.logBalance(player1, player2, startingBalance, response.balance, String.format("%s changed your balance", player1.getName()));
            } else {

                DCEPlugin.CONSOLE.logFailedBalance(player1, player2, startingBalance, response.balance, response.errorMessage);
            }
        }

        return true;
    }
}

