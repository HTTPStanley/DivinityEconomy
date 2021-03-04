package EDGRRRR.DCE.Commands.Money;

import EDGRRRR.DCE.Economy.EconomyTransferResponse;
import EDGRRRR.DCE.Mail.Mail;
import EDGRRRR.DCE.Mail.MailList;
import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;

/**
 * Command executor for sending cashing between players
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
        Player from = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComSendCash))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command <player> <amount>
        OfflinePlayer to;
        double amount;

        switch (args.length) {
            case 2:
                // Get online player
                to = this.app.getServer().getPlayer(args[0]);
                amount = Math.getDouble(args[1]);
                // If they aren't online or don't exist.
                if (to == null) {
                    to = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                }
                break;

            default:
                this.app.getConsoleManager().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        // Ensure online or offline player exists.
        if (to == null) {
            this.app.getConsoleManager().usage(from, "Invalid player name.", usage);

        } else {
            if (to == from) {
                this.app.getConsoleManager().usage(from, "You can't send money to yourself (╯°□°）╯︵ ┻━┻", usage);

            } else {
                EconomyTransferResponse response = this.app.getEconomyManager().sendCash(from, to, amount);

                // Handles console, message and mail
                if (response.responseType == EconomyResponse.ResponseType.SUCCESS) {
                    this.app.getConsoleManager().logTransfer(from, to, amount);
                } else {
                    this.app.getConsoleManager().logFailedTransfer(from, to, amount, response.errorMessage);
                }
            }
        }

        // Graceful exit
        return true;
    }
}
