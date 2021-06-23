package edgrrrr.de.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandMaterialsTC;
import edgrrrr.de.materials.MaterialData;
import edgrrrr.de.math.Math;
import edgrrrr.de.player.PlayerInventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the sell item command
 */
public class SellTC extends DivinityCommandMaterialsTC {

    /**
     * Constructor
     *
     * @param app
     */
    public SellTC(DEPlugin app) {
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
                if (materialData == null) {
                    strings = new String[]{
                            "Invalid material entered."
                    };
                } else {
                    Material material = materialData.getMaterial();
                    ArrayList<String> allStrings = new ArrayList<>();
                    int stackSize = material.getMaxStackSize();
                    int inventoryCount = PlayerInventoryManager.getMaterialCount(sender, material);

                    if (stackSize < inventoryCount) {
                        allStrings.add(String.valueOf(stackSize));
                    }
                    allStrings.add(String.valueOf(inventoryCount));

                    strings = allStrings.toArray(new String[0]);
                    break;
                }

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
