package edgrrrr.dce.commands.market;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.materials.MaterialPotionData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.response.ValueResponse;
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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_INFO_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(from, "This command is not enabled.");
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
                DCEPlugin.CONSOLE.usage(from, "Invalid number of arguments.", this.usage);
                return true;
        }

        MaterialData material = this.app.getMaterialManager().getMaterial(materialName);
        if (material == null) {
            DCEPlugin.CONSOLE.usage(from, "Unknown Item: " + materialName, this.usage);
        } else {
            ValueResponse userResponse = this.app.getMaterialManager().getBuyValue(this.app.getPlayerInventoryManager().createItemStacks(material.getMaterial(), amount));
            ValueResponse marketResponse = this.app.getMaterialManager().getSellValue(this.app.getPlayerInventoryManager().createItemStacks(material.getMaterial(), amount));

            double userPrice;
            double marketPrice;
            int userAmount = amount;
            int marketAmount = amount;

            if (userResponse.isSuccess()) {
                userPrice = userResponse.value;
            } else {
                userPrice = material.getUserPrice();
                userAmount = 1;
            }

            if (marketResponse.isSuccess()) {
                marketPrice = marketResponse.value;
            } else {
                marketPrice = material.getMarketPrice();
                marketAmount = 1;
            }

            userPrice = this.app.getEconomyManager().round(userPrice);
            marketPrice = this.app.getEconomyManager().round(marketPrice);

            DCEPlugin.CONSOLE.info(from, "==[Information for" + material.getCleanName() + "]==");
            DCEPlugin.CONSOLE.info(from, "ID: " + material.getMaterialID());
            DCEPlugin.CONSOLE.info(from, "Type: " + material.getType());
            DCEPlugin.CONSOLE.info(from, "Buy Price(x" + userAmount + "): " + userPrice);
            DCEPlugin.CONSOLE.info(from, "Sell Price(x" + marketAmount + "): " + marketPrice);
            DCEPlugin.CONSOLE.info(from, "Current Quantity: " + material.getQuantity());
            DCEPlugin.CONSOLE.info(from, "Is Banned: " + !(material.getAllowed()));
            if (material.getEntityName() != null)
                DCEPlugin.CONSOLE.info(from, "Entity Name: " + material.getEntityName());
            MaterialPotionData pData = material.getPotionData();
            if (pData != null) {
                DCEPlugin.CONSOLE.info(from, "Potion type: " + pData.getType());
                DCEPlugin.CONSOLE.info(from, "Upgraded potion: " + pData.getUpgraded());
                DCEPlugin.CONSOLE.info(from, "Extended potion: " + pData.getExtended());
            }
        }

        return true;
    }
}
