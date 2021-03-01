package EDGRRRR.DCE.Commands.Market;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Materials.MaterialValue;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
        if (materialData == null) {
            this.app.getConsoleManager().usage(from, "Unknown Item: " + materialName, usage);
        } else {
            ItemStack[] itemStacks = this.app.getPlayerInventoryManager().createItemStacks(materialData.getMaterial(), amount);
            MaterialValue priceResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
            MaterialValue secondPriceResponse = this.app.getMaterialManager().getSellValue(itemStacks);

            if (priceResponse.getResponseType() == ResponseType.SUCCESS) {
                this.app.getConsoleManager().info(from, "Buy: " + amount + " " + materialData.getCleanName() + " costs £" + this.app.getEconomyManager().round(priceResponse.getValue()));

            } else {
                this.app.getConsoleManager().usage(from, "Couldn't determine buy price of " + amount + " " + materialData.getCleanName() + " because " + priceResponse.getErrorMessage(), usage);
            }

            if (secondPriceResponse.getResponseType() == ResponseType.SUCCESS) {
                this.app.getConsoleManager().info(from, "Sell: " + amount + " " + materialData.getCleanName() + " costs £" + this.app.getEconomyManager().round(secondPriceResponse.getValue()));
            } else {
                this.app.getConsoleManager().usage(from, "Couldn't determine sell price of " + amount + " " + materialData.getCleanName() + " because " + secondPriceResponse.getErrorMessage(), usage);
            }
        }

        return true;
    }
}