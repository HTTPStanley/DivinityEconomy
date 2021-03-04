package EDGRRRR.DCE.Commands.Market;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Materials.MaterialValueResponse;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A simple ping pong! command
 */
public class HandBuy implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/hb | /hb <amount>";

    public HandBuy(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComHandBuy))) {
            this.app.getConsoleManager().severe(player, "This command is not enabled.");
            return true;
        }

        int amountToBuy;

        switch (args.length) {
            case 0:
                amountToBuy = 1;
                break;

            case 1:
                amountToBuy = Math.getInt(args[0]);
                break;

            default:
                this.app.getConsoleManager().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amountToBuy < 1) {
            this.app.getConsoleManager().usage(player, "Invalid amount.", this.usage);
            this.app.getConsoleManager().debug("(HandBuy)Invalid amount: " + amountToBuy);

        } else {
            ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);

            if (heldItem == null) {
                this.app.getConsoleManager().usage(player, "You are not holding any item.", this.usage);
                this.app.getConsoleManager().debug("(HandBuy)User is not holding an item.");

            } else {
                MaterialData materialData = this.app.getMaterialManager().getMaterial(heldItem.getType().name());

                int availableSpace = this.app.getPlayerInventoryManager().getAvailableSpace(player, materialData.getMaterial());
                if (amountToBuy > availableSpace) {
                    this.app.getConsoleManager().usage(player, "You only have space for " + availableSpace + " " + materialData.getCleanName(), this.usage);
                    this.app.getConsoleManager().debug(player.getName() + " couldn't buy " + materialData.getMaterialID() + " because they only have space for " + availableSpace + " " + materialData.getCleanName());

                } else {
                    ItemStack[] itemStacks = this.app.getPlayerInventoryManager().createItemStacks(materialData.getMaterial(), amountToBuy);
                    MaterialValueResponse priceResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
                    EconomyResponse saleResponse = this.app.getEconomyManager().remCash(player, priceResponse.getValue());
                    if (saleResponse.type == EconomyResponse.ResponseType.SUCCESS && priceResponse.getResponseType() == EconomyResponse.ResponseType.SUCCESS) {
                        this.app.getPlayerInventoryManager().addItemsToPlayer(player, itemStacks);
                        materialData.remQuantity(amountToBuy);

                        // Handles console, message and mail
                        this.app.getConsoleManager().logPurchase(player, amountToBuy, saleResponse.amount, materialData.getCleanName());


                    } else {
                        String errorMessage;
                        if (saleResponse.type == EconomyResponse.ResponseType.FAILURE) {
                            errorMessage = saleResponse.errorMessage;
                        }
                        else if (priceResponse.getResponseType() == EconomyResponse.ResponseType.FAILURE) {
                            errorMessage = priceResponse.getErrorMessage();
                        }
                        else {
                            errorMessage = "¯\\_(ツ)_/¯";
                        }

                        // Handles console, message and mail
                        this.app.getConsoleManager().logFailedPurchase(player, amountToBuy, saleResponse.amount, materialData.getCleanName(), errorMessage);
                    }
                }
            }
        }
        return true;
    }
}
