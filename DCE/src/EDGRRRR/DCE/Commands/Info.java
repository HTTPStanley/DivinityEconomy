package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Materials.MaterialPotionData;
import EDGRRRR.DCE.Materials.MaterialValue;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A simple ping pong! command
 */
public class Info implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/info <materialName> <amount> | /info <materialName>";

    public Info(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComInfo))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
            return true;
        }

        String materialName;
        int amount = 1;
        switch (args.length) {
            case 1:
                materialName = args[0];
                break;

            case 2:
                materialName = args[0];
                amount = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsoleManager().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        MaterialData material = this.app.getMaterialManager().getMaterial(materialName);
        if (material == null) {
            this.app.getConsoleManager().usage(from, "Unknown Item: " + materialName, usage);
        } else {
            MaterialValue userResponse = this.app.getMaterialManager().getBuyValue(this.app.getPlayerInventoryManager().createItemStacks(material.getMaterial(), amount));
            MaterialValue marketResponse = this.app.getMaterialManager().getSellValue(this.app.getPlayerInventoryManager().createItemStacks(material.getMaterial(), amount));

            double userPrice;
            double marketPrice;
            int userAmount = amount;
            int marketAmount = amount;

            if (userResponse.getResponseType() == ResponseType.SUCCESS) {
                userPrice = userResponse.getValue();
            } else {
                userPrice = material.getUserPrice();
                userAmount = 1;
            }

            if (marketResponse.getResponseType() == ResponseType.SUCCESS) {
                marketPrice = marketResponse.getValue();
            } else {
                marketPrice = material.getMarketPrice();
                marketAmount = 1;
            }

            userPrice = this.app.getEconomyManager().round(userPrice);
            marketPrice = this.app.getEconomyManager().round(marketPrice);

            this.app.getConsoleManager().info(from, "==[" + material.getCleanName() + "]==");
            this.app.getConsoleManager().info(from, "ID: " + material.getMaterialID());
            this.app.getConsoleManager().info(from, "Material Type: " + material.getType());
            this.app.getConsoleManager().info(from, "Buy Price(x" + userAmount + "): " + userPrice);
            this.app.getConsoleManager().info(from, "Sell Price(x" + marketAmount+ "): " + marketPrice);
            this.app.getConsoleManager().info(from, "Current Quantity: " + material.getQuantity());
            this.app.getConsoleManager().info(from, "Is Banned: " + !(material.getAllowed()));
            if (material.getEntityName() != null) this.app.getConsoleManager().info(from, "Entity Name: " + material.getEntityName());
            MaterialPotionData pData = material.getPotionData();
            if (pData != null) {
                this.app.getConsoleManager().info(from, "Potion type: " + pData.getType());
                this.app.getConsoleManager().info(from, "Upgraded potion: " + pData.getUpgraded());
                this.app.getConsoleManager().info(from, "Extended potion: " + pData.getExtended());
            }
        }

        return true;
    }
}
