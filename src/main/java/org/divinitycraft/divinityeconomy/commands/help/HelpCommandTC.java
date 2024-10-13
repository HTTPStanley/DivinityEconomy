package org.divinitycraft.divinityeconomy.commands.help;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommandTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public HelpCommandTC(DEPlugin app) {
        super(app, "ehelp", true, Setting.COMMAND_EHELP_ENABLE_BOOLEAN);
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
        ArrayList<String> list = new ArrayList<>();

        switch (args.length) {
            case 1:
                LangEntry.HELP_Command.addLang(getMain(), list);
                LangEntry.HELP_Term.addLang(getMain(), list);
                LangEntry.HELP_Page.addLang(getMain(), list);
                strings = list.toArray(new String[0]);
                break;

            case 2:
                LangEntry.HELP_Page.addLang(getMain(), list);
                strings = list.toArray(new String[0]);
                break;

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
        return this.onPlayerTabCompleter(null, args);
    }
}
