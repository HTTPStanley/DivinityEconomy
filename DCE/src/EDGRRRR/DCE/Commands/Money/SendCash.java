package EDGRRRR.DCE.Commands.Money;

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
        boolean playerIsOffline = false;
        double amount;
        double minSendAmount = this.app.getEconomyManager().minSendAmount;

        switch (args.length) {
            case 2:
                // Get online player
                to = this.app.getServer().getPlayer(args[0]);
                amount = Math.getDouble(args[1]);
                // If they aren't online or don't exist.
                if (to == null) {
                    to = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                    playerIsOffline = true;
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
            // Ensure amount was parsed
            // Ensure amount is greater than min send amount.
            if (amount < 1 && amount < minSendAmount) {
                this.app.getConsoleManager().usage(from, "Invalid amount, needs to be greater than £" + minSendAmount, usage);

            } else {

                EconomyResponse response;
                if (to == from) {
                    this.app.getConsoleManager().usage(from, "You can't send money to yourself (╯°□°）╯︵ ┻━┻", usage);

                } else {
                    response = this.app.getEconomyManager().sendCash(from, to, amount);
                    double amountSent = this.app.getEconomyManager().round(response.amount);
                    double balance = this.app.getEconomyManager().round(this.app.getEconomyManager().getBalance(to));

                    switch (response.type) {
                        case SUCCESS:
                            this.app.getConsoleManager().info(from, "You sent £" + amountSent + " to " + to.getName() + ". New Balance: £" + this.app.getEconomyManager().round(this.app.getEconomyManager().getBalance(from)));
                            if (!playerIsOffline) {
                                this.app.getConsoleManager().info((Player) to, "You received £" + amountSent + " from " + from.getName() + ". New Balance: £" + balance);
                            } else {
                                String message = "You received £<roundedAmount> from <sourceName> <daysAgo> days ago. New Balance: £<roundedBalance>";
                                Calendar date = Calendar.getInstance();
                                String sourceUUID = from.getUniqueId().toString();
                                MailList userMail = this.app.getMailManager().getMailList(to);
                                Mail mail = userMail.createMail(amount, balance, message, date, sourceUUID, false);
                                this.app.getConsoleManager().debug("Created mail(" + mail.getID() + ") for " + to.getName());
                            }
                            this.app.getConsoleManager().info(from.getName() + " sent £" + amountSent + " to " + to.getName());
                            break;

                        case FAILURE:
                            this.app.getConsoleManager().usage(from, response.errorMessage, usage);

                        default:
                            this.app.getConsoleManager().warn("Transaction error (" + from.getName() + "-->" + to.getName() + "): " + response.errorMessage);
                    }
                }
            }
        }

        // Graceful exit
        return true;
    }
}
