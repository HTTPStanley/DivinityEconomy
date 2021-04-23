package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for clearing the balance of a player.
 */
public class ClearBal implements CommandExecutor {
    private final DCEPlugin app;
    // The usage for this command.
    private final Help help;

    /**
     * Constructor
     * @param app - The main class
     */
    public ClearBal(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("clearbal");
    }

    /**
     * onCommand is called everytime this command is called.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }

        // Player1 is the player sending the commaand
        // Player2 is the string name, which will be passed to setBal as an argument.
        Player player1 = (Player) sender;
        OfflinePlayer player2;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_CLEAR_BALANCE_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player1, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // 0 args:
        //  player2 is the caller.
        // 1 arg:
        //  player2's name is provided.
        switch (args.length) {
            // 0 args
            case 0:
                player2 = player1;
                break;

            // 1 arg
            case 1:
                player2 = this.app.getServer().getPlayer(args[0]);

                // If player is offline, get offline player.
                if (player2 == null) {
                    player2 = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                }
                break;

            // If any other number of arguments are passed.
            default:
                this.app.getConsole().usage(player1, "Incorrect number of arguments.", this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (player2 == null) {
            this.app.getConsole().usage(player1, "Invalid player name.", this.help.getUsages());
        } else {
            // Set balance to 0
            double startingBalance = this.app.getEconomyManager().getBalance(player2);
            EconomyResponse response = this.app.getEconomyManager().setCash(player2, 0);

            // Handles sender, receiver, message, mail and console log
            if (response.transactionSuccess()) {
                this.app.getConsole().logBalance(player1, player2, startingBalance, response.balance, String.format("%s changed your balance", player1.getName()));
            } else {
                this.app.getConsole().logFailedBalance(player1, player2, response.errorMessage);
            }
        }
        return true;
    }
}

