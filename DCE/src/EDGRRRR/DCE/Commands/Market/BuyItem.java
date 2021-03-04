package EDGRRRR.DCE.Commands.Market;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Materials.MaterialValueResponse;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for buying items from the market
 */
public class BuyItem implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/buy <itemName> <amountToBuy> | /buy <itemName>";

    public BuyItem(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComBuyItem))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
            return true;
        }

        String materialName;
        int amount = 1;
        switch (args.length) {
            // Just material, used default amount of 1
            case 1:
                materialName = args[0];
                break;

            // Material & Amount
            case 2:
                materialName = args[0];
                amount = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsoleManager().usage(from, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amount < 1) {
            this.app.getConsoleManager().usage(from, "Invalid amount.", this.usage);
            this.app.getConsoleManager().debug("(BuyItem)Invalid amount: " + amount);
        } else {
            MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
            if (materialData == null) {
                this.app.getConsoleManager().usage(from, "Unknown Item: '" + materialName + "'", "");
                this.app.getConsoleManager().debug("(BuyItem)Unknown Item: " + materialName);

            } else {
                int availableSpace = this.app.getPlayerInventoryManager().getAvailableSpace(from, materialData.getMaterial());
                if (amount > availableSpace) {
                    this.app.getConsoleManager().usage(from, "You only have space for " + availableSpace + " " + materialData.getCleanName(), this.usage);
                    this.app.getConsoleManager().debug(from.getName() + " couldn't buy " + materialData.getMaterialID() + " because missing inventory space " + availableSpace + " / " + amount);

                } else {
                    ItemStack[] itemStacks = this.app.getPlayerInventoryManager().createItemStacks(materialData.getMaterial(), amount);
                    MaterialValueResponse priceResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
                    EconomyResponse saleResponse = this.app.getEconomyManager().remCash(from, priceResponse.getValue());
                    if (saleResponse.type == ResponseType.SUCCESS && priceResponse.getResponseType() == ResponseType.SUCCESS) {
                        this.app.getPlayerInventoryManager().addItemsToPlayer(from, itemStacks);
                        materialData.remQuantity(amount);

                        // Handles console, message and mail
                        this.app.getConsoleManager().logPurchase(from, amount, saleResponse.amount, materialData.getCleanName());

                    } else {
                        String errorMessage;
                        if (saleResponse.type == ResponseType.FAILURE) errorMessage = saleResponse.errorMessage;
                        else if (priceResponse.getResponseType() == ResponseType.FAILURE) errorMessage = priceResponse.getErrorMessage();
                        else errorMessage = "¯\\_(ツ)_/¯";

                        // Handles console, message and mail
                        this.app.getConsoleManager().logFailedPurchase(from, amount, saleResponse.amount, materialData.getCleanName(), errorMessage);
                    }
                }

            }
        }
        return true;
    }
}
