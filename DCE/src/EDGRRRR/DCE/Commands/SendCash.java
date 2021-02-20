package EDGRRRR.DCE.Commands;

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
        if (!(this.app.getConfig().getBoolean(this.app.getConf().strComSendCash))) {
            this.app.getCon().severe(from, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command <player> <amount>
        Player to;
        OfflinePlayer toOff = null;
        double amount;
        double minSendAmount = this.app.getConfig().getDouble(app.getConf().strEconMinSendAmount);

        switch (args.length) {
            case 2:
                // Get online player
                to = this.app.getServer().getPlayer(args[0]);
                amount = this.app.getEco().getDouble(args[1]);
                // If they aren't online or don't exist. Do the dirty offline call.
                if (to == null) {
                    // Naughty naughty boy - doesn't allow fetching of non-seen players.
                    toOff = this.app.getOfflinePlayer(args[0], false);
                }
                break;

            default:
                this.app.getCon().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        // Ensure online or offline player exists.
        if (to == null && toOff == null) {
            this.app.getCon().usage(from, "Invalid player name.", usage);
            return true;
        }
        // Ensure amount was parsed
        // Ensure amount is greater than min send amount.
        if (amount < 1) {
            this.app.getCon().usage(from, "Invalid amount.", usage);
            return true;
        } else if (amount < minSendAmount) {
            this.app.getCon().usage(from, "Invalid amount, needs to be greater than £" + minSendAmount, usage);
            return true;
        }


        EconomyResponse response;
        String toName;
        if (!(to == null)) {
            if (to == from) {
                this.app.getCon().usage(from, "You can't send money to yourself (╯°□°）╯︵ ┻━┻", usage);
                return true;
            } else {
                response = this.app.getEco().sendCash(from, to, amount);
                toName = to.getName();
            }
        } else {
            response = this.app.getEco().sendCash(from, toOff, amount);
            toName = toOff.getName();
        }

        double cost = this.app.getEco().round(response.amount);


        switch(response.type) {
            case SUCCESS:
                    this.app.getCon().info(from, "You sent £" + cost + " to " + toName + ". New Balance: £" + this.app.getEco().round(this.app.getEco().getBalance(from)));
                if (!(to == null)) {
                    this.app.getCon().info(to, "You received £" + cost + " from " + from.getName() + ". New Balance: £" + this.app.getEco().round(this.app.getEco().getBalance(to)));
                } else {
                    // Perhaps send an ingame mail message to offlinePlayer ¯\_(ツ)_/¯
                }
                this.app.getCon().info(from.getName() + " sent £" + cost + " to " + toName);
                break;

            case FAILURE:
                this.app.getCon().usage(from, response.errorMessage, usage);

            default:
                this.app.getCon().warn("Transaction error (" + from.getName() + "-->" + toName + "): " + response.errorMessage);
        }

        // Graceful exit
        return true;
    }
}
