package EDGRRRR.DCE.Main.commands;

import static EDGRRRR.DCE.Main.App.get;
import static EDGRRRR.DCE.Main.App.getCon;
import static EDGRRRR.DCE.Main.App.getEco;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for editing (adding or removing) cash to a player
 */
public class EditCash implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;

        // Ensure two or more args
        if (!(args.length == 2)) {
            getCon().warn(from, "Incorrect usage, see¬");
            return false;
        }

        // String type of transaction
        String transType = "";
        // First arg should be the <to> players name
        String toName = args[0];
        // Second arg should be the <amount>
        Double amount = Double.parseDouble(args[1]);

        // Get player by <to> name
        Player to = get().getServer().getPlayer(toName);

        // Ensure player found or just not null in general.
        if (to == null) {
            getCon().warn(from, "Incorrect player name '" + toName + "', see¬");
            return false;
        }
        
        // If amount is negative, will removeCash and set amount to -amount (since removing x cash deducts x from y. deducting -x from y adds x to y.)
        // If amount is positive, will deposit cash to account
        if (amount >= 0) {
            transType = "deposited";
            getEco().addCash(to, amount);
        } else {
            amount = -amount;
            transType = "withdrawn";
            getEco().remCash(to, amount);
        }

        // Messages
        getCon().info(from, "You have " + transType + " £" + amount + " to " + to.getName() + "'s account.");
        getCon().info(to, "£" + amount + " was " + transType + " to your account. Your new balance is £" + getEco().getBalance(to));

        // Graceful exit
        return true;
    }
}

