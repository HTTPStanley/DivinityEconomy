package edgrrrr.dce.commands.money;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
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
    private final String usage = "/sendcash <username> <amount>";

    public SendCash(DCEPlugin app) {
        this.app = app;
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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_SEND_CASH_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player1, "This command is not enabled.");
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
                DCEPlugin.CONSOLE.usage(player1, "Invalid number of arguments.", usage);
                return true;
        }

        // Ensure online or offline player exists.
        if (player2 == null) {
            DCEPlugin.CONSOLE.usage(player1, "Invalid player name.", usage);

        } else {
            EconomyTransferResponse response = this.app.getEconomyManager().sendCash(player1, player2, amount);

            // Handles console, message and mail
            if (response.responseType == EconomyResponse.ResponseType.SUCCESS) {
                DCEPlugin.CONSOLE.logTransfer(player1, player2, amount);
            } else {
                DCEPlugin.CONSOLE.logFailedTransfer(player1, player2, amount, response.errorMessage);
            }
        }

        // Graceful exit
        return true;
    }
}
