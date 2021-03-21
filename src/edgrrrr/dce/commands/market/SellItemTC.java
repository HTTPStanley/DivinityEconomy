package edgrrrr.dce.commands.market;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SellItemTC implements TabCompleter {
    private final DCEPlugin app;

    public SellItemTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_SELL_ITEM_ENABLE_BOOLEAN.path()))) {
            return null;
        }

        Player player = (Player) sender;

        String[] strings;
        MaterialData materialData;
        switch (args.length) {
            // 1 args
            // return items in user inventory
            case 1:
                String[] materials = PlayerInventoryManager.getInventoryMaterials(player);
                strings = this.app.getMaterialManager().getMaterialAliases(materials, args[0]);
                break;

            // 2 args
            // return amount in user inventory
            case 2:
                materialData = this.app.getMaterialManager().getMaterial(args[0]);
                int stackSize = 64;
                int inventoryCount = 1;
                if (materialData != null) {
                    Material material = materialData.getMaterial();
                    stackSize = material.getMaxStackSize();
                    inventoryCount = PlayerInventoryManager.getMaterialCount(player, material);
                }
                strings = new String[]{
                        String.valueOf(stackSize), String.valueOf(inventoryCount)
                };
                break;

            case 3:
                materialData = this.app.getMaterialManager().getMaterial(args[0]);
                String value = "unknown";
                if (materialData != null) {
                    Material material = materialData.getMaterial();
                    value = String.format("£%,.2f", this.app.getMaterialManager().getSellValue(PlayerInventoryManager.getMaterialSlotsToCount(player, material, Math.getInt(args[1]))).value);
                }
                strings = new String[] {
                    String.format("Value: %s", value)
                };
                break;


            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}
