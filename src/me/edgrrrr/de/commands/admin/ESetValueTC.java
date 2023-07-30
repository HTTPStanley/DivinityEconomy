package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the eset value command
 */
public class ESetValueTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public ESetValueTC(DEPlugin app) {
        super(app, "esetvalue", true, Setting.COMMAND_E_SET_VALUE_ENABLE_BOOLEAN);
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
        String arg;
        switch (args.length) {
            // Args 1
            // get player names that start with args[0]
            case 1:
                arg = args[0];
                break;

            default:
                arg = "";
                break;
        }

        return Arrays.asList(this.getMain().getEnchMan().getItemNames(arg).toArray(new String[0]));
    }
}
