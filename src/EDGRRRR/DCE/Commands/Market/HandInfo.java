package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandMaterials;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.materials.MaterialPotionData;
import edgrrrr.dce.player.PlayerInventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for getting information about the item the user is currently holding
 */
public class HandInfo extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public HandInfo(DCEPlugin app) {
        super(app, "handinfo", false, Setting.COMMAND_HAND_INFO_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        switch (args.length) {
            case 0:
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        ItemStack heldItem = PlayerInventoryManager.getHeldItemNotNull(sender);
        Material material = heldItem.getType();
        MaterialData materialData = this.app.getMaterialManager().getMaterial(material.name());

        this.app.getConsole().info(sender, "==[Information for " + materialData.getCleanName() + "]==");
        this.app.getConsole().info(sender, "ID: " + materialData.getMaterialID());
        this.app.getConsole().info(sender, "Type: " + materialData.getType());
        this.app.getConsole().info(sender, "Current Quantity: " + materialData.getQuantity());
        this.app.getConsole().info(sender, "Is Banned: " + !(materialData.getAllowed()));
        if (materialData.getEntityName() != null)
            this.app.getConsole().info(sender, "Entity Name: " + materialData.getEntityName());
        MaterialPotionData pData = materialData.getPotionData();
        if (pData != null) {
            this.app.getConsole().info(sender, "Potion type: " + pData.getType());
            this.app.getConsole().info(sender, "Upgraded potion: " + pData.getUpgraded());
            this.app.getConsole().info(sender, "Extended potion: " + pData.getExtended());
        }

        return true;
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        return false;
    }
}
