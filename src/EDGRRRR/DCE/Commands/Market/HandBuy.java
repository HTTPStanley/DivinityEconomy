package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandMarket;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for buying the item the user is currently holding
 */
public class HandBuy extends DivinityCommandMarket {

    /**
     * Constructor
     *
     * @param app
     */
    public HandBuy(DCEPlugin app) {
        super(app, "handbuy", false, Setting.COMMAND_HAND_BUY_ITEM_ENABLE_BOOLEAN);
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
        int amountToBuy;

        switch (args.length) {
            case 0:
                amountToBuy = 1;
                break;

            case 1:
                amountToBuy = Math.getInt(args[0]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount given is greater than 0
        if (amountToBuy < 1) {
            this.app.getConsole().usage(sender, CommandResponse.InvalidAmountGiven.message, this.help.getUsages());
            return true;
        }


        ItemStack heldItem = PlayerInventoryManager.getHeldItem(sender);

        // Ensure user is holding an item
        if (heldItem == null) {
            this.app.getConsole().usage(sender, CommandResponse.InvalidItemHeld.message, this.help.getUsages());
            return true;
        }

        MaterialData materialData = this.app.getMaterialManager().getMaterial(heldItem.getType().name());
        int availableSpace = PlayerInventoryManager.getAvailableSpace(sender, materialData.getMaterial());

        // Ensure user has inventory space
        if (amountToBuy > availableSpace) {
            this.app.getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), String.format(CommandResponse.InvalidInventorySpace.message, availableSpace, amountToBuy));
            return true;
        }

        // Ensure market has enough
        if (materialData.has(amountToBuy)) {
            this.app.getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), String.format(CommandResponse.InvalidStockAmount.message, materialData.getQuantity(), amountToBuy));
            return true;
        }

        // Get item stacks to buy
        // Get value of item stacks
        // Remove value from user
        ItemStack[] itemStacks = PlayerInventoryManager.createItemStacks(materialData.getMaterial(), amountToBuy);
        ValueResponse priceResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
        EconomyResponse saleResponse = this.app.getEconomyManager().remCash(sender, priceResponse.value);

        // If user can afford & valuation was success
        if (saleResponse.transactionSuccess() && priceResponse.isSuccess()) {
            PlayerInventoryManager.addItemsToPlayer(sender, itemStacks);
            materialData.remQuantity(amountToBuy);

            // Handles console, message and mail
            this.app.getConsole().logPurchase(sender, amountToBuy, saleResponse.amount, materialData.getCleanName());


        }

        // Else return error message
        else {
            String errorMessage = CommandResponse.UnknownError.message;
            if (!saleResponse.transactionSuccess()) {
                errorMessage = saleResponse.errorMessage;
            } else if (priceResponse.isFailure()) {
                errorMessage = priceResponse.errorMessage;
            }

            // Handles console, message and mail
            this.app.getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), errorMessage);
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
