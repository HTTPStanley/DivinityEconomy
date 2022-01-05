package me.edgrrrr.de.commands.money;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.response.EconomyTransferResponse;
import me.edgrrrr.de.utils.Converter;
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
                player2 = this.getMain().getPlayMan().getOfflinePlayer(args[0], false);
                amount = Converter.getDouble(args[1]);
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure online or offline player exists.
        if (player2 == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidNumberOfArguments.defaultLogLevel, CommandResponse.InvalidNumberOfArguments.message);
            return true;
        }

        // Sendcash response
        EconomyTransferResponse response = this.getMain().getEconMan().sendCash(sender, player2, amount);

        // Handles console, message and mail
        if (response.responseType == EconomyResponse.ResponseType.SUCCESS) {
            this.getMain().getConsole().logTransfer(sender, player2, amount);
        } else {
            this.getMain().getConsole().logFailedTransfer(sender, player2, amount, response.errorMessage);
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
