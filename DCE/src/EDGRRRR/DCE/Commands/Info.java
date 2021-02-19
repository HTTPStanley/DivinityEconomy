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
        if (!(this.app.getConfig().getBoolean(this.app.getConf().strComInfo))) {
            this.app.getCon().severe(from, "This command is not enabled.");
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
                amount = (int) (double) this.app.getEco().getDouble(args[1]);
                break;

            default:
                this.app.getCon().usage(from, "Invalid number of arguments.", usage);
                return true;
        }

        MaterialData material = this.app.getMat().getMaterial(materialName);
        if (material == null) {
            this.app.getCon().usage(from, "Unknown Item: " + materialName, usage);
        } else {
            EconomyResponse userPriceResponse = this.app.getMat().getMaterialPrice(material, amount, 1.2, true);
            EconomyResponse marketPriceResponse = this.app.getMat().getMaterialPrice(material, amount, 1.0, false);
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

            userPrice = this.app.getEco().round(userPrice);
            marketPrice = this.app.getEco().round(marketPrice);

            this.app.getCon().info(from, "==[" + material.getCleanName() + "]==");
            this.app.getCon().info(from, "ID: " + material.getMaterialID());
            this.app.getCon().info(from, "Material Type: " + material.getType());
            this.app.getCon().info(from, "Buy Price(x" + userAmount + "): " + userPrice);
            this.app.getCon().info(from, "Sell Price(x" + marketAmount+ "): " + marketPrice);
            this.app.getCon().info(from, "Current Quantity: " + material.getQuantity());
            this.app.getCon().info(from, "Is Banned: " + !(material.getAllowed()));
            if (material.getEntityName() != null) this.app.getCon().info(from, "Entity Name: " + material.getEntityName());
            MaterialPotionData pData = material.getPotionData();
            if (pData != null) {
                this.app.getCon().info(from, "Potion type: " + pData.getType());
                this.app.getCon().info(from, "Upgraded potion: " + pData.getUpgraded());
                this.app.getCon().info(from, "Extended potion: " + pData.getExtended());
            }
        }

        return true;
    }
}
