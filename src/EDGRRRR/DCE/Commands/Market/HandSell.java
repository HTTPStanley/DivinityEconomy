package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
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
    private final Help help;

    public HandSell(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("handsell");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "The market is not enabled.");
            return true;
        }

        int amountToSell = -1;

        switch (args.length) {
            case 0:
                amountToSell = 1;
                break;

            case 1:
                amountToSell = Math.getInt(args[0]);
                break;

            default:
                this.app.getConsole().usage(player, "Invalid number of arguments.", this.help.getUsages());
                return true;
        }

        if (amountToSell < 1) {
            this.app.getConsole().usage(player, "Invalid amount.", this.help.getUsages());
        } else {
            ItemStack heldItem = PlayerInventoryManager.getHeldItem(player);

            if (heldItem == null) {
                this.app.getConsole().usage(player, "You are not holding any item.", this.help.getUsages());

            } else {
                Material material = heldItem.getType();
                String materialName = material.name();
                MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
                int materialCount = PlayerInventoryManager.getMaterialCount(PlayerInventoryManager.getMaterialSlots(player, material));

                if (materialCount < amountToSell) {
                    this.app.getConsole().logFailedSale(player, amountToSell, materialData.getCleanName(), String.format("you do not have enough of this material (%d/%d)", materialCount, amountToSell));

                } else {
                    ItemStack[] itemStacks = PlayerInventoryManager.getMaterialSlotsToCount(player, material, amountToSell);
                    ItemStack[] itemStacksClone = PlayerInventoryManager.cloneItems(itemStacks);
                    ValueResponse response = this.app.getMaterialManager().getSellValue(itemStacks);

                    if (response.isSuccess()) {
                        PlayerInventoryManager.removeMaterialsFromPlayer(itemStacks);

                        EconomyResponse economyResponse = this.app.getEconomyManager().addCash(player, response.value);
                        if (!economyResponse.transactionSuccess()) {
                            PlayerInventoryManager.addItemsToPlayer(player, itemStacksClone);
                            // Handles console, player message and mail
                            this.app.getConsole().logFailedSale(player, amountToSell, materialData.getCleanName(), economyResponse.errorMessage);
                        } else {
                            materialData.addQuantity(amountToSell);
                            // Handles console, player message and mail
                            this.app.getConsole().logSale(player, amountToSell, response.value, materialData.getCleanName());
                        }
                    } else {
                        // Handles console, player message and mail
                        this.app.getConsole().logFailedSale(player, amountToSell, materialData.getCleanName(), response.errorMessage);
                    }
                }
            }
        }
        return true;
    }
}
