package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchantTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the enchant hand sell command
 */
public class EnchantHandSellTC extends DivinityCommandEnchantTC {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandSellTC(DEPlugin app) {
        super(app, "esell", false, Setting.COMMAND_E_SELL_ENABLE_BOOLEAN);
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
        ItemStack heldItem = PlayerManager.getHeldItem(sender, new ItemStack(Material.AIR, 0));
        String max = LangEntry.W_max.get(getMain());
        String defaultMax = LangEntry.W_max.get(getMain());
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1 -> {
                ArrayList<String> allStrings = new ArrayList<>();
                allStrings.add(defaultMax);
                if (!max.equalsIgnoreCase(defaultMax)) {
                    allStrings.add(max);
                }
                allStrings.addAll(getMain().getEnchMan().getItemNames(heldItem, args[0]));
                strings = allStrings.toArray(new String[0]);
            }

            // 2 args
            // return max stack size for the material given
            case 2 -> {
                if (args[0].equalsIgnoreCase(max) || args[0].equalsIgnoreCase(defaultMax)) {
                    strings = new String[]{getMain().getEnchMan().getSellValueString(heldItem)};
                } else {
                    strings = getMain().getEnchMan().getDowngradeValueString(heldItem, args[0]);
                }
            }

            // 3 args
            // If uses clicks space after number, returns the value of the amount of item given
            case 3 -> {
                strings = new String[]{getMain().getEnchMan().getSellValueString(heldItem, args[0], Converter.getInt(args[1]))};
            }

            // else
            default -> strings = new String[0];
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
