package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.utils.Converter;
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
                amount = Converter.getDouble(args[0]);
                break;

            case 2:
                // use case #2
                receiver = getMain().getPlayMan().getPlayer(args[0], false);
                amount = Converter.getDouble(args[1]);
                break;

            default:
                // Incorrect number of args
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (receiver == null) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidPlayerName.logLevel, LangEntry.GENERIC_InvalidPlayerName.get(getMain()));
            return true;

        }

        // Edit cash
        EconomyResponse response;
        double startingBalance = getMain().getEconMan().getBalance(receiver);
        if (amount > 0) {
            response = getMain().getEconMan().addCash(receiver, amount);
        } else {
            response = getMain().getEconMan().remCash(receiver, -amount);
        }

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            getMain().getConsole().logBalance(sender, receiver, startingBalance, response.balance, LangEntry.BALANCE_ChangedByPlayer.get(getMain(), sender.getName()));

        } else {
            getMain().getConsole().logFailedBalance(sender, receiver, response.errorMessage);
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
                receiver = getMain().getPlayMan().getPlayer(args[0], false);
                amount = Converter.getDouble(args[1]);
                break;

            default:
                // Incorrect number of args
                getMain().getConsole().usage(LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (receiver == null) {
            getMain().getConsole().send(LangEntry.GENERIC_InvalidPlayerName.logLevel, LangEntry.GENERIC_InvalidPlayerName.get(getMain()));
            return true;

        }

        // Edit cash
        EconomyResponse response;
        double startingBalance = getMain().getEconMan().getBalance(receiver);
        if (amount > 0) {
            response = getMain().getEconMan().addCash(receiver, amount);
        } else {
            response = getMain().getEconMan().remCash(receiver, -amount);
        }

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            getMain().getConsole().logBalance(null, receiver, startingBalance, response.balance, LangEntry.BALANCE_ChangedByConsole.get(getMain()));

        } else {
            getMain().getConsole().logFailedBalance(null, receiver, response.errorMessage);
        }

        return true;
    }
}
