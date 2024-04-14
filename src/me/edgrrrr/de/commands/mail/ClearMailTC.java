package me.edgrrrr.de.commands.mail;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the clear mail command
 */
public class ClearMailTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param
     */
    public ClearMailTC(DEPlugin app) {
        super(app, "clearmail", false, Setting.COMMAND_CLEAR_MAIL_ENABLE_BOOLEAN);
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
        String[] strings;
        switch (args.length) {
            // 1 arg
            // return list of page numbers
            case 1:
                ArrayList<String> list = new ArrayList<>();

                LangEntry.W_all.addLang(getMain(), list);
                LangEntry.W_read.addLang(getMain(), list);
                LangEntry.W_unread.addLang(getMain(), list);

                strings = list.toArray(new String[0]);

                break;

            // else
            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }

    /**
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
