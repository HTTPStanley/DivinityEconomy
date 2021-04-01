package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.materials.MaterialPotionData;
import edgrrrr.dce.player.PlayerInventoryManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for getting information about the item the user is currently holding
 */
public class HandInfo implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public HandInfo(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("handinfo");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_HAND_INFO_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "The market is not enabled.");
            return true;
        }

        switch (args.length) {
            case 0:
                break;

            default:
                this.app.getConsole().usage(player, "Invalid number of arguments.", this.help.getUsages());
                return true;
        }


        ItemStack heldItem = PlayerInventoryManager.getHeldItemNotNull(player);
        Material material = heldItem.getType();
        MaterialData materialData = this.app.getMaterialManager().getMaterial(material.name());

        this.app.getConsole().info(player, "==[Information for " + materialData.getCleanName() + "]==");
        this.app.getConsole().info(player, "ID: " + materialData.getMaterialID());
        this.app.getConsole().info(player, "Type: " + materialData.getType());
        this.app.getConsole().info(player, "Current Quantity: " + materialData.getQuantity());
        this.app.getConsole().info(player, "Is Banned: " + !(materialData.getAllowed()));
        if (materialData.getEntityName() != null)
            this.app.getConsole().info(player, "Entity Name: " + materialData.getEntityName());
        MaterialPotionData pData = materialData.getPotionData();
        if (pData != null) {
            this.app.getConsole().info(player, "Potion type: " + pData.getType());
            this.app.getConsole().info(player, "Upgraded potion: " + pData.getUpgraded());
            this.app.getConsole().info(player, "Extended potion: " + pData.getExtended());
        }

        return true;
    }
}
