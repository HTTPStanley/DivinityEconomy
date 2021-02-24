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
public class SetBal implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/setbal <username> <amount> | /setbal <amount>";

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
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComSetBal))) {
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

            // Set cash
            EconomyResponse response;
            String toName;
            if (!(to == null)) {
                response = this.app.getEconomyManager().setCash(to, amount);
                toName = to.getName();
            } else {
                response = this.app.getEconomyManager().setCash(toOff, amount);
                toName = toOff.getName();
            }

            double balance = this.app.getEconomyManager().round(response.balance);

            // Response messages
            switch (response.type) {
                case SUCCESS:
                    // If to != from, respond.
                    if (!(to == from)) {
                        this.app.getConsoleManager().info(from, "You set " + toName + "'s balance to £" + balance);
                    }

                    // If online send message
                    if (!(to == null)) {
                        this.app.getConsoleManager().info(to, "Your balance was set to £" + balance + " by " + from.getName());

                        // If offline --
                    } else {
                        // Perhaps send an ingame mail message to offlinePlayer ¯\_(ツ)_/¯
                    }

                    // Console feedback
                    this.app.getConsoleManager().info(from.getName() + " set " + toName + "'s balance to £" + balance);
                    break;

                case FAILURE:
                    this.app.getConsoleManager().usage(from, response.errorMessage, usage);

                default:
                    this.app.getConsoleManager().warn("Balance Set error (" + from.getName() + "-->" + toName + "): " + response.errorMessage);
            }
        }
        return true;
    }
}

