package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Economy.Materials.MaterialData;
import EDGRRRR.DCE.Economy.Materials.MaterialPotionData;
import EDGRRRR.DCE.Main.DCEPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A simple ping pong! command
 */
public class Info implements CommandExecutor {
    private DCEPlugin app;
    private String usage = "/info <materialName> <amount> or /info <materialName>";

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
        if (!(app.getConfig().getBoolean(app.getConf().strComInfo))) {
            app.getCon().severe(from, "This command is not enabled.");
            return true;
        }
        
        String materialName = null;
        Integer amount = null;
        switch (args.length) {
            case 1:
                amount = 1;
                materialName = args[0];
                break;
                                
            case 2:
                materialName = args[0];
                amount = (int) (double) app.getEco().getDouble(args[1]);
                break;

            default:
                app.getCon().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        MaterialData material = app.getMat().getMaterial(materialName);
        if (material == null) {
            app.getCon().usage(from, "Unknown Item: " + materialName, usage);
        } else {
            EconomyResponse userPriceResponse = app.getMat().getMaterialPrice(material, amount, 1.2, true);
            EconomyResponse marketPriceResponse = app.getMat().getMaterialPrice(material, amount, 1.0, false);
            double userPrice = 0.0;
            double marketPrice = 0.0;
            int userAmount = amount;
            int marketAmount = amount;

            if (userPriceResponse.type == ResponseType.SUCCESS) {
                userPrice = userPriceResponse.balance;
            } else {
                userPrice = material.getUserPrice();
                userAmount = 1;
            }

            if (marketPriceResponse.type == ResponseType.SUCCESS) {
                marketPrice = marketPriceResponse.balance;
            } else {
                marketPrice = material.getMarketPrice();
                marketAmount = 1;
            }

            userPrice = app.getEco().round(userPrice);
            marketPrice = app.getEco().round(marketPrice);

            app.getCon().info(from, "==[" + material.getCleanName() + "]==");
            app.getCon().info(from, "ID: " + material.getMaterialID());
            app.getCon().info(from, "Material Type: " + material.getType());
            app.getCon().info(from, "Buy Price(x" + userAmount + "): " + userPrice);
            app.getCon().info(from, "Sell Price(x" + marketAmount+ "): " + marketPrice);
            app.getCon().info(from, "Current Quantity: " + material.getQuantity());
            app.getCon().info(from, "Is Banned: " + !(material.getAllowed()));
            if (material.getEntityName() != null) app.getCon().info(from, "Entity Name: " + material.getEntityName());
            MaterialPotionData pData = material.getPotionData();
            if (pData != null) {
                app.getCon().info(from, "Potion type: " + pData.getType());
                app.getCon().info(from, "Upgraded potion: " + pData.getUpgraded());
                app.getCon().info(from, "Extended potion: " + pData.getExtended());
            }            
        }

        return true;
    }
}
