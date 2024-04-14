package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemListTC extends DivinityCommandTC {
    /**
     * Constructor
     *
     * @param main
     */
    public ItemListTC(DEPlugin main) {
        super(main, "listitems", true, Setting.COMMAND_ITEMS_ENABLE_BOOLEAN);
    }

    /**
     * ###To be overridden by the actual command
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public List<String> onPlayerTabCompleter(Player sender, String[] args) {
        ArrayList<String> strings = new ArrayList<>();

        switch (args.length) {
            case 1:
                LangEntry.W_name.addLang(getMain(), strings);
                break;

            case 2:
                ItemList.alphabeticalAliases.forEach(string -> strings.add(String.format("+%s", string)));
                ItemList.alphabeticalAliases.forEach(string -> strings.add(String.format("-%s", string)));
                ItemList.priceAliases.forEach(string -> strings.add(String.format("+%s", string)));
                ItemList.priceAliases.forEach(string -> strings.add(String.format("-%s", string)));
                ItemList.stockAliases.forEach(string -> strings.add(String.format("+%s", string)));
                ItemList.stockAliases.forEach(string -> strings.add(String.format("-%s", string)));
                break;

            case 3:
                LangEntry.W_pagenumber.addLang(getMain(), strings);
                break;

            default:
                break;
        }


        return strings;
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        return onPlayerTabCompleter(null, args);
    }
}
