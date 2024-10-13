package org.divinitycraft.divinityeconomy.commands.enchants;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandEnchantTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.market.items.enchants.MarketableEnchant;
import org.divinitycraft.divinityeconomy.utils.ArrayUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the enchant hand value command
 */
public class EnchantValueTC extends DivinityCommandEnchantTC {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantValueTC(DEPlugin app) {
        super(app, "evalue", true, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN);
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
        MarketableEnchant enchantData;
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1:
                strings = getMain().getEnchMan().getItemNames(args[0]).toArray(new String[0]);
                break;

            // 2 args
            // return max stack size for the material given
            case 2:
                enchantData = getMain().getEnchMan().getEnchant(args[0]);
                int maxLevel = 1;
                if (enchantData != null) {
                    maxLevel = enchantData.getMaxLevel();
                }

                strings = ArrayUtils.strRange(1, maxLevel);
                break;

            // else
            default:
                strings = getMain().getEnchMan().getItemNames().toArray(new String[0]);
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
