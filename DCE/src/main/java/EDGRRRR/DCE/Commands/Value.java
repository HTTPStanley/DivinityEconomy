package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Economy.Materials.MaterialData;
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
public class Value implements CommandExecutor {
    private DCEPlugin app;
    private String usage = "/value <materialName> <amount> or /value <materialName>";

    public Value(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;

        // Ensure command is enabled
        if (!(app.getConfig().getBoolean(app.getConf().strComValue))) {
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
            EconomyResponse priceResponse = app.getMat().getMaterialPrice(material, amount, app.getEco().tax, true);
            EconomyResponse secondPriceResponse = app.getMat().getMaterialPrice(material, amount, 1.0, false);
            if (priceResponse.type == ResponseType.SUCCESS && secondPriceResponse.type == ResponseType.SUCCESS) {
                app.getCon().info(from, "Buy: " + amount + " * " + material.getCleanName() + " costs £" + app.getEco().round(priceResponse.balance));
                app.getCon().info(from, "Sell: " + amount + " * " + material.getCleanName() + " costs £" + app.getEco().round(secondPriceResponse.balance));
            } else {
                String error = null;
                if (!(priceResponse.type == ResponseType.SUCCESS)) error = priceResponse.errorMessage;
                else error = secondPriceResponse.errorMessage;
                app.getCon().usage(from, "Couldn't determine price of " + material.getCleanName() + " * " + amount + " because " + error, usage);
            }
        }

        return true;
    }
}
