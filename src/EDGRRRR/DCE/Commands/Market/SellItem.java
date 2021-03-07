package edgrrrr.dce.commands.market;

import edgrrrr.dce.config.Setting;
import edgrrrr.dce.main.DCEPlugin;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.response.ValueResponse;
import edgrrrr.dce.math.Math;
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

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_SELL_ITEM_ENABLE_BOOLEAN.path()))) {
            this.app.getConsoleManager().severe(player, "This command is not enabled.");
            return true;
        }

        String materialName;
        boolean sellAll = false;
        int amountToSell = 1;

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
                    amountToSell = Math.getInt(args[1]);
                }
                break;

            default:
                this.app.getConsoleManager().usage(player, "Invalid number of arguments.", usage);
                return true;
        }

        if (amountToSell < 1) {
            this.app.getConsoleManager().usage(player, "Invalid amount.", this.usage);
            this.app.getConsoleManager().debug("(SellItem)Invalid item amount: " + materialName);

        } else {
            MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
            if (materialData == null) {
                this.app.getConsoleManager().usage(player, "Unknown Item: '" + materialName + "'", "");
                this.app.getConsoleManager().debug("(SellItem)Unknown item search: " + materialName);

            } else {
                Material material = materialData.getMaterial();
                ItemStack[] totalUserMaterials = this.app.getPlayerInventoryManager().getMaterialSlots(player, material);
                int userAmount = this.app.getPlayerInventoryManager().getMaterialCount(totalUserMaterials);

                if (sellAll) {
                    amountToSell = userAmount;
                }

                ItemStack[] itemStacks = this.app.getPlayerInventoryManager().getMaterialSlotsToCount(player, material, amountToSell);
                ValueResponse valueResponse = this.app.getMaterialManager().getSellValue(itemStacks);

                if (valueResponse.isFailure()) {
                    this.app.getConsoleManager().logFailedSale(player, amountToSell, valueResponse.value, materialData.getCleanName(), valueResponse.errorMessage);

                } else {
                    if (userAmount >= amountToSell) {
                        this.app.getPlayerInventoryManager().removeMaterialsFromPlayer(itemStacks);
                        materialData.addQuantity(amountToSell);
                        this.app.getEconomyManager().addCash(player, valueResponse.value);

                        this.app.getConsoleManager().logSale(player, amountToSell, valueResponse.value, materialData.getCleanName());
                    } else {
                        this.app.getConsoleManager().logFailedSale(player, amountToSell, valueResponse.value, materialData.getCleanName(), String.format("you do not have enough of this material. (%d/%d)", userAmount, amountToSell));
                    }
                }
            }
        }

        return true;
    }
}
