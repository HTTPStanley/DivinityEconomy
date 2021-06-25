package me.edgrrrr.de.commands.money;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the send cash command
 */
public class SendCashTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public SendCashTC(DEPlugin app) {
        super(app, false, Setting.COMMAND_SEND_CASH_ENABLE_BOOLEAN);
    }

    /**
     * ###To be overridden by the actual command
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public List<String> onPlayerTabCompleter(Player sender, String[] args) {
        String[] strings;
        switch (args.length) {
            // Args 1
            // get player names that start with args[0]
            case 1:
                strings = this.app.getPlayerManager().getOfflinePlayerNames(args[0]);
                break;

            // Args 2
            // just return some numbers
            case 2:
                String balance = String.format("%,.2f", this.app.getEconomyManager().getBalance(sender));
                strings = new String[]{
                        "1", "10", "100", "1000", balance
                };
                break;

            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        return null;
    }
}
