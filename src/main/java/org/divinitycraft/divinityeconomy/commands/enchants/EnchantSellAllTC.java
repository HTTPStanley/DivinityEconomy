package org.divinitycraft.divinityeconomy.commands.enchants;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandEnchantTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the enchant hand sell command
 */
public class EnchantSellAllTC extends DivinityCommandEnchantTC {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantSellAllTC(DEPlugin app) {
        super(app, "esellall", false, Setting.COMMAND_E_SELL_ALL_ENABLE_BOOLEAN);
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
            // 1 args
            // return items in user inventory
            case 1:
                ArrayList<String> list = new ArrayList<>();
                LangEntry.SELLALL_Whitelist.addLang(getMain(), list);
                LangEntry.SELLALL_Blacklist.addLang(getMain(), list);
                LangEntry.SELLALL_Empty.addLang(getMain(), list);
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
        return null;
    }
}
