package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the setvalue command
 */
public class BanItemTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public BanItemTC(DEPlugin app) {
        super(app, "banitem", true, Setting.COMMAND_BAN_ITEM_ENABLE_BOOLEAN);
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
                strings = getMain().getMarkMan().getItemNames().toArray(new String[0]);
                break;

            case 2:
                strings = new String[]{"true", "false"};
                break;

            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}

