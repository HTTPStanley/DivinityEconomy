package edgrrrr.de.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandMaterialsTC;
import edgrrrr.de.materials.MaterialData;
import edgrrrr.de.player.PlayerInventoryManager;
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
        super(app, false, Setting.COMMAND_HAND_VALUE_ENABLE_BOOLEAN);
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
        ItemStack heldItem = PlayerInventoryManager.getHeldItem(sender);
        if (heldItem == null) {
            strings = new String[]{"You are not holding any item."};
        } else {
            MaterialData materialData = this.app.getMaterialManager().getMaterial(heldItem.getType().toString());
            // 1 args
            // return max stack size for the material given
            if (args.length == 1) {
                strings = new String[]{
                        String.valueOf(materialData.getMaterial().getMaxStackSize())
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
