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
 * A simple ping pong! command
 */
public class HandSell implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/hs | /hs <amount> | /hs max";

    public HandSell(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComHandSell))) {
            this.app.getConsoleManager().severe(player, "This command is not enabled.");
            return true;
        }

        int amountToSell = 1;
        boolean sellAll = false;
        boolean sellHand = false;

        switch (args.length) {
            case 0:
                sellHand = true;
                break;

            case 1:
                if (args[0].equals("max")) {
                    sellAll = true;
                } else {
                    amountToSell = Math.getInt(args[0]);
                }
                break;

            default:
                this.app.getConsoleManager().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amountToSell < 1) {
            this.app.getConsoleManager().usage(player, "Invalid amount.", this.usage);
        } else {
            ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);

            if (heldItem == null) {
                this.app.getConsoleManager().usage(player, "You are not holding any item.", this.usage);

            } else {
                Material material = heldItem.getType();
                String materialName = material.name();
                MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
                int materialCount = this.app.getPlayerInventoryManager().getMaterialCount(this.app.getPlayerInventoryManager().getMaterialSlots(player, material));

                if (sellAll) {
                    amountToSell = materialCount;
                }

                if (sellHand) {
                    amountToSell = heldItem.getAmount();
                }
                if (materialCount < amountToSell) {
                    this.app.getConsoleManager().logFailedSale(player, amountToSell, 0.0, materialData.getCleanName(), String.format("you do not have enough of this material (%d/%d)", materialCount, amountToSell));

                } else {
                    ItemStack[] itemStacks = this.app.getPlayerInventoryManager().getMaterialSlotsToCount(player, material, amountToSell);
                    ValueResponse response = this.app.getMaterialManager().getSellValue(itemStacks);

                    if (response.isSuccess()) {
                        this.app.getPlayerInventoryManager().removeMaterialsFromPlayer(itemStacks);
                        materialData.addQuantity(amountToSell);
                        this.app.getEconomyManager().addCash(player, response.value);
                        double cost = app.getEconomyManager().round(response.value);
                        double balance = app.getEconomyManager().round(app.getEconomyManager().getBalance(player));

                        // Handles console, player message and mail
                        this.app.getConsoleManager().logSale(player, amountToSell, response.value, materialData.getCleanName());
                    } else {
                        // Handles console, player message and mail
                        this.app.getConsoleManager().logFailedSale(player, amountToSell, response.value, materialData.getCleanName(), response.errorMessage);
                    }
                }
            }
        }
        return true;
    }
}
