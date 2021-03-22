package edgrrrr.dce.commands.market;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.ValueResponse;
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

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_VALUE_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "The market is not enabled.");
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
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", usage);
                return true;
        }

        MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
        if (materialData == null) {
            DCEPlugin.CONSOLE.usage(player, "Unknown Item: " + materialName, usage);
        } else {
            ItemStack[] itemStacks = PlayerInventoryManager.createItemStacks(materialData.getMaterial(), amount);
            ValueResponse priceResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
            ValueResponse secondPriceResponse = this.app.getMaterialManager().getSellValue(itemStacks);

            if (priceResponse.isSuccess()) {
                DCEPlugin.CONSOLE.info(player, "Buy: " + amount + " " + materialData.getCleanName() + " costs £" + this.app.getEconomyManager().round(priceResponse.value));

            } else {
                DCEPlugin.CONSOLE.usage(player, "Couldn't determine buy price of " + amount + " " + materialData.getCleanName() + " because " + priceResponse.errorMessage, usage);
            }

            if (secondPriceResponse.isSuccess()) {
                DCEPlugin.CONSOLE.info(player, "Sell: " + amount + " " + materialData.getCleanName() + " costs £" + this.app.getEconomyManager().round(secondPriceResponse.value));
            } else {
                DCEPlugin.CONSOLE.usage(player, "Couldn't determine sell price of " + amount + " " + materialData.getCleanName() + " because " + secondPriceResponse.errorMessage, usage);
            }
        }

        return true;
    }
}
