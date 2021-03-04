package EDGRRRR.DCE.Commands.Market;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Response.ValueResponse;
import EDGRRRR.DCE.Math.Math;
import org.bukkit.Material;
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
            this.app.getConsoleManager().debug("(SellItem)Invalid item amount: " + materialName);

        } else {
            MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
            if (materialData == null) {
                this.app.getConsoleManager().usage(from, "Unknown Item: '" + materialName + "'", "");
                this.app.getConsoleManager().debug("(SellItem)Unknown item search: " + materialName);

            } else {
                Material material = materialData.getMaterial();
                ItemStack[] totalUserMaterials = this.app.getPlayerInventoryManager().getMaterialSlots(from, material);
                int userAmount = this.app.getPlayerInventoryManager().getMaterialCount(totalUserMaterials);

                if (sellAll) {
                    amount = userAmount;
                }

                ItemStack[] itemStacks = this.app.getPlayerInventoryManager().getMaterialSlotsToCount(from, material, amount);
                ValueResponse valueResponse = this.app.getMaterialManager().getSellValue(itemStacks);

                if (valueResponse.isFailure()) {
                    this.app.getConsoleManager().logFailedSale(from, amount, valueResponse.value, materialData.getCleanName(), valueResponse.errorMessage);

                } else {
                    if (userAmount >= amount) {
                        this.app.getPlayerInventoryManager().removeMaterialsFromPlayer(itemStacks);
                        materialData.addQuantity(amount);
                        this.app.getEconomyManager().addCash(from, valueResponse.value);

                        this.app.getConsoleManager().logSale(from, amount, valueResponse.value, materialData.getCleanName());
                    } else {
                        this.app.getConsoleManager().logFailedSale(from, amount, valueResponse.value, materialData.getCleanName(), String.format("you do not have enough of this material. (%d/%d)", userAmount, amount));
                    }
                }
            }
        }

        return true;
    }
}
