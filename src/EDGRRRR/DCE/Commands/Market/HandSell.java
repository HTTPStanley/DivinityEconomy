package edgrrrr.dce.commands.market;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.ValueResponse;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for selling items in the users hand
 */
public class HandSell implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/hs | /hs <amount>";

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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "The market is not enabled.");
            return true;
        }

        int amountToSell = 1;

        switch (args.length) {
            case 1:
                amountToSell = Math.getInt(args[0]);
                break;

            default:
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amountToSell < 1) {
            DCEPlugin.CONSOLE.usage(player, "Invalid amount.", this.usage);
        } else {
            ItemStack heldItem = PlayerInventoryManager.getHeldItem(player);

            if (heldItem == null) {
                DCEPlugin.CONSOLE.usage(player, "You are not holding any item.", this.usage);

            } else {
                Material material = heldItem.getType();
                String materialName = material.name();
                MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
                int materialCount = PlayerInventoryManager.getMaterialCount(PlayerInventoryManager.getMaterialSlots(player, material));

                if (materialCount < amountToSell) {
                    DCEPlugin.CONSOLE.logFailedSale(player, amountToSell, materialData.getCleanName(), String.format("you do not have enough of this material (%d/%d)", materialCount, amountToSell));

                } else {
                    ItemStack[] itemStacks = PlayerInventoryManager.getMaterialSlotsToCount(player, material, amountToSell);
                    ValueResponse response = this.app.getMaterialManager().getSellValue(itemStacks);

                    if (response.isSuccess()) {
                        PlayerInventoryManager.removeMaterialsFromPlayer(itemStacks);
                        materialData.addQuantity(amountToSell);
                        if (!this.app.getEconomyManager().addCash(player, response.value).transactionSuccess()) {DCEPlugin.CONSOLE.severe(player,"An error occurred on funding your account, show this message to an admin.");}

                        // Handles console, player message and mail
                        DCEPlugin.CONSOLE.logSale(player, amountToSell, response.value, materialData.getCleanName());
                    } else {
                        // Handles console, player message and mail
                        DCEPlugin.CONSOLE.logFailedSale(player, amountToSell, materialData.getCleanName(), response.errorMessage);
                    }
                }
            }
        }
        return true;
    }
}
