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
    private String usage = "/buy <itemName> <amountToBuy>";

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
        if (!(app.getConfig().getBoolean(app.getConf().strComBuyItem))) {
            app.getCon().severe(from, "This command is not enabled.");
            return true;
        }

        String materialName = null;
        Integer amount = null;
        switch (args.length) {
            case 2:
                // Get the material args
                materialName = args[0];
                amount = (int) (double) app.getEco().getDouble(args[1]);
                break;

            default:
                app.getCon().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        MaterialData material = app.getMat().getMaterial(materialName);
        if (material == null) {
            app.getCon().usage(from, "Unknown Item: '" + materialName + "'", "");
            return true;
        } else {
            EconomyResponse priceResponse = app.getMat().getMaterialPrice(material, amount, app.getEco().tax, true);
            EconomyResponse saleResponse = app.getEco().remCash(from, priceResponse.balance);
            if (saleResponse.type == ResponseType.SUCCESS && priceResponse.type == ResponseType.SUCCESS) {
                ItemStack iStack = material.getItemStack(amount);
                from.getInventory().addItem(iStack);
                material.remQuantity(amount);
                app.getCon().info(from, "You bought " + amount + " of " + material.getCleanName() + " for £" + saleResponse.amount + ". New Balance: £" + saleResponse.balance);
                app.getCon().info(from.getName() + "Bought " + amount + " of " + material.getCleanName() + " for £" + saleResponse.amount);
            }
            else {
                String errorMessage = null;
                if (saleResponse.type == ResponseType.FAILURE) errorMessage = saleResponse.errorMessage;
                else if (priceResponse.type == ResponseType.FAILURE) errorMessage = priceResponse.errorMessage;
                else errorMessage = "¯\\_(ツ)_/¯";

                app.getCon().usage(from, "Couldn't buy " + amount + " of " + material.getCleanName() + " for £" + saleResponse.amount + " because " + errorMessage, usage);
                app.getCon().warn(from.getName() + " couldn't buy " + amount + " of " + material.getCleanName() + " for £" + saleResponse.amount + " because " + errorMessage);
            }
        }

        return true;
    }
}
