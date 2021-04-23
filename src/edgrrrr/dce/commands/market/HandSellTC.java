package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommand;
import edgrrrr.dce.commands.DivinityCommandMarketTC;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the hand sell command
 */
public class HandSellTC extends DivinityCommandMarketTC {

    /**
     * Constructor
     *
     * @param app
     */
    public HandSellTC(DCEPlugin app) {
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
                    strings = new String[]{
                            String.valueOf(heldItem.getAmount()),
                            String.valueOf(materialData.getMaterial().getMaxStackSize()),
                            String.valueOf(PlayerInventoryManager.getMaterialCount(sender, materialData.getMaterial()))
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
