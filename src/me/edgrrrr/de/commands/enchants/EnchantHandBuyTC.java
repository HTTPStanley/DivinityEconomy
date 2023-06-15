package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchantTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the enchant hand buy command
 */
public class EnchantHandBuyTC extends DivinityCommandEnchantTC {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandBuyTC(DEPlugin app) {
        super(app, "ebuy", false, Setting.COMMAND_E_BUY_ENABLE_BOOLEAN);
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
        switch (args.length) {
            case 1 -> strings = this.getMain().getEnchMan().getCompatibleEnchants(heldItem, args[0]);
            case 2 -> {
                strings = this.getMain().getEnchMan().getUpgradeValueString(heldItem, args[0]);
            }
            case 3 -> {
                strings = new String[]{this.getMain().getEnchMan().getBuyValueString(heldItem, args[0], Converter.getInt(args[1]))};
            }
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

