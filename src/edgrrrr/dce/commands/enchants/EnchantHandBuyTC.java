package edgrrrr.dce.commands.enchants;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandEnchantTC;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.utils.ArrayUtils;
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
    public EnchantHandBuyTC(DCEPlugin app) {
        super(app, false, Setting.COMMAND_E_BUY_ENABLE_BOOLEAN);
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
                strings = this.app.getEnchantmentManager().getCompatibleEnchants(heldItem, args[0]);
                break;

            // 2 args
            // return max stack size for the material given
            case 2:
                enchantData = this.app.getEnchantmentManager().getEnchant(args[0]);
                int maxLevel = 1;
                if (enchantData != null) {
                    maxLevel = enchantData.getMaxLevel() - heldItem.getEnchantmentLevel(enchantData.getEnchantment());
                }

                strings = ArrayUtils.strRange(1, maxLevel);
                break;

            // 3 args
            // If uses clicks space after number, returns the value of the amount of item given
            case 3:
                enchantData = this.app.getEnchantmentManager().getEnchant(args[0]);
                String value = "unknown";
                if (enchantData != null) {
                    value = String.format("£%,.2f", this.app.getEnchantmentManager().calculatePrice(this.app.getEnchantmentManager().getEnchantAmount(Math.getInt(args[1])), enchantData.getQuantity(), this.app.getEnchantmentManager().enchantBuyTax, true));
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

