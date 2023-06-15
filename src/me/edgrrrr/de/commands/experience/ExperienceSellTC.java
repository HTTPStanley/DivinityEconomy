package me.edgrrrr.de.commands.experience;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchantTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.exp.ExpManager;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the enchant hand sell command
 */
public class ExperienceSellTC extends DivinityCommandEnchantTC {

    /**
     * Constructor
     *
     * @param app
     */
    public ExperienceSellTC(DEPlugin app) {
        super(app, "xpsell", false, Setting.COMMAND_EXP_SELL_ENABLE_BOOLEAN);
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
            default -> strings = new String[0];
            case 1 -> {
                ArrayList<String> values = new ArrayList<>();
                long exp = ExpManager.getPlayerExp(sender);
                if (exp > 0) {
                    values.add(String.valueOf(exp));
                    values.add("max");
                }

                if (exp > 1) {
                    values.add("1");
                }

                if (exp > 10) {
                    values.add("10");
                }

                if (exp > 100) {
                    values.add("100");
                }

                if (exp > 1000) {
                    values.add("1000");
                }

                if (exp > 10000) {
                    values.add("10000");
                }

                if (exp > 100000) {
                    values.add("100000");
                }

                strings = values.toArray(new String[0]);
            }
            case 2 -> {
                long exp = Converter.getLong(args[0]);
                if (exp > 0 && exp < 100000) {
                    strings = new String[]{this.getMain().getExpMan().getSellValueString(exp, sender)};
                } else {
                    strings = new String[]{"Error: Invalid amount."};
                }
            }
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
