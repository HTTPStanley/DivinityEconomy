package EDGRRRR.DCE.Commands.Market;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Materials.MaterialValue;
import EDGRRRR.DCE.Math.Math;
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
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComSellItem))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
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
                    amount = Math.getInt(args[1]);
                }
                break;

            default:
                this.app.getConsoleManager().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        if (amount < 1) {
            this.app.getConsoleManager().usage(from, "Invalid amount.", this.usage);
        } else {
            MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
            if (materialData == null) {
                this.app.getConsoleManager().usage(from, "Unknown Item: '" + materialName + "'", "");

            } else {
                ItemStack[] totalUserMaterials = this.app.getPlayerInventoryManager().getMaterialSlots(from, materialData.getMaterial());
                int userAmount = this.app.getPlayerInventoryManager().getMaterialCount(totalUserMaterials);

                if (sellAll) {
                    amount = userAmount;
                }

                if (!materialData.getAllowed()) {
                    this.app.getConsoleManager().usage(from, "Cannot sell " + materialData.getCleanName() + " because it is not allowed to be bought or sold", this.usage);
                    this.app.getConsoleManager().warn(from.getName() + " couldn't sell " + materialData.getMaterialID() + " because it is not allowed to be bought or sold");

                } else {
                    ItemStack[] itemStacks = this.app.getPlayerInventoryManager().getMaterialSlotsToCount(from, materialData.getMaterial(), amount);
                    MaterialValue materialValue = this.app.getMaterialManager().getSellValue(itemStacks);
                    double cost = app.getEconomyManager().round(materialValue.getValue());
                    double balance = this.app.getEconomyManager().round(app.getEconomyManager().getBalance(from));
                    if (userAmount >= amount) {
                        this.app.getPlayerInventoryManager().removeMaterialsFromPlayer(itemStacks);
                        materialData.addQuantity(amount);
                        this.app.getEconomyManager().addCash(from, materialValue.getValue());
                        this.app.getConsoleManager().info(from, "Sold " + amount + " " + materialData.getCleanName() + " for £" + cost + ". New balance: £" + balance);
                        this.app.getConsoleManager().info(from.getName() + " sold " + amount + " " + materialData.getMaterialID() + " for £" + cost);

                    } else {
                        this.app.getConsoleManager().usage(from, "You do not have " + amount + " " + materialData.getCleanName(), this.usage);
                        this.app.getConsoleManager().warn(from.getName() + " couldn't sell " + amount + " " + materialData.getMaterialID() + " for £" + cost + " because they only had " + userAmount + " / " + amount);
                    }
                }
            }
        }
        return true;
    }
}
