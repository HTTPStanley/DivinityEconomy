package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandMaterialsTC;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the handvalue command
 */
public class HandValueTC extends DivinityCommandMaterialsTC {

    /**
     * Constructor
     *
     * @param app
     */
    public HandValueTC(DEPlugin app) {
        super(app, "handvalue", false, Setting.COMMAND_HAND_VALUE_ENABLE_BOOLEAN);
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
            strings = new String[]{LangEntry.MARKET_YouAreNotHoldingAnItem.get(getMain())};
        } else {
            MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(heldItem);
            // 1 args
            // return max stack size for the material given
            if (args.length == 1) {
                strings = new String[]{
                        String.valueOf(marketableMaterial.getMaterial().getMaxStackSize())
                };
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
