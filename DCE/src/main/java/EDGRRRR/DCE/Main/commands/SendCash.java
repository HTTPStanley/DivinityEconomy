package EDGRRRR.DCE.Main.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import EDGRRRR.DCE.Main.App;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Command executor for sending cashing between players
 */
public class SendCash implements CommandExecutor {
    private App app;
    private String usage = "/sendcash <username> <amount>";

    public SendCash(App app) {
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

        // Use case scenarios
        // command <player> <amount>
        Player to = null;
        OfflinePlayer toOff = null;
        Double amount = null;

        switch (args.length) {
            case 2:
                // Get online player
                to = app.getServer().getPlayer(args[0]);
                amount = app.getEco().getDouble(args[1]);
                // If they aren't online or don't exist. Do the dirty offline call.
                if (to == null) {
                    // Naughty naughty boy - doesn't allow fetching of non-seen players.
                    toOff = app.getOfflinePlayer(args[0], false);
                }
                break;

            default:
                app.getCon().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        // Ensure online or offline player exists.
        if (to == null && toOff == null) {
            app.getCon().usage(from, "Invalid player name.", usage);
            return true;
        }
        // Ensure amount was parsed
        // Ensure amount is greater than min send amount.
        if (amount == null) {
            app.getCon().usage(from, "Invalid amount.", usage);
            return true;
        } else if (amount < app.getEco().minSendAmount) {
            app.getCon().usage(from, "Invalid amount, needs to be greater than £" + app.getEco().minSendAmount, usage);
            return true;
        }


        EconomyResponse response = null;
        String toName = null;
        if (!(to == null)) {
            if (to == from) {
                app.getCon().usage(from, "You can't send money to yourself (╯°□°）╯︵ ┻━┻", usage);
                return true;
            } else {
                response = app.getEco().sendCash(from, to, amount);
                toName = to.getName();
            }
        } else {
            response = app.getEco().sendCash(from, toOff, amount);
            toName = toOff.getName();
        }


        switch(response.type) {
            case SUCCESS:
                app.getCon().info(from, "You sent £" + response.amount + " to " + toName + ". New Balance: £" + app.getEco().getBalance(from));
                if (!(to == null)) {
                    app.getCon().info(to, "You received £" + response.amount + " from " + from.getName() + ". New Balance: £" + app.getEco().getBalance(to));
                } else {
                    // Perhaps send an ingame mail message to offlinePlayer ¯\_(ツ)_/¯
                }
                app.getCon().info(from.getName() + " sent £" + response.amount + " to " + toName);
                break;

            case FAILURE:
                app.getCon().usage(from, response.errorMessage, usage);            

            default:
                app.getCon().warn("Transaction error (" + from.getName() + "-->" + toName + "): " + response.errorMessage);
        }

        // Graceful exit
        return true;
    } 
}
