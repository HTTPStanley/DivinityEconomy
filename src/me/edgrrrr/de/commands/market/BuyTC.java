package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterialsTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;

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
        MarketableMaterial marketableMaterial;
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1 -> strings = this.getMain().getMarkMan().getItemNames(args[0]);


            // 2 args
            // return max stack size for the material given
            case 2 -> {
                marketableMaterial = this.getMain().getMarkMan().getItem(args[0]);
                int stackSize;
                if (marketableMaterial != null) {
                    stackSize = marketableMaterial.getMaterial().getMaxStackSize();
                    strings = new String[]{
                            String.valueOf(stackSize),
                            "max",
                            String.valueOf(marketableMaterial.getAvailableSpace(sender))
                    };
                } else {
                    strings = new String[]{
                            "Unknown material."
                    };
                }
            }

            // 3 args
            // If uses clicks space after number, returns the value of the amount of item given
            case 3 -> {
                marketableMaterial = this.getMain().getMarkMan().getItem(args[0]);
                String value = "unknown.";
                if (marketableMaterial != null) {
                    int amount;
                    if (args[1].equalsIgnoreCase("max")) {
                        amount = marketableMaterial.getAvailableSpace(sender);
                    } else {
                        amount = Converter.getInt(args[1]);
                    }
                    value = String.format("£%,.2f", marketableMaterial.getManager().calculatePrice(amount, marketableMaterial.getQuantity(), marketableMaterial.getManager().getBuyScale(), true));
                }

                strings = new String[]{
                        String.format("Value: %s", value)
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


