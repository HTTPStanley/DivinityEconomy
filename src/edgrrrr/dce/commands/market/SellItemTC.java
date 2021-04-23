package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandMaterialsTC;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the sell item command
 */
public class SellItemTC extends DivinityCommandMaterialsTC {

    /**
     * Constructor
     *
     * @param app
     */
    public SellItemTC(DCEPlugin app) {
        super(app, false, Setting.COMMAND_SELL_ITEM_ENABLE_BOOLEAN);
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
        MaterialData materialData;
        switch (args.length) {
            // 1 args
            // return items in user inventory
            case 1:
                String[] materials = PlayerInventoryManager.getInventoryMaterials(sender);
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
                    inventoryCount = PlayerInventoryManager.getMaterialCount(sender, material);
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
                    value = String.format("£%,.2f", this.app.getMaterialManager().getSellValue(PlayerInventoryManager.getMaterialSlotsToCount(sender, material, Math.getInt(args[1]))).value);
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
