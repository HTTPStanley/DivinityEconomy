package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialManager;
import me.edgrrrr.de.market.items.materials.MaterialValueResponse;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * A command for buying items from the market
 */
public class Buy extends DivinityCommandMaterials {
    /**
     * Constructor
     *
     * @param app
     */
    public Buy(DEPlugin app) {
        super(app, "buy", false, Setting.COMMAND_BUY_ITEM_ENABLE_BOOLEAN);
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
        String materialName;
        int amountToBuy = 1;
        switch (args.length) {
            // Just material, used default amount of 1
            case 1:
                materialName = args[0];
                break;

            // Material & Amount
            case 2:
                // Get the material name
                materialName = args[0];

                // Check if the player wants to buy the max amount
                if (LangEntry.W_max.is(getMain(), args[1])) {

                    // Get the marketable material
                    MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(materialName);
                    // Ensure the material exists
                    if (marketableMaterial != null) {
                        amountToBuy = marketableMaterial.getAvailableSpace(sender);
                    }
                    // Else, set the amount to buy to 0
                    else {
                        amountToBuy = 0;
                    }
                }
                // Else, set the amount to buy to the given amount
                else {
                    amountToBuy = Converter.getInt(args[1]);
                }
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure amount is greater than 0
        if (amountToBuy < 1) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_InvalidAmountGiven.logLevel, LangEntry.GENERIC_InvalidAmountGiven.get(getMain()));
            return true;
        }

        // Ensure Material given exists.
        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(materialName);
        if (marketableMaterial == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidItemName.logLevel, LangEntry.MARKET_InvalidItemName.get(getMain()), materialName);
            return true;
        }
        MaterialManager manager = marketableMaterial.getManager();

        // Ensure the material is allowed to be bought and sold
        if (!marketableMaterial.getAllowed()) {
            getMain().getConsole().logFailedPurchase(sender, amountToBuy, marketableMaterial.getName(), LangEntry.MARKET_ItemIsBanned.get(getMain()));
            return true;
        }

        // Ensure player has the available inventory space
        int availableSpace = marketableMaterial.getAvailableSpace(sender);
        if (amountToBuy > availableSpace) {
            getMain().getConsole().logFailedPurchase(sender, amountToBuy, marketableMaterial.getName(), String.format(LangEntry.MARKET_InvalidInventorySpace.get(getMain()), availableSpace, amountToBuy));
            return true;
        }

        // Ensure market has enough stock
        if (!marketableMaterial.has(amountToBuy)) {
            getMain().getConsole().logFailedPurchase(sender, amountToBuy, marketableMaterial.getName(), String.format(LangEntry.MARKET_InvalidStockAmount.get(getMain()), marketableMaterial.getQuantity(), amountToBuy));
            return true;
        }

        // Get item stacks to buy
        // Get the value of the item stacks
        // Remove the value of the items from the player
        MaterialValueResponse priceResponse = manager.getBuyValue(marketableMaterial.getItemStacks(amountToBuy));
        EconomyResponse saleResponse = getMain().getEconMan().remCash(sender, priceResponse.getValue());

        // If the transaction or valuation failed then the user is returned an error.
        if (!saleResponse.transactionSuccess() || priceResponse.isFailure()) {
            String errorMessage = LangEntry.GENERIC_UnknownError.get(getMain());
            if (!saleResponse.transactionSuccess()) errorMessage = saleResponse.errorMessage;
            else if (priceResponse.isFailure()) errorMessage = priceResponse.getErrorMessage();

            // Handles console, message and mail
            getMain().getConsole().logFailedPurchase(sender, priceResponse.getQuantity(), marketableMaterial.getName(), errorMessage);
            return true;
        }

        // Handle adding items to player and removing quantity from market
        PlayerManager.addPlayerItems(sender, priceResponse.getItemStacksAsArray());
        manager.editQuantity(marketableMaterial, -priceResponse.getQuantity());

        // Handles console, message and mail
        getMain().getConsole().logPurchase(sender, priceResponse.getQuantity(), saleResponse.amount, marketableMaterial.getName());

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
