package me.edgrrrr.de.commands.experience;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandExperienceTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.edgrrrr.de.utils.Converter.constrainInt;

/**
 * A tab completer for the enchant hand buy command
 */
public class ExperienceBuyTC extends DivinityCommandExperienceTC {

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
            case 1 -> {
                // Create value array
                ArrayList<String> values = new ArrayList<>();

                // Add max args
                LangEntry.W_max.addLang(getMain(), values);

                // Add powers of 10
                int i = 1;
                while (i < getMain().getExpMan().getMaxTradableExp()) {
                    values.add(String.valueOf(i));
                    i *= 10;
                }

                // Return
                yield values.toArray(new String[0]);
            }
            case 2 -> {
                // Resolve amount to buy
                int amount;
                // Max argument
                if (LangEntry.W_max.is(getMain(), args[0])) {
                    amount = 100000;
                }

                // Default
                else {
                    amount = Converter.getInt(args[0]);
                }

                // Constrain and return
                yield new String[]{getMain().getExpMan().getBuyValueString(constrainInt(amount, getMain().getExpMan().getMinTradableExp(), getMain().getExpMan().getMaxTradableExp()))};

            }
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

