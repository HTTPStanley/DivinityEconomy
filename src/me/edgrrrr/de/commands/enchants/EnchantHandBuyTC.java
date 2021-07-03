package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchantTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.enchants.EnchantData;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.player.PlayerInventoryManager;
import me.edgrrrr.de.utils.ArrayUtils;
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
        EnchantData enchantData;
        ItemStack heldItem = PlayerInventoryManager.getHeldItemNotNull(sender);
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1:
                strings = this.getMain().getEnchantmentManager().getCompatibleEnchants(heldItem, args[0]);
                break;

            // 2 args
            // return max stack size for the material given
            case 2:
                enchantData = this.getMain().getEnchantmentManager().getEnchant(args[0]);
                int maxLevel = 1;
                if (enchantData != null) {
                    maxLevel = enchantData.getMaxLevel() - heldItem.getEnchantmentLevel(enchantData.getEnchantment());
                }

                strings = ArrayUtils.strRange(1, maxLevel);
                break;

            // 3 args
            // If uses clicks space after number, returns the value of the amount of item given
            case 3:
                enchantData = this.getMain().getEnchantmentManager().getEnchant(args[0]);
                String value = "unknown";
                if (enchantData != null) {
                    int ui = heldItem.getEnchantmentLevel(enchantData.getEnchantment());
                    value = String.format("£%,.2f", this.getMain().getEnchantmentManager().calculatePrice(EnchantData.levelsToBooks(ui, ui+Math.getInt(args[1])), enchantData.getQuantity(), this.getMain().getEnchantmentManager().getBuyScale(), true));
                }

                strings = new String[] {
                        String.format("Value: %s", value)
                };
                break;

            // else
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

