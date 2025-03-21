package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.Constants;
import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandMaterialsTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.utils.Converter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the buy item command
 */
public class BuyTC extends DivinityCommandMaterialsTC {
    /**
     * Constructor
     *
     * @param app
     */
    public BuyTC(DEPlugin app) {
        super(app, "buy", false, Setting.COMMAND_BUY_ITEM_ENABLE_BOOLEAN);
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
        ArrayList<String> list = new ArrayList<>();
        MarketableMaterial marketableMaterial;
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1 -> strings = getMain().getMarkMan().getItemNames(args[0]).toArray(new String[0]);


            // 2 args
            // return max stack size for the material given
            case 2 -> {
                marketableMaterial = getMain().getMarkMan().getItem(args[0]);
                int stackSize;
                if (marketableMaterial != null) {
                    stackSize = marketableMaterial.getMaterial().getMaxStackSize();
                    list.add(String.valueOf(stackSize));
                    LangEntry.W_max.addLang(getMain(), list);
                    list.add(String.valueOf(marketableMaterial.getAvailableSpace(sender)));
                    strings = list.toArray(new String[0]);
                } else {
                    LangEntry.MARKET_UnknownMaterial.addLang(getMain(), list);
                    strings = list.toArray(new String[0]);
                }
            }

            // 3 args
            // If uses clicks space after number, returns the value of the amount of item given
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
                    value = String.format("%s", getMain().getConsole().formatMoney(marketableMaterial.getManager().calculatePrice(amount, marketableMaterial.getQuantity(), marketableMaterial.getManager().getBuyScale(), true)));
                }

                strings = new String[]{
                        LangEntry.VALUE_Response.get(getMain(), value)
                };
            }

            // else
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


