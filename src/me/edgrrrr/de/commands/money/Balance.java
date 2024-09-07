package me.edgrrrr.de.commands.money;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A command for getting player's balances
 */
public class Balance extends DivinityCommand {
    protected final boolean isBalanceEnabled;
    protected final boolean isBalanceOtherEnabled;
    private final String balancePermission = "de.money.balance";
    private final String balanceOtherPermission = "de.money.balanceOther";

    /**
     * Constructor
     *
     * @param app
     */
    public Balance(DEPlugin app) {
        super(app, "balance", true, true);
        this.checkEconomyEnabled = true;
        this.checkPermissions = false;
        this.isBalanceEnabled = getMain().getConfMan().getBoolean(Setting.COMMAND_BALANCE_ENABLE_BOOLEAN);
        this.isBalanceOtherEnabled = getMain().getConfMan().getBoolean(Setting.COMMAND_BALANCE_OTHER_ENABLE_BOOLEAN);
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
        // command - returns the callers balance.
        // command <username> - returns the usernames balance.
        OfflinePlayer receiverPlayer;

        if (args.length == 1) {
            // Using /balance <username>, check if the command is enabled.
            if (!isBalanceOtherEnabled) {
                returnCommandDisabled(sender);
                return true;
            }

            // Using /balance <username>, check if the player has permission.
            if (!sender.hasPermission(balanceOtherPermission)) {
                returnPlayerNoPermission(sender);
                return true;
            }

            receiverPlayer = getMain().getPlayMan().getPlayer(args[0], false);
        } else {
            // Using /balance, check if the command is enabled.
            if (!isBalanceEnabled) {
                returnCommandDisabled(sender);
                return true;
            }

            // Using /balance, check if the player has permission.
            if (!sender.hasPermission(balancePermission)) {
                returnPlayerNoPermission(sender);
                return true;
            }

            // any number of args. just return their own.
            receiverPlayer = sender;
        }

        if (receiverPlayer == null) {
            getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidPlayerName.get(getMain()), this.help.getUsages());
            return true;
        }

        getMain().getConsole().send(sender, LangEntry.BALANCE_ResponseOther.logLevel, LangEntry.BALANCE_ResponseOther.get(getMain()), receiverPlayer.getName(), getMain().getConsole().getFormattedBalance(receiverPlayer));
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
        if (args.length == 1) {
            // Using /balance <username>, check if the command is enabled.
            if (!isBalanceOtherEnabled) {
                returnCommandDisabled(null);
                return true;
            }

            player = getMain().getPlayMan().getPlayer(args[0], false);
        } else {
            // Using /balance, check if the command is enabled.
            if (!isBalanceEnabled) {
                returnCommandDisabled(null);
                return true;
            }

            getMain().getConsole().usage(LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
            return true;
        }

        if (player == null) {
            getMain().getConsole().send(LangEntry.GENERIC_InvalidPlayerName.logLevel, LangEntry.GENERIC_InvalidPlayerName.get(getMain()));
            return true;
        }

        getMain().getConsole().send(LangEntry.BALANCE_ResponseOther.logLevel, LangEntry.BALANCE_ResponseOther.get(getMain()), player.getName(), getMain().getConsole().getFormattedBalance(player));
        return true;
    }
}
