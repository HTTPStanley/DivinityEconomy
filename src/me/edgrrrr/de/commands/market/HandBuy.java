package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.materials.MaterialData;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.player.PlayerInventoryManager;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for buying the item the user is currently holding
 */
public class HandBuy extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public HandBuy(DEPlugin app) {
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
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount given is greater than 0
        if (amountToBuy < 1) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }


        ItemStack heldItem = PlayerInventoryManager.getHeldItem(sender);

        // Ensure user is holding an item
        if (heldItem == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidItemHeld.message);
            return true;
        }

        MaterialData materialData = this.getMain().getMaterialManager().getMaterial(heldItem.getType().name());
        int availableSpace = PlayerInventoryManager.getAvailableSpace(sender, materialData.getMaterial());

        // Ensure user has inventory space
        if (amountToBuy > availableSpace) {
            this.getMain().getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), String.format(CommandResponse.InvalidInventorySpace.message, availableSpace, amountToBuy));
            return true;
        }

        // Ensure market has enough
        if (!materialData.has(amountToBuy)) {
            this.getMain().getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), String.format(CommandResponse.InvalidStockAmount.message, materialData.getQuantity(), amountToBuy));
            return true;
        }

        // Get item stacks to buy
        // Get value of item stacks
        // Remove value from user
        ItemStack[] itemStacks = PlayerInventoryManager.createItemStacks(materialData.getMaterial(), amountToBuy);
        ValueResponse priceResponse = this.getMain().getMaterialManager().getBuyValue(itemStacks);
        EconomyResponse saleResponse = this.getMain().getEconomyManager().remCash(sender, priceResponse.value);

        // If user can afford & valuation was success
        if (saleResponse.transactionSuccess() && priceResponse.isSuccess()) {
            PlayerInventoryManager.addPlayerItems(sender, itemStacks);
            this.getMain().getMaterialManager().editQuantity(materialData, -amountToBuy);

            // Handles console, message and mail
            this.getMain().getConsole().logPurchase(sender, amountToBuy, saleResponse.amount, materialData.getCleanName());


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
            this.getMain().getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), errorMessage);
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
