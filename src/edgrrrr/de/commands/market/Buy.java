package edgrrrr.de.commands.market;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandMaterials;
import edgrrrr.de.config.Setting;
import edgrrrr.de.materials.MaterialData;
import edgrrrr.de.math.Math;
import edgrrrr.de.player.PlayerInventoryManager;
import edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                materialName = args[0];
                amountToBuy = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount is greater than 0
        if (amountToBuy < 1) {
            this.app.getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }

        // Ensure Material given exists.
        MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
        if (materialData == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, String.format(CommandResponse.InvalidItemName.message, materialName));
            return true;
        }

        // Ensure player has the available inventory space
        int availableSpace = PlayerInventoryManager.getAvailableSpace(sender, materialData.getMaterial());
        if (amountToBuy > availableSpace) {
            this.app.getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), String.format(CommandResponse.InvalidInventorySpace.message, availableSpace, amountToBuy));
            return true;
        }

        // Ensure market has enough stock
        if (!materialData.has(amountToBuy)) {
            this.app.getConsole().logFailedPurchase(sender, amountToBuy, materialData.getCleanName(), String.format(CommandResponse.InvalidStockAmount.message, materialData.getQuantity(), amountToBuy));
            return true;
        }

        // Get item stacks to buy
        // Get the value of the item stacks
        // Remove the value of the items from the player
        ItemStack[] itemStacks = PlayerInventoryManager.createItemStacks(materialData.getMaterial(), amountToBuy);
        ValueResponse priceResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
        EconomyResponse saleResponse = this.app.getEconomyManager().remCash(sender, priceResponse.value);

        // Handle adding items to player and removing quantity from market
        if (saleResponse.transactionSuccess() && priceResponse.isSuccess()) {
            PlayerInventoryManager.addPlayerItems(sender, itemStacks);
            this.app.getMaterialManager().editQuantity(materialData, -amountToBuy);

            // Handles console, message and mail
            this.app.getConsole().logPurchase(sender, amountToBuy, saleResponse.amount, materialData.getCleanName());
        }

        // If the transaction or valuation failed then the user is returned an error.
        else {
            String errorMessage = CommandResponse.UnknownError.message;
            if (!saleResponse.transactionSuccess()) errorMessage = saleResponse.errorMessage;
            else if (priceResponse.isFailure()) errorMessage = priceResponse.errorMessage;

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
