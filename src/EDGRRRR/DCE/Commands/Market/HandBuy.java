package edgrrrr.dce.commands.market;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for buying the item the user is currently holding
 */
public class HandBuy implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public HandBuy(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("handbuy");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_HAND_BUY_ITEM_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "The market is not enabled.");
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
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.help);
                return true;
        }

        if (amountToBuy < 1) {
            DCEPlugin.CONSOLE.usage(player, "Invalid amount.", this.help);
            DCEPlugin.CONSOLE.debug("(HandBuy)Invalid amount: " + amountToBuy);

        } else {
            ItemStack heldItem = PlayerInventoryManager.getHeldItem(player);

            if (heldItem == null) {
                DCEPlugin.CONSOLE.usage(player, "You are not holding any item.", this.help);
                DCEPlugin.CONSOLE.debug("(HandBuy)User is not holding an item.");

            } else {
                MaterialData materialData = this.app.getMaterialManager().getMaterial(heldItem.getType().name());

                int availableSpace = PlayerInventoryManager.getAvailableSpace(player, materialData.getMaterial());
                if (amountToBuy > availableSpace) {
                    DCEPlugin.CONSOLE.logFailedPurchase(player, amountToBuy, materialData.getCleanName(), String.format("missing inventory space (%d/%d)", availableSpace, amountToBuy));

                } else {
                    if (amountToBuy > materialData.getQuantity()) {
                        DCEPlugin.CONSOLE.logFailedPurchase(player, amountToBuy, materialData.getCleanName(), String.format("not enough stock (%d/%d)", materialData.getQuantity(), amountToBuy));
                    } else {
                        ItemStack[] itemStacks = PlayerInventoryManager.createItemStacks(materialData.getMaterial(), amountToBuy);
                        ValueResponse priceResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
                        EconomyResponse saleResponse = this.app.getEconomyManager().remCash(player, priceResponse.value);
                        if (saleResponse.transactionSuccess() && priceResponse.isSuccess()) {
                            PlayerInventoryManager.addItemsToPlayer(player, itemStacks);
                            materialData.remQuantity(amountToBuy);

                            // Handles console, message and mail
                            DCEPlugin.CONSOLE.logPurchase(player, amountToBuy, saleResponse.amount, materialData.getCleanName());


                        } else {
                            String errorMessage = "unknown error";
                            if (!saleResponse.transactionSuccess()) {
                                errorMessage = saleResponse.errorMessage;
                            } else if (priceResponse.isFailure()) {
                                errorMessage = priceResponse.errorMessage;
                            }

                            // Handles console, message and mail
                            DCEPlugin.CONSOLE.logFailedPurchase(player, amountToBuy, materialData.getCleanName(), errorMessage);
                        }
                    }
                }
            }
        }
        return true;
    }
}
