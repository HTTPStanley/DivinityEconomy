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
            case 0:
                // No args
                app.getCon().usage(from, "Not enough arguments.", usage);
                return true;

            case 1:
                // use case #1
                to = from;
                amount = Double.parseDouble(args[0]);
                break;

            case 2:
                // use case #2
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

        // Initializing before because VSCODE IS FUCKING WHINING ABOUT IT.
        EconomyResponse response = null;
        String transType = null;
        // Edit cash
        if (amount > 0){
            response = app.getEco().addCash(to, amount);
            transType = "deposited to";
        } else if (amount < 0) {
            response = app.getEco().remCash(to, amount);
            transType = "withdrawn from";
        } else {
            response = new EconomyResponse(amount, app.getEco().getBalance(to), ResponseType.FAILURE, "No amount to add or remove.");
        }

        if (response.transactionSuccess() == true) {
            if (!(from == to)) {
                app.getCon().info(from, "You have " + transType + to.getName() + "'s account. New Balance: £" + response.balance);
            }
            app.getCon().info(to, "Your balance was " + transType + " by " + from.getName() + " the amount of £" + response.amount + ". New Balance: £" + response.balance);
            app.getCon().info("Transaction: " + from.getName() + "(£" + app.getEco().getBalance(from) + ") -->  £" + response.amount + " -->" + to.getName() + "(£" + app.getEco().getBalance(to) + ")");

        } else {
            app.getCon().warn(from, "An issue occurred. " + to.getName() + "'s balance remains £" + response.balance);
            app.getCon().warn("Transaction error: " + response.errorMessage);
        }

        // Graceful exit
        return true;
    }
}

