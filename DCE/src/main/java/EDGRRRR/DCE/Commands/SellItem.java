package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Economy.Materials.MaterialData;
import EDGRRRR.DCE.Main.DCEPlugin;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for buying items from the market
 */
public class SellItem implements CommandExecutor {
    private DCEPlugin app;
    private String usage = "/sell <itemName> <amountToSell> or /sell <itemName>";

    public SellItem(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConf().strComSellItem))) {
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
            EconomyResponse priceResponse = this.app.getMat().getMaterialPrice(material, amount, 1.0, false);
            ItemStack iStack = material.getItemStack(amount);
            if (from.getInventory().contains(iStack)) {
                from.getInventory().remove(iStack);
                material.addQuantity(amount);
                this.app.getEco().addCash(from, priceResponse.balance);
                this.app.getCon().info(from, "You have sold " + amount + " * " + material.getCleanName() + " for £" + app.getEco().round(priceResponse.balance) + ". New balance: £" + this.app.getEco().round(app.getEco().getBalance(from)));
            } else {
                this.app.getCon().usage(from, "You do not have " + amount + " * " + material.getCleanName(), usage);
            }
        }

        return true;
    }
}
