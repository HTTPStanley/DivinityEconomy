package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the hand sell command
 */
public class HandSellTC implements TabCompleter {
    private final DCEPlugin app;

    public HandSellTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN.path))) {
            return null;
        }

        Player player = (Player) sender;

        String[] strings = new String[0];
        ItemStack heldItem = PlayerInventoryManager.getHeldItem(player);
        if (heldItem == null) {
            strings = new String[]{"You are not holding any item."};
        } else {
            MaterialData materialData = this.app.getMaterialManager().getMaterial(heldItem.getType().toString());
            switch (args.length) {
                // 1 args
                // return max stack size for the material given
                case 1:
                    strings = new String[]{
                            String.valueOf(heldItem.getAmount()),
                            String.valueOf(materialData.getMaterial().getMaxStackSize()),
                            String.valueOf(PlayerInventoryManager.getMaterialCount(player, materialData.getMaterial()))
                    };
                    break;

                // 2 args
                // If uses clicks space after number, returns the value of the amount of item given
                case 2:
                    strings = new String[]{
                            String.format("Value: £%,.2f", this.app.getMaterialManager().calculatePrice(Math.getInt(args[0]), materialData.getQuantity(), this.app.getMaterialManager().materialSellTax, false))
                    };
                    break;
            }
        }

        return Arrays.asList(strings);
    }
}
