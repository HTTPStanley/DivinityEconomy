package edgrrrr.de.commands.money;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A command for getting player's balances
 */
public class Balance extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public Balance(DEPlugin app) {
        super(app, "balance", true, Setting.COMMAND_BALANCE_ENABLE_BOOLEAN);
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

        switch (args.length) {
            case 1:
                receiverPlayer = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                break;

            default:
                // any number of args.. just return their own.
                receiverPlayer = sender;
                break;
        }

        if (receiverPlayer == null) {
            this.app.getConsole().usage(sender, CommandResponse.InvalidPlayerName.message, this.help.getUsages());
            return true;
        }

        this.app.getConsole().send(sender, CommandResponse.BalanceResponseOther.defaultLogLevel, String.format(CommandResponse.BalanceResponseOther.message, receiverPlayer.getName(), this.app.getConsole().getFormattedBalance(receiverPlayer)));
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
            case 1:
                player = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                break;

            default:
                this.app.getConsole().usage(CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        if (player == null) {
            this.app.getConsole().send(CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;
        }

        this.app.getConsole().send(CommandResponse.BalanceResponseOther.defaultLogLevel, String.format(CommandResponse.BalanceResponseOther.message, player.getName(), this.app.getConsole().getFormattedBalance(player)));
        return true;
    }
}
