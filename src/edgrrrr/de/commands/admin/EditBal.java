package edgrrrr.de.commands.admin;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import edgrrrr.de.config.Setting;
import edgrrrr.de.math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A command for editing the balances of players.
 */
public class EditBal extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public EditBal(DEPlugin app) {
        super(app, "editbal", true, Setting.COMMAND_EDIT_BALANCE_ENABLE_BOOLEAN);
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
        // The command receiver
        OfflinePlayer receiver;
        double amount;

        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        switch (args.length) {
            case 1:
                // use case #1
                receiver = sender;
                amount = Math.getDouble(args[0]);
                break;

            case 2:
                // use case #2
                receiver = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                amount = Math.getDouble(args[1]);
                break;

            default:
                // Incorrect number of args
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (receiver == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;

        }

        // Edit cash
        EconomyResponse response;
        double startingBalance = this.app.getEconomyManager().getBalance(receiver);
        if (amount > 0) {
            response = this.app.getEconomyManager().addCash(receiver, amount);
        } else {
            response = this.app.getEconomyManager().remCash(receiver, -amount);
        }

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            this.app.getConsole().logBalance(sender, receiver, startingBalance, response.balance, String.format("%s changed your balance", sender.getName()));

        } else {
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
        // The command receiver
        OfflinePlayer receiver;
        double amount;

        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        switch (args.length) {
            case 2:
                // use case #2
                receiver = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                amount = Math.getDouble(args[1]);
                break;

            default:
                // Incorrect number of args
                this.app.getConsole().usage(CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (receiver == null) {
            this.app.getConsole().send(CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;

        }

        // Edit cash
        EconomyResponse response;
        double startingBalance = this.app.getEconomyManager().getBalance(receiver);
        if (amount > 0) {
            response = this.app.getEconomyManager().addCash(receiver, amount);
        } else {
            response = this.app.getEconomyManager().remCash(receiver, -amount);
        }

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            this.app.getConsole().logBalance(null, receiver, startingBalance, response.balance, "CONSOLE changed your balance");

        } else {
            this.app.getConsole().logFailedBalance(null, receiver, response.errorMessage);
        }

        return true;
    }
}
