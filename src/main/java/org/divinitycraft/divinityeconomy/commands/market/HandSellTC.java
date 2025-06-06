package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandMaterialsTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.player.PlayerManager;
import org.divinitycraft.divinityeconomy.utils.Converter;
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
            strings = new String[]{LangEntry.MARKET_InvalidItemHeld.get(getMain())};
        } else {
            MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(heldItem);
            switch (args.length) {
                // 1 args
                // return max stack size for the material given
                case 1:
                    Material material = marketableMaterial.getMaterial();
                    ArrayList<String> allStrings = new ArrayList<>();
                    int heldAmount = heldItem.getAmount();
                    int stackSize = material.getMaxStackSize();
                    int inventoryCount = marketableMaterial.getMaterialCount(sender);

                    LangEntry.W_max.addLang(getMain(), allStrings);
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
                            LangEntry.VALUE_Response.get(getMain(), getMain().getConsole().formatMoney(marketableMaterial.getManager().calculatePrice(Converter.getInt(args[0]), marketableMaterial.getQuantity(), marketableMaterial.getManager().getSellScale(), false))),
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
