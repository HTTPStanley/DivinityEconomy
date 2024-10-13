package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.Constants;
import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandMaterialsTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.player.PlayerManager;
import org.divinitycraft.divinityeconomy.utils.Converter;
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
            case 1 -> {
                strings = getMain().getMarkMan().getItemNames(PlayerManager.getInventoryMaterialNames(sender), args[0]).toArray(new String[0]);
            }

            // 2 args
            // return amount in user inventory
            case 2 -> {
                marketableMaterial = getMain().getMarkMan().getItem(args[0]);
                if (marketableMaterial == null) {
                    strings = new String[]{
                            LangEntry.MARKET_InvalidMaterialName.get(getMain())
                    };
                } else {
                    Material material = marketableMaterial.getMaterial();
                    ArrayList<String> allStrings = new ArrayList<>();
                    LangEntry.W_max.addLang(getMain(), allStrings);
                    int stackSize = material.getMaxStackSize();
                    int inventoryCount = marketableMaterial.getMaterialCount(sender);

                    if (stackSize < inventoryCount) {
                        allStrings.add(String.valueOf(stackSize));
                    }
                    allStrings.add(String.valueOf(inventoryCount));

                    strings = allStrings.toArray(new String[0]);
                }
            }
            case 3 -> {
                marketableMaterial = getMain().getMarkMan().getItem(args[0]);
                String value = LangEntry.W_unknown.get(getMain());
                if (marketableMaterial != null) {
                    int amount;
                    if (LangEntry.W_max.is(getMain(), args[1])) {
                        amount = marketableMaterial.getAvailableSpace(sender);
                    } else {
                        amount = Converter.getInt(args[1]);
                    }
                    amount = Converter.constrainInt(amount, Constants.MIN_VALUE_AMOUNT, Constants.MAX_VALUE_AMOUNT);
                    args[1] = String.valueOf(amount);
                    value = getMain().getConsole().formatMoney(marketableMaterial.getManager().getSellValue(marketableMaterial.getMaterialSlotsToCount(sender, amount)).getValue());
                }
                strings = new String[]{
                        LangEntry.VALUE_Response.get(getMain(), value)
                };
            }
            default -> strings = new String[0];
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
