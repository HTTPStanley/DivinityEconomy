package EDGRRRR.DCE.Commands.Admin;

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
 * Command executor for editing (adding or removing) cash to a player
 */
public class SetBal implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/setbal <username> <amount> | /setbal <amount>";

    public SetBal(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComSetBal))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        OfflinePlayer to;
        boolean playerIsOffline = false;
        double amount;

        switch (args.length) {
            case 1:
                // use case #1
                to = from;
                amount = Math.getDouble(args[0]);
                break;

            case 2:
                // use case #2
                to = this.app.getServer().getPlayer(args[0]);
                amount = Math.getDouble(args[1]);
                if (to == null) {
                    to = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                    playerIsOffline = true;
                }
                break;

            default:
                // Incorrect number of args
                this.app.getConsoleManager().usage(from, "Incorrect number of arguments.", usage);
                return true;
        }

        // Ensure to player exists
        if (to == null) {
            this.app.getConsoleManager().usage(from, "Invalid player name.", usage);
        } else {

            EconomyResponse response = this.app.getEconomyManager().setCash(to, amount);
            double roundedBalance = this.app.getEconomyManager().round(response.balance);

            // Response messages
            switch (response.type) {
                case SUCCESS:
                    // If to != from, respond.
                    if (!(to == from)) {
                        this.app.getConsoleManager().info(from, "You set " + to.getName() + "'s roundedBalance to £" + roundedBalance);
                    }

                    // If online send message
                    if (!playerIsOffline) {
                        this.app.getConsoleManager().info((Player) to, "Your balance was set to £" + roundedBalance + " by " + from.getName());

                        // If offline --
                    } else {
                        String message = "You received £<roundedAmount> from <sourceName> <daysAgo> days ago. New Balance: £<roundedBalance>";
                        Calendar date = Calendar.getInstance();
                        String sourceUUID = from.getUniqueId().toString();
                        MailList userMail = this.app.getMailManager().getMailList(to);
                        Mail mail = userMail.createMail(amount, roundedBalance, message, date, sourceUUID, false);
                        this.app.getConsoleManager().debug("Created mail(" + mail.getID() + ") for " + to.getName());
                    }

                    // Console feedback
                    this.app.getConsoleManager().info(from.getName() + " set " + to.getName() + "'s balance to £" + roundedBalance);
                    break;

                case FAILURE:
                    this.app.getConsoleManager().usage(from, response.errorMessage, usage);

                default:
                    this.app.getConsoleManager().warn("Balance Set error (" + from.getName() + "-->" + to.getName() + "): " + response.errorMessage);
            }
        }
        return true;
    }
}

