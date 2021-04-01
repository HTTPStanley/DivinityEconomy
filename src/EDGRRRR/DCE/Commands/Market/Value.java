package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
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
 * A command for getting the value of items
 */
public class Value implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public Value(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("value");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_VALUE_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "The market is not enabled.");
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
                this.app.getConsole().usage(player, "Invalid number of arguments.", this.help.getUsages());
                return true;
        }

        MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
        if (materialData == null) {
            this.app.getConsole().usage(player, "Unknown Item: " + materialName, this.help.getUsages());
        } else {
            ItemStack[] itemStacks = PlayerInventoryManager.createItemStacks(materialData.getMaterial(), amount);
            ValueResponse buyResponse = this.app.getMaterialManager().getBuyValue(itemStacks);
            ValueResponse sellResponse = this.app.getMaterialManager().getSellValue(itemStacks);

            if (buyResponse.isSuccess()) {
                this.app.getConsole().info(player, String.format("Buy: %d %s costs £%,.2f", amount, materialData.getCleanName(), buyResponse.value));

            } else {
                this.app.getConsole().usage(player, String.format("Couldn't determine buy price of %d %s because %s", amount, materialData.getCleanName(), buyResponse.errorMessage), this.help.getUsages());
            }

            if (sellResponse.isSuccess()) {
                this.app.getConsole().info(player, String.format("Sell: %d %s costs £%,.2f", amount, materialData.getCleanName(), sellResponse.value));

            } else {
                this.app.getConsole().usage(player, String.format("Couldn't determine buy price of %d %s because %s", amount, materialData.getCleanName(), sellResponse.errorMessage), this.help.getUsages());
            }
        }

        return true;
    }
}
