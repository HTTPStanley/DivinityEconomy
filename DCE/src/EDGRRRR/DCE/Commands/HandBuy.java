package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Main.DCEPlugin;
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
                MaterialData material = this.app.getMaterialManager().getMaterial(iStack.getType().name());
                if (!material.getAllowed()) {
                    this.app.getConsoleManager().usage(player, "Cannot buy " + material.getCleanName() + " because it is not allowed to be bought or sold", this.usage);
                    this.app.getConsoleManager().warn(player.getName() + " couldn't buy " + material.getMaterialID() + " because it is not allowed to be bought or sold");
                } else {
                    int availableSpace = this.app.getPlayerInventoryManager().getAvailableSpace(player, material.getMaterial());
                    if (amountToBuy > availableSpace) {
                        this.app.getConsoleManager().usage(player, "You only have space for " + availableSpace + " " + material.getCleanName(), this.usage);
                        this.app.getConsoleManager().info(player.getName() + " couldn't buy " + material.getMaterialID() + " because missing inventory space " + availableSpace + " / " + amountToBuy);
                    } else {
                        EconomyResponse priceResponse = this.app.getMaterialManager().getMaterialPrice(material, amountToBuy, this.app.getEconomyManager().tax, true);
                        EconomyResponse saleResponse = this.app.getEconomyManager().remCash(player, priceResponse.balance);
                        double cost = this.app.getEconomyManager().round(saleResponse.amount);
                        double balance = this.app.getEconomyManager().round(saleResponse.balance);
                        if (saleResponse.type == EconomyResponse.ResponseType.SUCCESS && priceResponse.type == EconomyResponse.ResponseType.SUCCESS) {
                            this.app.getPlayerInventoryManager().addMaterialToPlayer(player, material.getMaterial(), amountToBuy);
                            material.remQuantity(amountToBuy);
                            this.app.getConsoleManager().info(player, "Bought " + amountToBuy + " " + material.getCleanName() + " for £" + cost + ". New Balance: £" + balance);
                            this.app.getConsoleManager().info(player.getName() + " Bought " + amountToBuy + " " + material.getMaterialID() + " for £" + cost);
                        } else {
                            String errorMessage;
                            if (saleResponse.type == EconomyResponse.ResponseType.FAILURE)
                                errorMessage = saleResponse.errorMessage;
                            else if (priceResponse.type == EconomyResponse.ResponseType.FAILURE)
                                errorMessage = priceResponse.errorMessage;
                            else errorMessage = "¯\\_(ツ)_/¯";

                            this.app.getConsoleManager().usage(player, "Couldn't buy " + amountToBuy + " " + material.getCleanName() + " for £" + cost + " because " + errorMessage, this.usage);
                            this.app.getConsoleManager().warn(player.getName() + " couldn't buy " + amountToBuy + " " + material.getMaterialID() + " for £" + cost + " because " + errorMessage);
                        }
                    }
                }
            }
        }
        return true;
    }
}
