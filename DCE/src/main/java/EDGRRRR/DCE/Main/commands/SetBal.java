package EDGRRRR.DCE.Main.commands;

import EDGRRRR.DCE.Main.App;
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
    private App app;
    private String usage = "/setbal <username> <amount> or /setbal <amount>";

    public SetBal(App app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;

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
                amount = app.getEco().getDouble(args[0]);
                break;

            case 2:
                // use case #2
                to = app.getServer().getPlayer(args[0]);
                amount = app.getEco().getDouble(args[1]);
                if (to == null) {
                    toOff = app.getOfflinePlayer(args[0], false);
                }
                break;

            default:
                // Incorrect number of args
                app.getCon().usage(from, "Incorrect number of arguments.", usage);
                break;
        }

        // Ensure to player exists
        if (to == null && toOff == null){
            app.getCon().usage(from, "Invalid player name.", usage);
            return true;
        }
        
        // Ensure amount is not null
        if (amount == null) {
            app.getCon().usage(from, "Incorrect amount.", usage);
            return true;
        }

        // Set cash
        EconomyResponse response = null;
        String toName = null;
        if (!(to == null)) {
            response = app.getEco().setCash(to, amount);
            toName = to.getName();            
        } else {
            response = app.getEco().setCash(toOff, amount);
            toName = toOff.getName();
        }


        // Response messages
        switch(response.type) {
            case SUCCESS:
                // If to != from, respond.
                if (!(to == from)) {
                    app.getCon().info(from, "You set " + toName + "'s balance to £" + response.balance);
                }   

                // If online send message
                if (!(to == null)) {
                    app.getCon().info(to, "Your balance was set to £" + response.balance + " by " + from.getName());

                // If offline --
                } else {
                    // Perhaps send an ingame mail message to offlinePlayer ¯\_(ツ)_/¯
                }

                // Console feedback
                app.getCon().info(from.getName() + " set " + toName + "'s balance to £" + response.balance);
                break;
            
            case FAILURE:
                app.getCon().usage(from, response.errorMessage, usage);            

            default:
                app.getCon().warn("Balance Set error (" + from.getName() + "-->" + toName + "): " + response.errorMessage);
        } 

        // Graceful exit
        return true;
    }
}

