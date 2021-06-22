package edgrrrr.de.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandTC;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the setvalue command
 */
public class SetStockTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public SetStockTC(DEPlugin app) {
        super(app, true, Setting.COMMAND_SET_STOCK_ENABLE_BOOLEAN);
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
                strings = this.app.getMaterialManager().getMaterialNames();
                break;

            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}

