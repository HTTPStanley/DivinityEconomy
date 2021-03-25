package edgrrrr.dce.commands.market;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.materials.MaterialPotionData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for getting information about a material
 */
public class Info implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public Info(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("information");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_INFO_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "The market is not enabled.");
            return true;
        }

        String materialName;
        switch (args.length) {
            case 1:
                materialName = args[0];
                break;

            default:
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.help);
                return true;
        }

        MaterialData material = this.app.getMaterialManager().getMaterial(materialName);
        if (material == null) {
            DCEPlugin.CONSOLE.usage(player, "Unknown Item: " + materialName, this.help);
        } else {
            DCEPlugin.CONSOLE.info(player, "==[Information for " + material.getCleanName() + "]==");
            DCEPlugin.CONSOLE.info(player, "ID: " + material.getMaterialID());
            DCEPlugin.CONSOLE.info(player, "Type: " + material.getType());
            DCEPlugin.CONSOLE.info(player, "Current Quantity: " + material.getQuantity());
            DCEPlugin.CONSOLE.info(player, "Is Banned: " + !(material.getAllowed()));
            if (material.getEntityName() != null)
                DCEPlugin.CONSOLE.info(player, "Entity Name: " + material.getEntityName());
            MaterialPotionData pData = material.getPotionData();
            if (pData != null) {
                DCEPlugin.CONSOLE.info(player, "Potion type: " + pData.getType());
                DCEPlugin.CONSOLE.info(player, "Upgraded potion: " + pData.getUpgraded());
                DCEPlugin.CONSOLE.info(player, "Extended potion: " + pData.getExtended());
            }
        }

        return true;
    }
}
