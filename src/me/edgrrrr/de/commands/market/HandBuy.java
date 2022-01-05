package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
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
                amountToBuy = Converter.getInt(args[0]);
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


        ItemStack heldItem = PlayerManager.getHeldItem(sender);

        // Ensure user is holding an item
        if (heldItem == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidItemHeld.message);
            return true;
        }

        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(heldItem);
        int availableSpace = marketableMaterial.getAvailableSpace(sender);

        // Ensure user has inventory space
        if (amountToBuy > availableSpace) {
            this.getMain().getConsole().logFailedPurchase(sender, amountToBuy, marketableMaterial.getCleanName(), String.format(CommandResponse.InvalidInventorySpace.message, availableSpace, amountToBuy));
            return true;
        }

        // Ensure market has enough
        if (!marketableMaterial.has(amountToBuy)) {
            this.getMain().getConsole().logFailedPurchase(sender, amountToBuy, marketableMaterial.getCleanName(), String.format(CommandResponse.InvalidStockAmount.message, marketableMaterial.getQuantity(), amountToBuy));
            return true;
        }

        // Get item stacks to buy
        // Get value of item stacks
        // Remove value from user
        ItemStack[] itemStacks = marketableMaterial.getItemStacks(amountToBuy);
        ValueResponse priceResponse = marketableMaterial.getManager().getBuyValue(itemStacks);
        EconomyResponse saleResponse = this.getMain().getEconMan().remCash(sender, priceResponse.value);

        // If user can afford & valuation was success
        if (saleResponse.transactionSuccess() && priceResponse.isSuccess()) {
            PlayerManager.addPlayerItems(sender, itemStacks);
            marketableMaterial.getManager().editQuantity(marketableMaterial, -amountToBuy);

            // Handles console, message and mail
            this.getMain().getConsole().logPurchase(sender, amountToBuy, saleResponse.amount, marketableMaterial.getCleanName());


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
            this.getMain().getConsole().logFailedPurchase(sender, amountToBuy, marketableMaterial.getCleanName(), errorMessage);
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
