package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.commands.DivinityCommandMaterialsTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.player.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the hand sell command
 */
public class HandSellTC extends DivinityCommandMaterialsTC {

    /**
     * Constructor
     *
     * @param app
     */
    public HandSellTC(DEPlugin app) {
        super(app, "handsell", false, Setting.COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN);
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
        String[] strings = new String[0];
        ItemStack heldItem = PlayerManager.getHeldItem(sender);
        if (heldItem == null) {
            strings = new String[]{DivinityCommand.CommandResponse.InvalidItemHeld.message};
        } else {
            MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(heldItem);
            switch (args.length) {
                // 1 args
                // return max stack size for the material given
                case 1:
                    Material material = marketableMaterial.getMaterial();
                    ArrayList<String> allStrings = new ArrayList<>();
                    int heldAmount = heldItem.getAmount();
                    int stackSize = material.getMaxStackSize();
                    int inventoryCount = marketableMaterial.getMaterialCount(sender);

                    allStrings.add("max");
                    allStrings.add(String.valueOf(heldAmount));
                    if (stackSize < inventoryCount) {
                        allStrings.add(String.valueOf(stackSize));
                        allStrings.add(String.valueOf(inventoryCount));
                    }

                    strings = allStrings.toArray(new String[0]);
                    break;

                // 2 args
                // If uses clicks space after number, returns the value of the amount of item given
                case 2:
                    strings = new String[]{
                            String.format("Value: £%,.2f", marketableMaterial.getManager().calculatePrice(Math.getInt(args[0]), marketableMaterial.getQuantity(), marketableMaterial.getManager().getSellScale(), false))
                    };
                    break;
            }
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
