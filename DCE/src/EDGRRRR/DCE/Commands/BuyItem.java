package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Economy.Materials.MaterialData;
import EDGRRRR.DCE.Main.DCEPlugin;
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
    private DCEPlugin app;
    private String usage = "/buy <itemName> <amountToBuy> | /buy <itemName>";

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

        String materialName = null;
        Integer amount = null;
        switch (args.length) {
            // Just material, used default amount of 1
            case 1:
                materialName = args[0];
                amount = 1;
                break;

            // Material & Amount
            case 2:
                materialName = args[0];
                amount = (int) (double) this.app.getEco().getDouble(args[1]);
                break;

            default:
            this.app.getCon().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        MaterialData material = this.app.getMat().getMaterial(materialName);
        if (material == null) {
            this.app.getCon().usage(from, "Unknown Item: '" + materialName + "'", "");
            return true;
        } else {
            EconomyResponse priceResponse = this.app.getMat().getMaterialPrice(material, amount, this.app.getEco().tax, true);
            EconomyResponse saleResponse = this.app.getEco().remCash(from, priceResponse.balance);
            if (saleResponse.type == ResponseType.SUCCESS && priceResponse.type == ResponseType.SUCCESS) {
                ItemStack iStack = material.getItemStack(amount);
                from.getInventory().addItem(iStack);
                material.remQuantity(amount);
                double cost = this.app.getEco().round(saleResponse.amount);
                double balance = this.app.getEco().round(saleResponse.balance);
                this.app.getCon().info(from, "You bought " + amount + " of " + material.getCleanName() + " for £" + cost + ". New Balance: £" + balance);
                this.app.getCon().info(from.getName() + "Bought " + amount + " of " + material.getCleanName() + " for £" + cost);
            }
            else {
                String errorMessage = null;
                if (saleResponse.type == ResponseType.FAILURE) errorMessage = saleResponse.errorMessage;
                else if (priceResponse.type == ResponseType.FAILURE) errorMessage = priceResponse.errorMessage;
                else errorMessage = "¯\\_(ツ)_/¯";

                this.app.getCon().usage(from, "Couldn't buy " + amount + " of " + material.getCleanName() + " for £" + saleResponse.amount + " because " + errorMessage, usage);
                this.app.getCon().warn(from.getName() + " couldn't buy " + amount + " of " + material.getCleanName() + " for £" + saleResponse.amount + " because " + errorMessage);
            }
        }

        return true;
    }
}
