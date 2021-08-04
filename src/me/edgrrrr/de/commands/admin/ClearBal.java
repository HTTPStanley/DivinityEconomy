package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
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
                player2 = this.getMain().getPlayMan().getOfflinePlayer(args[0], false);
                break;

            // If any other number of arguments are passed.
            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (player2 == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;
        }

        // Set balance to 0
        double startingBalance = this.getMain().getEconMan().getBalance(player2);
        EconomyResponse response = this.getMain().getEconMan().setCash(player2, 0);

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            this.getMain().getConsole().logBalance(sender, player2, startingBalance, response.balance, String.format("%s changed your balance", sender.getName()));
        } else {
            this.getMain().getConsole().logFailedBalance(sender, player2, response.errorMessage);
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
                player = this.getMain().getPlayMan().getOfflinePlayer(args[0], false);
                break;

            // If any other number of arguments are passed.
            default:
                this.getMain().getConsole().usage(CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (player == null) {
            this.getMain().getConsole().send(CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;
        }

        // Set balance to 0
        double startingBalance = this.getMain().getEconMan().getBalance(player);
        EconomyResponse response = this.getMain().getEconMan().setCash(player, 0);

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            this.getMain().getConsole().logBalance(null, player, startingBalance, response.balance, "CONSOLE changed your balance");
        } else {
            this.getMain().getConsole().logFailedBalance(null, player, response.errorMessage);
        }

        return true;
    }
}
