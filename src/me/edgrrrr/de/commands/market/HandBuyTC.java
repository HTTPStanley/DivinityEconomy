package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterialsTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the hand buy command
 */
public class HandBuyTC extends DivinityCommandMaterialsTC {

    /**
     * Constructor
     *
     * @param app
     */
    public HandBuyTC(DEPlugin app) {
        super(app, "handbuy", false, Setting.COMMAND_HAND_BUY_ITEM_ENABLE_BOOLEAN);
    }

    /**
     * ###To be overridden by the actual command
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
                    strings = new String[]{
                            String.valueOf(marketableMaterial.getMaterial().getMaxStackSize() - heldItem.getAmount()),
                            String.valueOf(marketableMaterial.getMaterial().getMaxStackSize()),
                            String.valueOf(marketableMaterial.getAvailableSpace(sender))
                    };
                    break;

                // 2 args
                // If uses clicks space after number, returns the value of the amount of item given
                case 2:
                    strings = new String[]{
                            LangEntry.VALUE_Response.get(getMain(), marketableMaterial.getManager().calculatePrice(Converter.getInt(args[0]), marketableMaterial.getQuantity(), marketableMaterial.getManager().getBuyScale(), true))
                    };
                    break;
            }
        }

        return Arrays.asList(strings);
    }

    /**
     * ###To be overridden by the actual command
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
