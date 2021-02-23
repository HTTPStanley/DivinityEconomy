package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Materials.MaterialValue;
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

        } else {

            int slotIdx = player.getInventory().getHeldItemSlot();
            ItemStack iStack = player.getInventory().getItem(slotIdx);

            if (iStack == null) {
                this.app.getConsoleManager().usage(player, "You are not holding any item.", this.usage);
            } else {
                MaterialData materialData = this.app.getMaterialManager().getMaterial(iStack.getType().name());

                if (!materialData.getAllowed()) {
                    this.app.getConsoleManager().usage(player, "Cannot buy " + materialData.getCleanName() + " because it is not allowed to be bought or sold", this.usage);
                    this.app.getConsoleManager().warn(player.getName() + " couldn't buy " + materialData.getMaterialID() + " because it is not allowed to be bought or sold");

                } else {
                    int availableSpace = this.app.getPlayerInventoryManager().getAvailableSpace(player, materialData.getMaterial());
                    if (amountToBuy > availableSpace) {
                        this.app.getConsoleManager().usage(player, "You only have space for " + availableSpace + " " + materialData.getCleanName(), this.usage);
                        this.app.getConsoleManager().info(player.getName() + " couldn't buy " + materialData.getMaterialID() + " because missing inventory space " + availableSpace + " / " + amountToBuy);

                    } else {
                        ItemStack[] itemStacks = this.app.getPlayerInventoryManager().createItemStacks(materialData.getMaterial(), amountToBuy);
                        MaterialValue priceResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
                        EconomyResponse saleResponse = this.app.getEconomyManager().remCash(player, priceResponse.getValue());
                        double cost = this.app.getEconomyManager().round(saleResponse.amount);
                        double balance = this.app.getEconomyManager().round(saleResponse.balance);
                        if (saleResponse.type == EconomyResponse.ResponseType.SUCCESS && priceResponse.getResponseType() == EconomyResponse.ResponseType.SUCCESS) {
                            this.app.getPlayerInventoryManager().addItemsToPlayer(player, itemStacks);
                            materialData.remQuantity(amountToBuy);
                            this.app.getConsoleManager().info(player, "Bought " + amountToBuy + " " + materialData.getCleanName() + " for £" + cost + ". New Balance: £" + balance);
                            this.app.getConsoleManager().info(player.getName() + " Bought " + amountToBuy + " " + materialData.getMaterialID() + " for £" + cost);

                        } else {
                            String errorMessage;
                            if (saleResponse.type == EconomyResponse.ResponseType.FAILURE)
                                errorMessage = saleResponse.errorMessage;
                            else if (priceResponse.getResponseType() == EconomyResponse.ResponseType.FAILURE)
                                errorMessage = priceResponse.getErrorMessage();
                            else errorMessage = "¯\\_(ツ)_/¯";

                            this.app.getConsoleManager().usage(player, "Couldn't buy " + amountToBuy + " " + materialData.getCleanName() + " for £" + cost + " because " + errorMessage, this.usage);
                            this.app.getConsoleManager().warn(player.getName() + " couldn't buy " + amountToBuy + " " + materialData.getMaterialID() + " for £" + cost + " because " + errorMessage);
                        }
                    }
                }
            }
        }
        return true;
    }
}
