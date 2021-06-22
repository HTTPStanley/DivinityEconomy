package edgrrrr.de.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandTC;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the esetstock command
 */
public class ESetStockTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public ESetStockTC(DEPlugin app) {
        super(app, true, Setting.COMMAND_E_SET_STOCK_ENABLE_BOOLEAN);
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
            case 1:
                arg = args[0];
                break;

            default:
                arg = "";
                break;
        }

        return Arrays.asList(this.app.getEnchantmentManager().getEnchantNames(arg));
    }
}

