package EDGRRRR.DCE.Commands.Market;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Materials.MaterialPotionData;
import EDGRRRR.DCE.Materials.MaterialValueResponse;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse;
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
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComHandInfo))) {
            this.app.getConsoleManager().severe(player, "This command is not enabled.");
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
                this.app.getConsoleManager().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amount < 0) {
            this.app.getConsoleManager().usage(player, "Invalid amount.", this.usage);
        } else {
            ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);
            if (heldItem == null) {
                this.app.getConsoleManager().usage(player, "You are not holding any item.", this.usage);
            } else {
                Material material = heldItem.getType();
                MaterialData materialData = this.app.getMaterialManager().getMaterial(material.name());
                ItemStack[] itemStacks = this.app.getPlayerInventoryManager().createItemStacks(material, amount);
                MaterialValueResponse userResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
                MaterialValueResponse marketResponse = this.app.getMaterialManager().getSellValue(itemStacks);

                double userPrice;
                double marketPrice;
                int userAmount = amount;
                int marketAmount = amount;

                if (userResponse.getResponseType() == EconomyResponse.ResponseType.SUCCESS) {
                    userPrice = userResponse.getValue();
                } else {
                    userPrice = materialData.getUserPrice();
                    userAmount = 1;
                }

                if (marketResponse.getResponseType() == EconomyResponse.ResponseType.SUCCESS) {
                    marketPrice = marketResponse.getValue();
                } else {
                    marketPrice = materialData.getMarketPrice();
                    marketAmount = 1;
                }

                userPrice = this.app.getEconomyManager().round(userPrice);
                marketPrice = this.app.getEconomyManager().round(marketPrice);

                this.app.getConsoleManager().info(player, "==[Information for" + materialData.getCleanName() + "]==");
                this.app.getConsoleManager().info(player, "ID: " + materialData.getMaterialID());
                this.app.getConsoleManager().info(player, "Type: " + materialData.getType());
                this.app.getConsoleManager().info(player, "Buy Price(x" + userAmount + "): " + userPrice);
                this.app.getConsoleManager().info(player, "Sell Price(x" + marketAmount + "): " + marketPrice);
                this.app.getConsoleManager().info(player, "Current Quantity: " + materialData.getQuantity());
                this.app.getConsoleManager().info(player, "Is Banned: " + !(materialData.getAllowed()));
                if (materialData.getEntityName() != null)
                    this.app.getConsoleManager().info(player, "Entity Name: " + materialData.getEntityName());
                MaterialPotionData pData = materialData.getPotionData();
                if (pData != null) {
                    this.app.getConsoleManager().info(player, "Potion type: " + pData.getType());
                    this.app.getConsoleManager().info(player, "Upgraded potion: " + pData.getUpgraded());
                    this.app.getConsoleManager().info(player, "Extended potion: " + pData.getExtended());
                }
            }
        }
        return true;
    }
}
