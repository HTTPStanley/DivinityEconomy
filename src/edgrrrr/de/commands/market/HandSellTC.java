package edgrrrr.de.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import edgrrrr.de.commands.DivinityCommandMaterialsTC;
import edgrrrr.de.materials.MaterialData;
import edgrrrr.de.math.Math;
import edgrrrr.de.player.PlayerInventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        super(app, false, Setting.COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN);
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
            strings = new String[]{DivinityCommand.CommandResponse.InvalidItemHeld.message};
        } else {
            MaterialData materialData = this.app.getMaterialManager().getMaterial(heldItem.getType().toString());
            switch (args.length) {
                // 1 args
                // return max stack size for the material given
                case 1:
                    int heldAmount = heldItem.getAmount();
                    int maxStack = materialData.getMaterial().getMaxStackSize();
                    int totalCount = PlayerInventoryManager.getMaterialCount(sender, materialData.getMaterial());

                    int tempAmount;
                    if (maxStack < totalCount) tempAmount = maxStack;
                    else tempAmount = heldAmount;

                    strings = new String[]{
                            String.valueOf(heldAmount),
                            String.valueOf(tempAmount),
                            String.valueOf(maxStack)
                    };
                    break;

                // 2 args
                // If uses clicks space after number, returns the value of the amount of item given
                case 2:
                    strings = new String[]{
                            String.format("Value: £%,.2f", this.app.getMaterialManager().calculatePrice(Math.getInt(args[0]), materialData.getQuantity(), this.app.getMaterialManager().materialSellTax, false))
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
