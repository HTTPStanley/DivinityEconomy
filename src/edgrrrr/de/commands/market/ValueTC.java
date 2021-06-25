package edgrrrr.de.commands.market;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandMaterialsTC;
import edgrrrr.de.config.Setting;
import edgrrrr.de.materials.MaterialData;
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
        super(app, true, Setting.COMMAND_VALUE_ENABLE_BOOLEAN);
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
            // return names of players starting with arg
            case 1:
                strings = this.app.getMaterialManager().getMaterialNames(args[0]);
                break;

            // 2 args
            // return max stack size for the material given
            case 2:
                materialData = this.app.getMaterialManager().getMaterial(args[0]);
                int stackSize = 64;
                if (materialData != null) {
                    stackSize = materialData.getMaterial().getMaxStackSize();
                }

                strings = new String[] {
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
