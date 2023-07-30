package me.edgrrrr.de.commands.money;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the balance command
 */
public class BalanceTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public BalanceTC(DEPlugin app) {
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
    public List<String> onPlayerTabCompleter(Player sender, String[] args) {
        return this.onConsoleTabCompleter(args);
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        String[] playerNames;
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1:
                playerNames = this.getMain().getPlayMan().getPlayerNames(args[0]);
                break;

            default:
                playerNames = new String[0];
                break;
        }

        return Arrays.asList(playerNames);
    }
}
