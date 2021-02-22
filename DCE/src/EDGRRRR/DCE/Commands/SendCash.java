package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Math.Math;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import EDGRRRR.DCE.Main.DCEPlugin;
import net.milkbowl.vault.economy.EconomyResponse;

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
        Player to;
        OfflinePlayer toOff = null;
        double amount;
        double minSendAmount = this.app.getConfig().getDouble(app.getConfigManager().strEconMinSendAmount);

        switch (args.length) {
            case 2:
                // Get online player
                to = this.app.getServer().getPlayer(args[0]);
                amount = Math.getDouble(args[1]);
                // If they aren't online or don't exist. Do the dirty offline call.
                if (to == null) {
                    // Naughty naughty boy - doesn't allow fetching of non-seen players.
                    toOff = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                }
                break;

            default:
                this.app.getConsoleManager().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        // Ensure online or offline player exists.
        if (to == null && toOff == null) {
            this.app.getConsoleManager().usage(from, "Invalid player name.", usage);
        } else {
            // Ensure amount was parsed
            // Ensure amount is greater than min send amount.
            if (amount < 1) {
                this.app.getConsoleManager().usage(from, "Invalid amount.", usage);
            } else if (amount < minSendAmount) {
                this.app.getConsoleManager().usage(from, "Invalid amount, needs to be greater than £" + minSendAmount, usage);
            } else {

                EconomyResponse response;
                String toName;
                if (!(to == null)) {
                    if (to == from) {
                        this.app.getConsoleManager().usage(from, "You can't send money to yourself (╯°□°）╯︵ ┻━┻", usage);
                        return true;
                    } else {
                        response = this.app.getEconomyManager().sendCash(from, to, amount);
                        toName = to.getName();
                    }
                } else {
                    response = this.app.getEconomyManager().sendCash(from, toOff, amount);
                    toName = toOff.getName();
                }

                double cost = this.app.getEconomyManager().round(response.amount);


                switch (response.type) {
                    case SUCCESS:
                        this.app.getConsoleManager().info(from, "You sent £" + cost + " to " + toName + ". New Balance: £" + this.app.getEconomyManager().round(this.app.getEconomyManager().getBalance(from)));
                        if (!(to == null)) {
                            this.app.getConsoleManager().info(to, "You received £" + cost + " from " + from.getName() + ". New Balance: £" + this.app.getEconomyManager().round(this.app.getEconomyManager().getBalance(to)));
                        } else {
                            // Perhaps send an ingame mail message to offlinePlayer ¯\_(ツ)_/¯
                        }
                        this.app.getConsoleManager().info(from.getName() + " sent £" + cost + " to " + toName);
                        break;

                    case FAILURE:
                        this.app.getConsoleManager().usage(from, response.errorMessage, usage);

                    default:
                        this.app.getConsoleManager().warn("Transaction error (" + from.getName() + "-->" + toName + "): " + response.errorMessage);
                }
            }
        }

        // Graceful exit
        return true;
    }
}
