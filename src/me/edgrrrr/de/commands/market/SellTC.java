package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterialsTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.utils.Converter;
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
        super(app, "sell", false, Setting.COMMAND_SELL_ITEM_ENABLE_BOOLEAN);
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
        MarketableMaterial marketableMaterial;
        switch (args.length) {
            // 1 args
            // return items in user inventory
            case 1:
                String[] materials = PlayerManager.getInventoryMaterialNames(sender);
                strings = this.getMain().getMarkMan().getItemNames(materials, args[0]);
                break;

            // 2 args
            // return amount in user inventory
            case 2:
                marketableMaterial = this.getMain().getMarkMan().getItem(args[0]);
                if (marketableMaterial == null) {
                    strings = new String[]{
                            "Invalid material entered."
                    };
                } else {
                    Material material = marketableMaterial.getMaterial();
                    ArrayList<String> allStrings = new ArrayList<>();
                    allStrings.add("max");
                    int stackSize = material.getMaxStackSize();
                    int inventoryCount = marketableMaterial.getMaterialCount(sender);

                    if (stackSize < inventoryCount) {
                        allStrings.add(String.valueOf(stackSize));
                    }
                    allStrings.add(String.valueOf(inventoryCount));

                    strings = allStrings.toArray(new String[0]);
                    break;
                }

                break;

            case 3:
                marketableMaterial = this.getMain().getMarkMan().getItem(args[0]);
                String value = "unknown";
                if (marketableMaterial != null) {
                    value = String.format("£%,.2f", marketableMaterial.getManager().getSellValue(marketableMaterial.getMaterialSlotsToCount(sender, Converter.getInt(args[1]))).value);
                }
                strings = new String[]{
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
