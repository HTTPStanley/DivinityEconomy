package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.math.Math;
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
                receiver = this.getMain().getPlayerManager().getOfflinePlayer(args[0], false);
                amount = Math.getDouble(args[1]);
                break;

            default:
                // Incorrect number of args
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (receiver == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;

        }

        // Edit cash
        EconomyResponse response;
        double startingBalance = this.getMain().getEconomyManager().getBalance(receiver);
        if (amount > 0) {
            response = this.getMain().getEconomyManager().addCash(receiver, amount);
        } else {
            response = this.getMain().getEconomyManager().remCash(receiver, -amount);
        }

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            this.getMain().getConsole().logBalance(sender, receiver, startingBalance, response.balance, String.format("%s changed your balance", sender.getName()));

        } else {
            this.getMain().getConsole().logFailedBalance(sender, receiver, response.errorMessage);
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
                receiver = this.getMain().getPlayerManager().getOfflinePlayer(args[0], false);
                amount = Math.getDouble(args[1]);
                break;

            default:
                // Incorrect number of args
                this.getMain().getConsole().usage(CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (receiver == null) {
            this.getMain().getConsole().send(CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;

        }

        // Edit cash
        EconomyResponse response;
        double startingBalance = this.getMain().getEconomyManager().getBalance(receiver);
        if (amount > 0) {
            response = this.getMain().getEconomyManager().addCash(receiver, amount);
        } else {
            response = this.getMain().getEconomyManager().remCash(receiver, -amount);
        }

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            this.getMain().getConsole().logBalance(null, receiver, startingBalance, response.balance, "CONSOLE changed your balance");

        } else {
            this.getMain().getConsole().logFailedBalance(null, receiver, response.errorMessage);
        }

        return true;
    }
}
