package edgrrrr.de.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandMaterials;
import edgrrrr.de.materials.MaterialData;
import edgrrrr.de.math.Math;
import edgrrrr.de.player.PlayerInventoryManager;
import edgrrrr.de.response.ValueResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for valuing the item in the users hand
 */
public class HandValue extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public HandValue(DEPlugin app) {
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
    public boolean onPlayerCommand(Player sender, String[] args) {
        int amount = 1;
        boolean valueAll = false;
        boolean valueHand = false;
        switch (args.length) {
            case 0:
                valueHand = true;
                break;

            case 1:
                String firstArg = args[0].toLowerCase();
                if (firstArg.equals("max")) {
                    valueAll = true;
                } else {
                    amount = Math.getInt(firstArg);
                }
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount is greater than 0
        if (amount < 0) {
            this.app.getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }

        ItemStack heldItem = PlayerInventoryManager.getHeldItem(sender);

        // Ensure user is holding an item
        if (heldItem == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidItemHeld.defaultLogLevel, CommandResponse.InvalidItemHeld.message);
            return false;

        }

        Material material = heldItem.getType();
        MaterialData materialData = this.app.getMaterialManager().getMaterial(material.name());
        ItemStack[] buyStacks;
        ItemStack[] sellStacks;
        ItemStack[] itemStacks = PlayerInventoryManager.getMaterialSlots(sender, material);

        if (valueHand) {
            amount = heldItem.getAmount();
            buyStacks = PlayerInventoryManager.createItemStacks(material, amount);
            sellStacks = new ItemStack[1];
            sellStacks[0] = heldItem;
        } else if (valueAll) {
            amount = PlayerInventoryManager.getMaterialCount(itemStacks);
            sellStacks = itemStacks;
            buyStacks = PlayerInventoryManager.createItemStacks(material, amount);
        } else {
            sellStacks = PlayerInventoryManager.createItemStacks(material, amount);
            buyStacks = sellStacks;
        }

        ValueResponse buyResponse = this.app.getMaterialManager().getBuyValue(buyStacks);
        ValueResponse sellResponse = this.app.getMaterialManager().getSellValue(sellStacks);

        if (buyResponse.isSuccess()) {
            this.app.getConsole().info(sender, String.format("Buy: %d %s costs £%,.2f", amount, materialData.getCleanName(), buyResponse.value));

        } else {
            this.app.getConsole().info(sender, String.format("Couldn't determine buy price of %d %s because %s", amount, materialData.getCleanName(), buyResponse.errorMessage));
        }

        if (sellResponse.isSuccess()) {
            this.app.getConsole().info(sender, String.format("Sell: %d %s costs £%,.2f", amount, materialData.getCleanName(), sellResponse.value));

        } else {
            this.app.getConsole().info(sender, String.format("Couldn't determine buy price of %d %s because %s", amount, materialData.getCleanName(), sellResponse.errorMessage));
        }
        return true;
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        return false;
    }
}
