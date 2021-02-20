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
    private final DCEPlugin app;
    private final String usage = "/sell <itemName> <amountToSell> | /sell <itemName> | /sell <itemName> max";

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

        String materialName;
        boolean sellAll = false;
        int amount = 1;

        switch (args.length) {
            // Just material, used default amount of 1
            case 1:
                materialName = args[0];
                break;

            // Material & Amount
            case 2:
                materialName = args[0];
                if (args[1].equals("max")) {
                    sellAll = true;
                } else {
                    amount = (int) this.app.getEco().getDouble(args[1]);
                }
                break;

            default:
                this.app.getCon().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        if (amount < 1) {
            this.app.getCon().usage(from, "Invalid amount.", this.usage);
        } else {
            MaterialData material = this.app.getMat().getMaterial(materialName);
            if (material == null) {
                this.app.getCon().usage(from, "Unknown Item: '" + materialName + "'", "");
            } else {
                ItemStack[] itemStacks = this.app.getMat().getMaterialSlots(from, material.getMaterial());
                int userAmount = this.app.getMat().getMaterialCount(itemStacks);

                if (sellAll) {
                    amount = userAmount;
                }

                if (!material.getAllowed()) {
                    this.app.getCon().usage(from, "Cannot sell " + material.getCleanName() + " because it is not allowed to be bought or sold", this.usage);
                    this.app.getCon().warn(from.getName() + " couldn't sell " + material.getMaterialID() + " because it is not allowed to be bought or sold");
                } else {
                    EconomyResponse priceResponse = this.app.getMat().getMaterialPrice(material, amount, 1.0, false);
                    double cost = app.getEco().round(priceResponse.balance);
                    double balance = this.app.getEco().round(app.getEco().getBalance(from));
                    if (userAmount >= amount) {
                        this.app.getMat().removeMaterialsFromPlayer(itemStacks, amount);
                        material.addQuantity(amount);
                        this.app.getEco().addCash(from, priceResponse.balance);
                        this.app.getCon().info(from, "Sold " + amount + " " + material.getCleanName() + " for £" + cost + ". New balance: £" + balance);
                        this.app.getCon().info(from.getName() + " sold " + amount + " " + material.getMaterialID() + " for £" + cost);
                    } else {
                        this.app.getCon().usage(from, "You do not have " + amount + " " + material.getCleanName(), usage);
                        this.app.getCon().warn(from.getName() + " couldn't sell " + amount + " " + material.getMaterialID() + " for £" + cost + " because they only had " + userAmount + " / " + amount);
                    }
                }
            }
        }
        return true;
    }
}
