package EDGRRRR.DCE.Main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import EDGRRRR.DCE.Main.App;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * Command executor for editing (adding or removing) cash to a player
 */
public class EditBal implements CommandExecutor {
    private App app;
    private String usage = "/editbal <username> <amount> or /editbal <amount>";

    public EditBal(App app) {
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
        // command <amount> - applies amount to self
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

        EconomyResponse response = null;
        String transType = null;
        double oldAmount = app.getEco().getBalance(to);
        // Edit cash
        if (amount > 0){
            response = app.getEco().addCash(to, amount);
            transType = "+";
        } else if (amount < 0) {
            response = app.getEco().remCash(to, -amount);
            transType = "-";
        } else {
            response = new EconomyResponse(amount, app.getEco().getBalance(to), ResponseType.FAILURE, "No amount to add or remove.");
        }

        if (response.type == ResponseType.SUCCESS) {
            if (!(from == to)) {
                app.getCon().info(from, "You have edited " + to.getName() + "'s balance. £" + oldAmount + transType + " £" + response.amount + " --> £" + response.balance);
            }
            app.getCon().info(to, "Your balance was edited by " + from.getName() + ". £" + oldAmount + transType + " £" + response.amount + " --> £" + response.balance);
            app.getCon().info("Edit Balance: " + from.getName() + " -->  £" + response.amount + " -->" + to.getName() + "(£" + app.getEco().getBalance(to) + ")");

        } else {
            app.getCon().warn(from, "An issue occurred. " + to.getName() + "'s balance remains £" + response.balance);
            app.getCon().severe("Edit Balance error: " + response.errorMessage);
        }

        // Graceful exit
        return true;
    }
}

