package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandMaterialsTC;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the buy item command
 */
public class BuyItemTC extends DivinityCommandMaterialsTC {
    /**
     * Constructor
     *
     * @param app
     */
    public BuyItemTC(DCEPlugin app) {
        super(app, false, Setting.COMMAND_BUY_ITEM_ENABLE_BOOLEAN);
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
                        String.valueOf(stackSize),
                        String.valueOf(PlayerInventoryManager.getAvailableSpace(sender, materialData.getMaterial()))
                };
                break;

            // 3 args
            // If uses clicks space after number, returns the value of the amount of item given
            case 3:
                materialData = this.app.getMaterialManager().getMaterial(args[0]);
                String value = "unknown";
                if (materialData != null) {
                    value = String.format("£%,.2f", this.app.getMaterialManager().calculatePrice(Math.getInt(args[1]), materialData.getQuantity(), this.app.getMaterialManager().materialBuyTax, true));
                }

                strings = new String[] {
                        String.format("Value: %s", value)
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
        return null;
    }
}


