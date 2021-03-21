package edgrrrr.dce.commands.enchants;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.math.Math;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EnchantHandBuyTC implements TabCompleter {
    private final DCEPlugin app;

    public EnchantHandBuyTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_E_HAND_BUY_ENABLE_BOOLEAN.path()))) {
            return null;
        }
        Player player = (Player) sender;
        String[] strings;
        EnchantData enchantData;
        ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);
        if (heldItem == null) heldItem = new ItemStack(Material.AIR, 0);
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
                int enchantLevel = 1;
                if (enchantData != null) {
                    enchantLevel = enchantData.getMaxLevel();
                }

                strings = new String[] {
                    String.valueOf(enchantLevel)
                };
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
}