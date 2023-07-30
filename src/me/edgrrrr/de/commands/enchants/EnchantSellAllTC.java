package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchantTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.block.MarketableBlock;
import org.bukkit.entity.Player;

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
        MarketableBlock materialData;
        switch (args.length) {
            // 1 args
            // return items in user inventory
            case 1:
                strings = new String[]{
                        "Whitelist: material1,material2,material3",
                        "Blacklist: !material1,material2,material3",
                        "Empty: everything"
                };
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
