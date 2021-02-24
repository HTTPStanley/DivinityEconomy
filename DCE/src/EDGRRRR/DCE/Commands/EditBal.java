package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for editing (adding or removing) cash to a player
 */
public class EditBal implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/editbal <username> <amount> | /editbal <amount>";

    public EditBal(DCEPlugin app) {
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
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComEditBal))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
            return true;
        }

        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        Player to;
        OfflinePlayer toOff = null;
        double amount;

        switch (args.length) {
            case 1:
                // use case #1
                to = from;
                amount = Math.getDouble(args[0]);
                break;

            case 2:
                // use case #2
                to = this.app.getServer().getPlayer(args[0]);
                amount = Math.getDouble(args[1]);
                if (to == null) {
                    toOff = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                }
                break;

            default:
                // Incorrect number of args
                this.app.getConsoleManager().usage(from, "Incorrect number of arguments.", usage);
                return true;
        }

        // Ensure to player exists
        if (to == null && toOff == null) {
            this.app.getConsoleManager().usage(from, "Invalid player name.", usage);
        } else {

            // Ensure amount is not null
            if (amount < 1) {
                this.app.getConsoleManager().usage(from, "Incorrect amount.", usage);
            } else {
                // Edit cash
                EconomyResponse response = null;
                String toName = null;
                if (!(to == null) && (amount > 0)) {
                    // Online and add
                    toName = to.getName();
                    response = this.app.getEconomyManager().addCash(to, amount);
                } else if ((to == null) && (amount > 0)) {
                    // Offline and add
                    toName = toOff.getName();
                    response = this.app.getEconomyManager().addCash(toOff, amount);
                } else if (!(to == null) && (amount < 0)) {
                    // Online and remove (note the - on <amount> to invert to positive.)
                    toName = to.getName();
                    response = this.app.getEconomyManager().remCash(to, -amount);
                } else if ((to == null) && (amount < 0)) {
                    // Offline and remove (note the - on <amount> to invert to positive.)
                    toName = toOff.getName();
                    response = this.app.getEconomyManager().remCash(toOff, -amount);
                }

                double cost = this.app.getEconomyManager().round(response.amount);
                double balance = this.app.getEconomyManager().round(response.balance);


                // Response messages
                switch (response.type) {
                    case SUCCESS:
                        // If to != from, respond.
                        if (!(to == from)) {
                            this.app.getConsoleManager().info(from, "You changed " + toName + "'s balance by £" + cost + " to £" + balance);
                        }

                        // If online send message
                        if (!(to == null)) {
                            this.app.getConsoleManager().info(to, from.getName() + "Changed your balance by £" + cost + " to £" + balance);

                            // If offline --
                        } else {
                            // Perhaps send an ingame mail message to offlinePlayer ¯\_(ツ)_/¯
                        }

                        // Console feedback
                        this.app.getConsoleManager().info(from.getName() + "changed " + toName + "'s balance by £" + cost + " to £" + balance);
                        break;

                    case FAILURE:
                        this.app.getConsoleManager().usage(from, response.errorMessage, usage);

                    default:
                        this.app.getConsoleManager().warn("Balance Edit error (" + from.getName() + "-->" + toName + "): " + response.errorMessage);
                }
            }
        }
        return true;
    }
}

