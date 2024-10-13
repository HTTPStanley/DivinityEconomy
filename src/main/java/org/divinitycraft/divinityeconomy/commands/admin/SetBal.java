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
 * A command for setting the balance of a player
 */
public class SetBal extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public SetBal(DEPlugin app) {
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
                amount = Converter.getDouble(args[0]);
                break;

            case 2:
                // use case #2
                amount = Converter.getDouble(args[1]);
                receiver = getMain().getPlayMan().getPlayer(args[0], false);
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

        double startingBalance = getMain().getEconMan().getBalance(receiver);
        EconomyResponse response = getMain().getEconMan().setCash(receiver, amount);

        // Response messages
        if (response.transactionSuccess()) {
            // Handles console, player and mail
            getMain().getConsole().logBalance(sender, receiver, startingBalance, response.balance, LangEntry.BALANCE_SetByPlayer.get(getMain(), sender.getName()));

        } else {
            // Handles console, player and mail
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
        // Use case scenarios
        // command <amount> - applies amount to self
        // command <player> <amount> - applies amount to player
        OfflinePlayer receiver;
        double amount;

        switch (args.length) {
            case 2:
                // use case #2
                amount = Converter.getDouble(args[1]);
                receiver = getMain().getPlayMan().getPlayer(args[0], false);
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

        double startingBalance = getMain().getEconMan().getBalance(receiver);
        EconomyResponse response = getMain().getEconMan().setCash(receiver, amount);

        // Response messages
        if (response.transactionSuccess()) {
            // Handles console, player and mail
            getMain().getConsole().logBalance(null, receiver, startingBalance, response.balance, LangEntry.BALANCE_SetByConsole.get(getMain()));

        } else {
            // Handles console, player and mail
            getMain().getConsole().logFailedBalance(null, receiver, response.errorMessage);
        }
        return true;
    }
}

