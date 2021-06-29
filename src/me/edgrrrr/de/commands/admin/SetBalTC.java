package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the setbal command
 */
public class SetBalTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public SetBalTC(DEPlugin app) {
        super(app, "setbal", true, Setting.COMMAND_SET_BALANCE_ENABLE_BOOLEAN);
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
        String[] strings;
        switch (args.length) {
            // Args 1
            // get player names that start with args[0]
            case 1:
                strings = this.getMain().getPlayerManager().getOfflinePlayerNames(args[0]);
                break;

            // Args 2
            // just return some numbers
            case 2:
                strings = new String[]{
                        "1", "10", "100", "1000"
                };
                break;

            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}
