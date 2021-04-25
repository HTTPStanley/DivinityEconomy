package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommand;
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
    public ClearBal(DCEPlugin app) {
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
                player2 = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                break;

            // If any other number of arguments are passed.
            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (player2 == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;
        }

        // Set balance to 0
        double startingBalance = this.app.getEconomyManager().getBalance(player2);
        EconomyResponse response = this.app.getEconomyManager().setCash(player2, 0);

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            this.app.getConsole().logBalance(sender, player2, startingBalance, response.balance, String.format("%s changed your balance", sender.getName()));
        } else {
            this.app.getConsole().logFailedBalance(sender, player2, response.errorMessage);
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
                player = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                break;

            // If any other number of arguments are passed.
            default:
                this.app.getConsole().usage(CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure to player exists
        if (player == null) {
            this.app.getConsole().send(CommandResponse.InvalidPlayerName.defaultLogLevel, CommandResponse.InvalidPlayerName.message);
            return true;
        }

        // Set balance to 0
        double startingBalance = this.app.getEconomyManager().getBalance(player);
        EconomyResponse response = this.app.getEconomyManager().setCash(player, 0);

        // Handles sender, receiver, message, mail and console log
        if (response.transactionSuccess()) {
            this.app.getConsole().logBalance(null, player, startingBalance, response.balance, "CONSOLE changed your balance");
        } else {
            this.app.getConsole().logFailedBalance(null, player, response.errorMessage);
        }

        return true;
    }
}
