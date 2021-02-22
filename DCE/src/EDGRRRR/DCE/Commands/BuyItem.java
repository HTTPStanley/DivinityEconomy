package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        } else {
            MaterialData material = this.app.getMaterialManager().getMaterial(materialName);
            if (material == null) {
                this.app.getConsoleManager().usage(from, "Unknown Item: '" + materialName + "'", "");
            } else {
                if (!material.getAllowed()) {
                    this.app.getConsoleManager().usage(from, "Cannot buy " + material.getCleanName() + " because it is not allowed to be bought or sold", this.usage);
                    this.app.getConsoleManager().warn(from.getName() + " couldn't buy " + material.getMaterialID() + " because it is not allowed to be bought or sold");
                } else {
                    int availableSpace = this.app.getPlayerInventoryManager().getAvailableSpace(from, material.getMaterial());
                    if (amount > availableSpace) {
                        this.app.getConsoleManager().usage(from, "You only have space for " + availableSpace + " " + material.getCleanName(), this.usage);
                        this.app.getConsoleManager().info(from.getName() + " couldn't buy " + material.getMaterialID() + " because missing inventory space " + availableSpace + " / " + amount);
                    } else {
                        EconomyResponse priceResponse = this.app.getMaterialManager().getMaterialPrice(material, amount, this.app.getEconomyManager().tax, true);
                        EconomyResponse saleResponse = this.app.getEconomyManager().remCash(from, priceResponse.balance);
                        double cost = this.app.getEconomyManager().round(saleResponse.amount);
                        double balance = this.app.getEconomyManager().round(saleResponse.balance);
                        if (saleResponse.type == ResponseType.SUCCESS && priceResponse.type == ResponseType.SUCCESS) {
                            this.app.getPlayerInventoryManager().addMaterialToPlayer(from, material.getMaterial(), amount);
                            material.remQuantity(amount);
                            this.app.getConsoleManager().info(from, "Bought " + amount + " " + material.getCleanName() + " for £" + cost + ". New Balance: £" + balance);
                            this.app.getConsoleManager().info(from.getName() + " Bought " + amount + " " + material.getMaterialID() + " for £" + cost);
                        } else {
                            String errorMessage;
                            if (saleResponse.type == ResponseType.FAILURE) errorMessage = saleResponse.errorMessage;
                            else if (priceResponse.type == ResponseType.FAILURE)
                                errorMessage = priceResponse.errorMessage;
                            else errorMessage = "¯\\_(ツ)_/¯";

                            this.app.getConsoleManager().usage(from, "Couldn't buy " + amount + " " + material.getCleanName() + " for £" + cost + " because " + errorMessage, this.usage);
                            this.app.getConsoleManager().warn(from.getName() + " couldn't buy " + amount + " " + material.getMaterialID() + " for £" + cost + " because " + errorMessage);
                        }
                    }
                }
            }
        }
        return true;
    }
}
