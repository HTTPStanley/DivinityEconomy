package edgrrrr.dce.commands.market;

import edgrrrr.dce.config.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.materials.MaterialPotionData;
import edgrrrr.dce.response.ValueResponse;
import edgrrrr.dce.math.Math;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HandInfo implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/handinfo | /handinfo <amount>";

    public HandInfo(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_HAND_INFO_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        int amount;
        switch (args.length) {
            case 0:
                amount = 1;
                break;

            case 1:
                amount = Math.getInt(args[0]);
                break;

            default:
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amount < 0) {
            DCEPlugin.CONSOLE.usage(player, "Invalid amount.", this.usage);
        } else {
            ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);
            if (heldItem == null) {
                DCEPlugin.CONSOLE.usage(player, "You are not holding any item.", this.usage);
            } else {
                Material material = heldItem.getType();
                MaterialData materialData = this.app.getMaterialManager().getMaterial(material.name());
                ItemStack[] itemStacks = this.app.getPlayerInventoryManager().createItemStacks(material, amount);
                ValueResponse userResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
                ValueResponse marketResponse = this.app.getMaterialManager().getSellValue(itemStacks);

                double userPrice;
                double marketPrice;
                int userAmount = amount;
                int marketAmount = amount;

                if (userResponse.isSuccess()) {
                    userPrice = userResponse.value;
                } else {
                    userPrice = materialData.getUserPrice();
                    userAmount = 1;
                }

                if (marketResponse.isSuccess()) {
                    marketPrice = marketResponse.value;
                } else {
                    marketPrice = materialData.getMarketPrice();
                    marketAmount = 1;
                }

                userPrice = this.app.getEconomyManager().round(userPrice);
                marketPrice = this.app.getEconomyManager().round(marketPrice);

                DCEPlugin.CONSOLE.info(player, "==[Information for" + materialData.getCleanName() + "]==");
                DCEPlugin.CONSOLE.info(player, "ID: " + materialData.getMaterialID());
                DCEPlugin.CONSOLE.info(player, "Type: " + materialData.getType());
                DCEPlugin.CONSOLE.info(player, "Buy Price(x" + userAmount + "): " + userPrice);
                DCEPlugin.CONSOLE.info(player, "Sell Price(x" + marketAmount + "): " + marketPrice);
                DCEPlugin.CONSOLE.info(player, "Current Quantity: " + materialData.getQuantity());
                DCEPlugin.CONSOLE.info(player, "Is Banned: " + !(materialData.getAllowed()));
                if (materialData.getEntityName() != null)
                    DCEPlugin.CONSOLE.info(player, "Entity Name: " + materialData.getEntityName());
                MaterialPotionData pData = materialData.getPotionData();
                if (pData != null) {
                    DCEPlugin.CONSOLE.info(player, "Potion type: " + pData.getType());
                    DCEPlugin.CONSOLE.info(player, "Upgraded potion: " + pData.getUpgraded());
                    DCEPlugin.CONSOLE.info(player, "Extended potion: " + pData.getExtended());
                }
            }
        }
        return true;
    }
}
