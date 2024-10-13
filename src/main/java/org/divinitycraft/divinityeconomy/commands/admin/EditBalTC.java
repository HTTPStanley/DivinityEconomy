package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * tab completer for editbal command
 */
public class EditBalTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public EditBalTC(DEPlugin app) {
        super(app, "editbal", true, Setting.COMMAND_EDIT_BALANCE_ENABLE_BOOLEAN);
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
                strings = getMain().getPlayMan().getPlayerNames(args[0]);
                break;

            // Args 2
            // just return some numbers
            case 2:
                strings = new String[]{
                        "-1000", "-100", "-10", "-1",
                        "1000", "100", "10", "1"
                };
                break;

            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}
