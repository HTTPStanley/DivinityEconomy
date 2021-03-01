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
public class EditBal implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/editbal <username> <amount> | /editbal <amount>";

    public EditBal(DCEPlugin app) {
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
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComEditBal))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        OfflinePlayer to = null;
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

            // Ensure amount is not null
            if (amount < 1) {
                this.app.getConsoleManager().usage(from, "Incorrect amount.", usage);
            } else {
                // Edit cash
                EconomyResponse response;
                if (amount > 0) {
                    response = this.app.getEconomyManager().addCash(to, amount);
                } else {
                    response = this.app.getEconomyManager().remCash(to, -amount);
                }

                double roundedCost = this.app.getEconomyManager().round(response.amount);
                double roundedBalance = this.app.getEconomyManager().round(response.balance);


                // Response messages
                switch (response.type) {
                    case SUCCESS:
                        // If to != from, respond.
                        if (!(to == from)) {
                            this.app.getConsoleManager().info(from, "You changed " + to.getName() + "'s roundedBalance by £" + roundedCost + " to £" + roundedBalance);
                        }

                        // If online send message
                        if (!playerIsOffline) {
                            this.app.getConsoleManager().info((Player) to, from.getName() + "Changed your roundedBalance by £" + roundedCost + " to £" + roundedBalance);

                            // If offline --
                        } else {
                            String message = "You received £<roundedAmount> from <sourceName> <daysAgo> days ago. New Balance: £<roundedBalance>";
                            Calendar date = Calendar.getInstance();
                            String sourceUUID = from.getUniqueId().toString();
                            MailList userMail = this.app.getMailManager().getMailList(to);
                            Mail mail = userMail.createMail(response.amount, response.balance, message, date, sourceUUID, false);
                            this.app.getConsoleManager().debug("Created mail(" + mail.getID() + ") for " + to.getName());
                        }

                        // Console feedback
                        this.app.getConsoleManager().info(from.getName() + "changed " + to.getName() + "'s roundedBalance by £" + roundedCost + " to £" + roundedBalance);
                        break;

                    case FAILURE:
                        this.app.getConsoleManager().usage(from, response.errorMessage, usage);

                    default:
                        this.app.getConsoleManager().warn("Balance Edit error (" + from.getName() + "-->" + to.getName() + "): " + response.errorMessage);
                }
            }
        }
        return true;
    }
}

