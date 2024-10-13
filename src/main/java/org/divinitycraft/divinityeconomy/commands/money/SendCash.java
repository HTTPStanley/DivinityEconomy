package org.divinitycraft.divinityeconomy.commands.money;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.response.EconomyTransferResponse;
import org.divinitycraft.divinityeconomy.utils.Converter;
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
        this.checkEconomyEnabled = true;
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
                player2 = getMain().getPlayMan().getPlayer(args[0], false);
                amount = Converter.getDouble(args[1]);
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure online or offline player exists.
        if (player2 == null) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidPlayerName.logLevel, LangEntry.GENERIC_InvalidPlayerName.get(getMain()));
            return true;
        }

        // Sendcash response
        EconomyTransferResponse response = getMain().getEconMan().sendCash(sender, player2, amount);

        // Handles console, message and mail
        if (response.isSuccess()) {
            getMain().getConsole().logTransfer(sender, player2, amount);
        } else {
            getMain().getConsole().logFailedTransfer(sender, player2, amount, response.getErrorMessage());
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
