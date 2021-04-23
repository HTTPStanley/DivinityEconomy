package edgrrrr.dce.commands.money;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.response.EconomyTransferResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for sending cash between players
 */
public class SendCash implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public SendCash(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("sendcash");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }
        // Cast player
        Player player1 = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_SEND_CASH_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player1, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command <player> <amount>
        OfflinePlayer player2;
        double amount;

        switch (args.length) {
            case 2:
                // Get online player
                player2 = this.app.getServer().getPlayer(args[0]);
                amount = Math.getDouble(args[1]);
                // If they aren't online or don't exist.
                if (player2 == null) {
                    player2 = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                }
                break;

            default:
                this.app.getConsole().usage(player1, "Invalid number of arguments.", this.help.getUsages());
                return true;
        }

        // Ensure online or offline player exists.
        if (player2 == null) {
            this.app.getConsole().usage(player1, "Invalid player name.", this.help.getUsages());

        } else {
            EconomyTransferResponse response = this.app.getEconomyManager().sendCash(player1, player2, amount);

            // Handles console, message and mail
            if (response.responseType == EconomyResponse.ResponseType.SUCCESS) {
                this.app.getConsole().logTransfer(player1, player2, amount);
            } else {
                this.app.getConsole().logFailedTransfer(player1, player2, amount, response.errorMessage);
            }
        }

        // Graceful exit
        return true;
    }
}
