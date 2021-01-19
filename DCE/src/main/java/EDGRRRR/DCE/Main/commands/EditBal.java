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
public class EditBal implements CommandExecutor {
    private App app;

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

        // Ensure two or more args
        if (!(args.length == 2)) {
            app.getCon().warn(from, "Incorrect usage, see¬");
            return false;
        }

        // String type of transaction
        String transType = "";
        // First arg should be the <to> players name
        String toName = args[0];
        // Second arg should be the <amount>
        double amount = Double.parseDouble(args[1]);

        // Get player by <to> name
        Player to = app.getServer().getPlayer(toName);

        // Ensure player found or just not null in general.
        if (to == null) {
            app.getCon().warn(from, "Incorrect player name '" + toName + "', see¬");
            return false;
        }
        
        // If amount is negative, will removeCash and set amount to -amount (since removing x cash deducts x from y. deducting -x from y adds x to y.)
        // If amount is positive, will deposit cash to account
        EconomyResponse response = null;
        if (amount >= 0) {
            transType = "deposited";
            response = app.getEco().addCash(to, amount);
        } else {
            amount = -amount;
            transType = "withdrawn";
            response = app.getEco().remCash(to, amount);
        }   
        
        if (response.transactionSuccess() == true) {
            // Messages
            app.getCon().info(from, "You have " + transType + " £" + response.amount + " to " + to.getName() + "'s account.");
            app.getCon().info(to, "£" + response.amount + " was " + transType + " to your account. Your new balance is £" + response.balance);
        } else {
            app.getCon().warn(from, "An issue occurred. " + toName + "'s balance remains £" + response.balance);
        }        

        // Graceful exit
        return true;
    }
}

