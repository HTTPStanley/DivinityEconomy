package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandMarket;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for selling items in the users hand
 */
public class HandSell extends DivinityCommandMarket {

    /**
     * Constructor
     *
     * @param app
     */
    public HandSell(DCEPlugin app) {
        super(app, "handsell", false, Setting.COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN);
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
    public boolean onPlayerCommand(Player sender, String[] args) {
        int amountToSell = -1;

        switch (args.length) {
            case 0:
                amountToSell = 1;
                break;

            case 1:
                amountToSell = Math.getInt(args[0]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount is above 0
        if (amountToSell < 1) {
            this.app.getConsole().usage(sender, CommandResponse.InvalidAmountGiven.message, this.help.getUsages());
            return true;
        }


        ItemStack heldItem = PlayerInventoryManager.getHeldItem(sender);

        // Ensure item held is not null
        if (heldItem == null) {
            this.app.getConsole().usage(sender, CommandResponse.InvalidItemHeld.message, this.help.getUsages());
            return true;
        }

        Material material = heldItem.getType();
        String materialName = material.name();
        MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
        int materialCount = PlayerInventoryManager.getMaterialCount(PlayerInventoryManager.getMaterialSlots(sender, material));

        // Ensure player inventory has enough
        if (materialCount < amountToSell) {
            this.app.getConsole().logFailedSale(sender, amountToSell, materialData.getCleanName(), String.format(CommandResponse.InvalidInventoryStock.message, materialCount, amountToSell));
            return true;
        }

        // Get item stacks to remove
        // Clone item stacks incase they need to be refunded
        // Get the value
        ItemStack[] itemStacks = PlayerInventoryManager.getMaterialSlotsToCount(sender, material, amountToSell);
        ItemStack[] itemStacksClone = PlayerInventoryManager.cloneItems(itemStacks);
        ValueResponse response = this.app.getMaterialManager().getSellValue(itemStacks);

        if (response.isSuccess()) {
            PlayerInventoryManager.removeMaterialsFromPlayer(itemStacks);

            EconomyResponse economyResponse = this.app.getEconomyManager().addCash(sender, response.value);
            if (!economyResponse.transactionSuccess()) {
                PlayerInventoryManager.addItemsToPlayer(sender, itemStacksClone);
                // Handles console, player message and mail
                this.app.getConsole().logFailedSale(sender, amountToSell, materialData.getCleanName(), economyResponse.errorMessage);
            }

            else {
                materialData.addQuantity(amountToSell);
                // Handles console, player message and mail
                this.app.getConsole().logSale(sender, amountToSell, response.value, materialData.getCleanName());
            }
        }

        else {
            // Handles console, player message and mail
            this.app.getConsole().logFailedSale(sender, amountToSell, materialData.getCleanName(), response.errorMessage);
        }
        return true;
    }

    /**
     * ###To be overridden by the actual command
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
