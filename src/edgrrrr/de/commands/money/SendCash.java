package edgrrrr.de.commands.money;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import edgrrrr.de.config.Setting;
import edgrrrr.de.math.Math;
import edgrrrr.de.response.EconomyTransferResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A command for sending cash between players
 */
public class SendCash extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public SendCash(DEPlugin app) {
        super(app, "sendcash", false, Setting.COMMAND_SEND_CASH_ENABLE_BOOLEAN);
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
        // command <player> <amount>
        OfflinePlayer player2;
        double amount;

        switch (args.length) {
            case 2:
                // Get online player
                player2 = this.app.getPlayerManager().getOfflinePlayer(args[0], false);
                amount = Math.getDouble(args[1]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure online or offline player exists.
        if (player2 == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidNumberOfArguments.defaultLogLevel, CommandResponse.InvalidNumberOfArguments.message);
            return true;
        }

        // Sendcash response
        EconomyTransferResponse response = this.app.getEconomyManager().sendCash(sender, player2, amount);

        // Handles console, message and mail
        if (response.responseType == EconomyResponse.ResponseType.SUCCESS) {
            this.app.getConsole().logTransfer(sender, player2, amount);
        } else {
            this.app.getConsole().logFailedTransfer(sender, player2, amount, response.errorMessage);
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
        return false;
    }
}
