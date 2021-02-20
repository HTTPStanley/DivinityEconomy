package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Economy.Materials.MaterialData;
import EDGRRRR.DCE.Main.DCEPlugin;
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
        if (!(this.app.getConfig().getBoolean(this.app.getConf().strComBuyItem))) {
            this.app.getCon().severe(from, "This command is not enabled.");
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
                amount = (int) (double) this.app.getEco().getDouble(args[1]);
                break;

            default:
                this.app.getCon().usage(from, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amount < 1) {
            this.app.getCon().usage(from, "Invalid amount.", this.usage);
        } else {
            MaterialData material = this.app.getMat().getMaterial(materialName);
            if (material == null) {
                this.app.getCon().usage(from, "Unknown Item: '" + materialName + "'", "");
            } else {
                int availableSpace = this.app.getMat().getAvailableSpace(from, material.getMaterial());
                if (amount > availableSpace) {
                    this.app.getCon().usage(from, "You only have space for " + availableSpace + " " + material.getCleanName(), this.usage);
                } else {
                    EconomyResponse priceResponse = this.app.getMat().getMaterialPrice(material, amount, this.app.getEco().tax, true);
                    EconomyResponse saleResponse = this.app.getEco().remCash(from, priceResponse.balance);
                    double cost = this.app.getEco().round(saleResponse.amount);
                    double balance = this.app.getEco().round(saleResponse.balance);
                    if (saleResponse.type == ResponseType.SUCCESS && priceResponse.type == ResponseType.SUCCESS) {
                        this.app.getMat().addMaterialToPlayer(from, material.getMaterial(), amount);
                        material.remQuantity(amount);
                        this.app.getCon().info(from, "Bought " + amount + " " + material.getCleanName() + " for £" + cost + ". New Balance: £" + balance);
                        this.app.getCon().info(from.getName() + " Bought " + amount + " " + material.getMaterialID() + " for £" + cost);
                    } else {
                        String errorMessage;
                        if (saleResponse.type == ResponseType.FAILURE) errorMessage = saleResponse.errorMessage;
                        else if (priceResponse.type == ResponseType.FAILURE) errorMessage = priceResponse.errorMessage;
                        else errorMessage = "¯\\_(ツ)_/¯";

                        this.app.getCon().usage(from, "Couldn't buy " + amount + " " + material.getCleanName() + " for £" + cost + " because " + errorMessage, this.usage);
                        this.app.getCon().warn(from.getName() + " couldn't buy " + amount + " " + material.getMaterialID() + " for £" + cost + " because " + errorMessage);
                    }
                }
            }
        }
        return true;
    }
}
