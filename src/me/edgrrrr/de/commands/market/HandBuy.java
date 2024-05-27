package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialValueResponse;
import me.edgrrrr.de.player.PlayerManager;
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
        this.checkItemMarketEnabled = true;
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
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure amount given is greater than 0
        if (amountToBuy < 1) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()));
            return true;
        }


        ItemStack heldItem = PlayerManager.getHeldItem(sender);

        // Ensure user is holding an item
        if (heldItem == null) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.MARKET_InvalidItemHeld.get(getMain()));
            return true;
        }

        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(heldItem);
        int availableSpace = marketableMaterial.getAvailableSpace(sender);

        // Ensure the material is allowed to be bought
        if (!marketableMaterial.getAllowed()) {
            getMain().getConsole().send(sender, LangEntry.MARKET_ItemIsBanned.logLevel, LangEntry.MARKET_ItemIsBanned.get(getMain()), marketableMaterial.getName());
            return true;
        }

        // Ensure user has inventory space
        if (amountToBuy > availableSpace) {
            getMain().getConsole().logFailedPurchase(sender, amountToBuy, marketableMaterial.getName(), String.format(LangEntry.MARKET_InvalidInventorySpace.get(getMain()), availableSpace, amountToBuy));
            return true;
        }

        // Ensure market has enough
        if (!marketableMaterial.has(amountToBuy)) {
            getMain().getConsole().logFailedPurchase(sender, amountToBuy, marketableMaterial.getName(), String.format(LangEntry.MARKET_InvalidStockAmount.get(getMain()), marketableMaterial.getQuantity(), amountToBuy));
            return true;
        }

        // Get item stacks to buy
        // Get value of item stacks
        // Remove value from user
        MaterialValueResponse priceResponse = marketableMaterial.getManager().getBuyValue(marketableMaterial.getItemStacks(amountToBuy));
        EconomyResponse saleResponse = getMain().getEconMan().remCash(sender, priceResponse.getValue());

        // If user can afford & valuation was success
        if (saleResponse.transactionSuccess() && priceResponse.isSuccess()) {
            PlayerManager.addPlayerItems(sender, priceResponse.getItemStacksAsArray());
            marketableMaterial.getManager().editQuantity(marketableMaterial, -priceResponse.getQuantity());

            // Handles console, message and mail
            getMain().getConsole().logPurchase(sender, priceResponse.getQuantity(), saleResponse.amount, marketableMaterial.getName());
        }

        // Else return error message
        else {
            String errorMessage = LangEntry.GENERIC_UnknownError.get(getMain());
            if (!saleResponse.transactionSuccess()) {
                errorMessage = saleResponse.errorMessage;
            } else if (priceResponse.isFailure()) {
                errorMessage = priceResponse.getErrorMessage();
            }

            // Handles console, message and mail
            getMain().getConsole().logFailedPurchase(sender, priceResponse.getQuantity(), marketableMaterial.getName(), errorMessage);
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
