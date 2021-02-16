package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Main.DCEPlugin;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for editing (adding or removing) cash to a player
 */
public class SetBal implements CommandExecutor {
    private DCEPlugin app;
    private String usage = "/setbal <username> <amount> or /setbal <amount>";

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
        if (!(this.app.getConfig().getBoolean(this.app.getConf().strComSetBal))) {
            this.app.getCon().severe(from, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command <amount> - applies ammount to self
        // command <player> <amount> - applies amount to player
        Player to = null;
        OfflinePlayer toOff = null;
        Double amount = null;

        switch (args.length) {
            case 1:
                // use case #1
                to = from;
                amount = this.app.getEco().getDouble(args[0]);
                break;

            case 2:
                // use case #2
                to = this.app.getServer().getPlayer(args[0]);
                amount = this.app.getEco().getDouble(args[1]);
                if (to == null) {
                    toOff = this.app.getOfflinePlayer(args[0], false);
                }
                break;

            default:
                // Incorrect number of args
                this.app.getCon().usage(from, "Incorrect number of arguments.", usage);
                return true;
        }

        // Ensure to player exists
        if (to == null && toOff == null){
            this.app.getCon().usage(from, "Invalid player name.", usage);
            return true;
        }

        // Ensure amount is not null
        if (amount == null) {
            this.app.getCon().usage(from, "Incorrect amount.", usage);
            return true;
        }

        // Set cash
        EconomyResponse response = null;
        String toName = null;
        if (!(to == null)) {
            response = this.app.getEco().setCash(to, amount);
            toName = to.getName();
        } else {
            response = this.app.getEco().setCash(toOff, amount);
            toName = toOff.getName();
        }

        double balance = this.app.getEco().round(response.balance);

        // Response messages
        switch(response.type) {
            case SUCCESS:
                // If to != from, respond.
                if (!(to == from)) {
                    this.app.getCon().info(from, "You set " + toName + "'s balance to £" + balance);
                }

                // If online send message
                if (!(to == null)) {
                    this.app.getCon().info(to, "Your balance was set to £" + balance + " by " + from.getName());

                // If offline --
                } else {
                    // Perhaps send an ingame mail message to offlinePlayer ¯\_(ツ)_/¯
                }

                // Console feedback
                this.app.getCon().info(from.getName() + " set " + toName + "'s balance to £" + balance);
                break;

            case FAILURE:
                this.app.getCon().usage(from, response.errorMessage, usage);

            default:
                this.app.getCon().warn("Balance Set error (" + from.getName() + "-->" + toName + "): " + response.errorMessage);
        }

        // Graceful exit
        return true;
    }
}

