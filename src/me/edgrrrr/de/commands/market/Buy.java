package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialManager;
import me.edgrrrr.de.market.items.materials.MaterialValueResponse;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

import java.util.Arrays;

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
                if (args[1].equalsIgnoreCase("max")) {

                    // Get the marketable material
                    MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(materialName);
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
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount is greater than 0
        if (amountToBuy < 1) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }

        // Ensure Material given exists.
        MarketableMaterial materialData = this.getMain().getMarkMan().getItem(materialName);
        if (materialData == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, CommandResponse.InvalidItemName.message, materialName);
            return true;
        }
        MaterialManager manager = materialData.getManager();

        // Ensure player has the available inventory space
        int availableSpace = materialData.getAvailableSpace(sender);
        if (amountToBuy > availableSpace) {
            this.getMain().getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), String.format(CommandResponse.InvalidInventorySpace.message, availableSpace, amountToBuy));
            return true;
        }

        // Ensure market has enough stock
        if (!materialData.has(amountToBuy)) {
            this.getMain().getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), String.format(CommandResponse.InvalidStockAmount.message, materialData.getQuantity(), amountToBuy));
            return true;
        }

        // Get item stacks to buy
        // Get the value of the item stacks
        // Remove the value of the items from the player
        MaterialValueResponse priceResponse = manager.getBuyValue(materialData.getItemStacks(amountToBuy));
        EconomyResponse saleResponse = this.getMain().getEconMan().remCash(sender, priceResponse.getValue());

        // If the transaction or valuation failed then the user is returned an error.
        if (!saleResponse.transactionSuccess() || priceResponse.isFailure()) {
            String errorMessage = CommandResponse.UnknownError.message;
            if (!saleResponse.transactionSuccess()) errorMessage = saleResponse.errorMessage;
            else if (priceResponse.isFailure()) errorMessage = priceResponse.getErrorMessage();

            // Handles console, message and mail
            this.getMain().getConsole().logFailedPurchase(sender, priceResponse.getQuantity(), materialData.getCleanName(), errorMessage);
            return true;
        }

        // Handle adding items to player and removing quantity from market
        PlayerManager.addPlayerItems(sender, priceResponse.getItemStacksAsArray());
        manager.editQuantity(materialData, -priceResponse.getQuantity());

        // Handles console, message and mail
        this.getMain().getConsole().logPurchase(sender, priceResponse.getQuantity(), saleResponse.amount, materialData.getCleanName());

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
