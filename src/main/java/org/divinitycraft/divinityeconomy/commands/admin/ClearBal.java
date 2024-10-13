package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A command for clearing the balance of a player.
 */
public class ClearBal extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public ClearBal(DEPlugin app) {
        super(app, "clearbal", true, Setting.COMMAND_CLEAR_BALANCE_ENABLE_BOOLEAN);
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
        // 0 args:
        //  player2 is the caller.
        // 1 arg:
        //  player2's name is provided.
        OfflinePlayer player2;
        switch (args.length) {
            // 0 args
            case 0:
                player2 = sender;
                break;

            // 1 arg
            case 1:
                player2 = getMain().getPlayMan().getPlayer(args[0], false);
                break;

            // If any other number of arguments are passed.
            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (player2 == null) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidPlayerName.logLevel, LangEntry.GENERIC_InvalidPlayerName.get(getMain()));
            return true;
        }

        // Set balance to 0
        double startingBalance = getMain().getEconMan().getBalance(player2);
        EconomyResponse response = getMain().getEconMan().setCash(player2, 0);

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            getMain().getConsole().logBalance(sender, player2, startingBalance, response.balance, LangEntry.BALANCE_ClearedByPlayer.get(getMain(), sender.getName()));
        } else {
            getMain().getConsole().logFailedBalance(sender, player2, response.errorMessage);
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
        OfflinePlayer player;
        switch (args.length) {
            // 1 arg
            case 1:
                player = getMain().getPlayMan().getPlayer(args[0], false);
                break;

            // If any other number of arguments are passed.
            default:
                getMain().getConsole().usage(LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (player == null) {
            getMain().getConsole().send(LangEntry.GENERIC_InvalidPlayerName.logLevel, LangEntry.GENERIC_InvalidPlayerName.get(getMain()));
            return true;
        }

        // Set balance to 0
        double startingBalance = getMain().getEconMan().getBalance(player);
        EconomyResponse response = getMain().getEconMan().setCash(player, 0);

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            getMain().getConsole().logBalance(null, player, startingBalance, response.balance, LangEntry.BALANCE_ClearedByConsole.get(getMain()));
        } else {
            getMain().getConsole().logFailedBalance(null, player, response.errorMessage);
        }

        return true;
    }
}
