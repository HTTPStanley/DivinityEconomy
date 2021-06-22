package edgrrrr.de.commands.enchants;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandEnchantTC;
import edgrrrr.de.enchants.EnchantData;
import edgrrrr.de.math.Math;
import edgrrrr.de.player.PlayerInventoryManager;
import edgrrrr.de.utils.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        super(app, false, Setting.COMMAND_E_SELL_ENABLE_BOOLEAN);
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
                strings = this.app.getEnchantmentManager().getEnchantNames(this.app.getEnchantmentManager().getEnchantNames(heldItem.getEnchantments().keySet()), args[0]);
                break;

            // 2 args
            // return max stack size for the material given
            case 2:
                enchantData = this.app.getEnchantmentManager().getEnchant(args[0]);
                int maxLevel = 1;
                if (enchantData != null) {
                    maxLevel = heldItem.getEnchantmentLevel(enchantData.getEnchantment());
                }

                strings = ArrayUtils.strRange(1, maxLevel);
                break;

            // 3 args
            // If uses clicks space after number, returns the value of the amount of item given
            case 3:
                enchantData = this.app.getEnchantmentManager().getEnchant(args[0]);
                String value = "unknown";
                if (enchantData != null) {
                    value = String.format("£%,.2f", this.app.getEnchantmentManager().calculatePrice(EnchantData.levelsToBooks(Math.getInt(args[1])), enchantData.getQuantity(), this.app.getEnchantmentManager().enchantSellTax, false));
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
