package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Math.Math;
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
    private final DCEPlugin app;
    private final String usage = "/value <materialName> <amount> | /value <materialName>";

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
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComValue))) {
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
            EconomyResponse priceResponse = this.app.getMaterialManager().getMaterialPrice(material, amount, this.app.getEconomyManager().tax, true);
            EconomyResponse secondPriceResponse = this.app.getMaterialManager().getMaterialPrice(material, amount, 1.0, false);
            if (priceResponse.type == ResponseType.SUCCESS && secondPriceResponse.type == ResponseType.SUCCESS) {
                this.app.getConsoleManager().info(from, "Buy: " + amount + " * " + material.getCleanName() + " costs £" + this.app.getEconomyManager().round(priceResponse.balance));
                this.app.getConsoleManager().info(from, "Sell: " + amount + " * " + material.getCleanName() + " costs £" + this.app.getEconomyManager().round(secondPriceResponse.balance));
            } else {
                String error;
                if (!(priceResponse.type == ResponseType.SUCCESS)) error = priceResponse.errorMessage;
                else error = secondPriceResponse.errorMessage;
                this.app.getConsoleManager().usage(from, "Couldn't determine price of " + material.getCleanName() + " * " + amount + " because " + error, usage);
            }
        }

        return true;
    }
}
