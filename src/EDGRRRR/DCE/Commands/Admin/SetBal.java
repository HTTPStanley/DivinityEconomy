package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommand;
import edgrrrr.dce.math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A command for setting the balance of a player
 */
public class SetBal extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public SetBal(DCEPlugin app) {
        super(app, "setbal", true, Setting.COMMAND_SET_BALANCE_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        OfflinePlayer receiver;
        double amount;

        switch (args.length) {
            case 1:
                // use case #1
                receiver = sender;
                amount = Math.getDouble(args[0]);
                break;

            case 2:
                // use case #2
                amount = Math.getDouble(args[1]);
                receiver = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                break;

            default:
                // Incorrect number of args
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (receiver == null) {
            this.app.getConsole().usage(sender, CommandResponse.InvalidPlayerName.message, this.help.getUsages());
            return true;
        }

        double startingBalance = this.app.getEconomyManager().getBalance(receiver);
        EconomyResponse response = this.app.getEconomyManager().setCash(receiver, amount);

        // Response messages
        if (response.transactionSuccess()) {
            // Handles console, player and mail
            this.app.getConsole().logBalance(sender, receiver, startingBalance, response.balance, String.format("%s set your balance", sender.getName()));

        } else {
            // Handles console, player and mail
            this.app.getConsole().logFailedBalance(sender, receiver, response.errorMessage);
        }
        return true;
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        OfflinePlayer receiver;
        double amount;

        switch (args.length) {
            case 2:
                // use case #2
                amount = Math.getDouble(args[1]);
                receiver = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                break;

            default:
                // Incorrect number of args
                this.app.getConsole().send(CommandResponse.InvalidNumberOfArguments.defaultLogLevel, CommandResponse.InvalidNumberOfArguments.message);
                return true;
        }

        // Ensure to player exists
        if (receiver == null) {
            this.app.getConsole().send(CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;
        }

        double startingBalance = this.app.getEconomyManager().getBalance(receiver);
        EconomyResponse response = this.app.getEconomyManager().setCash(receiver, amount);

        // Response messages
        if (response.transactionSuccess()) {
            // Handles console, player and mail
            this.app.getConsole().logBalance(null, receiver, startingBalance, response.balance, "CONSOLE set your balance");

        } else {
            // Handles console, player and mail
            this.app.getConsole().logFailedBalance(null, receiver, response.errorMessage);
        }
        return true;
    }
}

