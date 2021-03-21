package edgrrrr.dce.commands.enchants;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.utils.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EnchantHandSellTC implements TabCompleter {
    private final DCEPlugin app;

    public EnchantHandSellTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_E_HAND_SELL_ENABLE_BOOLEAN.path()))) {
            return null;
        }
        Player player = (Player) sender;
        String[] strings;
        EnchantData enchantData;
        ItemStack heldItem = PlayerInventoryManager.getHeldItemNotNull(player);
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
                    value = String.format("£%,.2f", this.app.getEnchantmentManager().calculatePrice(this.app.getEnchantmentManager().getEnchantAmount(Math.getInt(args[1])), enchantData.getQuantity(), this.app.getEnchantmentManager().enchantSellTax, false));
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
}
