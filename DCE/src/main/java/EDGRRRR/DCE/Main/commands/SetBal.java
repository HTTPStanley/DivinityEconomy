package EDGRRRR.DCE.Main.commands;

import EDGRRRR.DCE.Main.App;
import net.milkbowl.vault.economy.EconomyResponse;

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
                break;

            default:
                // Incorrect number of args
                app.getCon().usage(from, "Incorrect number of arguments.", usage);
                break;
        }

        // Ensure to player exists
        if (to == null) {
            app.getCon().usage(from, "Incorrect player name.", usage);
            return true;
        }
        
        // Ensure amount is not null
        if (amount == null) {
            app.getCon().usage(from, "Incorrect amount.", usage);
            return true;
        }
        
        // Set cash
        EconomyResponse response = app.getEco().setCash(to, amount);
        if (response.transactionSuccess() == true) {
            if (!(from == to)) {
                app.getCon().info(from, "Set " + to.getName() + "'s balance to £" + response.balance);
            }
            app.getCon().info(to, "Your balance was set to £" + response.balance + " by " + from.getName());
            app.getCon().info("Set Balance: " + from.getName() + " Set " + to.getName() + "'s balance to £" + response.balance);

        } else {
            app.getCon().warn(from, "An issue occurred. " + to.getName() + "'s balance remains £" + response.balance);
            app.getCon().severe("Set Balance error: " + response.errorMessage);
        }   

        // Graceful exit
        return true;
    }
}

