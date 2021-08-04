package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterialsTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the value item command
 */
public class ValueTC extends DivinityCommandMaterialsTC {

    /**
     * Constructor
     *
     * @param app
     */
    public ValueTC(DEPlugin app) {
        super(app, "value", true, Setting.COMMAND_VALUE_ENABLE_BOOLEAN);
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
            case 1:
                strings = this.getMain().getMarkMan().getItemNames(args[0]);
                break;

            // 2 args
            // return max stack size for the material given
            case 2:
                marketableMaterial = this.getMain().getMarkMan().getItem(args[0]);
                int stackSize = 64;
                if (marketableMaterial != null) {
                    stackSize = marketableMaterial.getMaterial().getMaxStackSize();
                }

                strings = new String[]{
                        String.valueOf(stackSize)
                };
                break;

            // else
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
        return this.onPlayerTabCompleter(null, args);
    }
}
