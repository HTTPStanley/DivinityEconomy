package me.edgrrrr.de.commands.experience;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchantTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the enchant hand buy command
 */
public class ExperienceBuyTC extends DivinityCommandEnchantTC {

    /**
     * Constructor
     *
     * @param app
     */
    public ExperienceBuyTC(DEPlugin app) {
        super(app, "xpbuy", false, Setting.COMMAND_EXP_BUY_ENABLE_BOOLEAN);
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
        String[] strings = switch (args.length) {
            case 1 -> new String[]{"1", "10", "100", "1000", "10000", "100000"};
            case 2 -> new String[]{this.getMain().getExpMan().getBuyValueString(Converter.getLong(args[0]))};
            default -> new String[0];
        };

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

