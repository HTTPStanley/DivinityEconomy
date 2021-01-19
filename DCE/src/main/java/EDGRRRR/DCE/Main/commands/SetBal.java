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
            case 0:
                // No args
                app.getCon().usage(from, "Not enough arguments.", usage);
                return true;

            case 1:
                // Use case #1
                to = from;
                amount = Double.parseDouble(args[0]);
                break;
                
            case 2:
                // Use case #2
                to = app.getServer().getPlayer(args[0]);
                amount = Double.parseDouble(args[1]);
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
            app.getCon().info(to, "Your balance was set to £" + response.balance);

        } else {
            app.getCon().warn(from, "An issue occurred. " + to.getName() + "'s balance remains £" + response.balance);
        }   

        // Graceful exit
        return true;
    }
}

