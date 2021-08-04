package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.player.PlayerManager;
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
    public HandInfo(DEPlugin app) {
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
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        ItemStack heldItem = PlayerManager.getHeldItem(sender, new ItemStack(Material.AIR, 0));
        Material material = heldItem.getType();
        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(material.name());

        this.getMain().getConsole().info(sender, "==[Information for " + marketableMaterial.getCleanName() + "]==");
        this.getMain().getConsole().info(sender, "TYPE: " + marketableMaterial.getManager().getType());
        this.getMain().getConsole().info(sender, "ID: " + marketableMaterial.getID());
        this.getMain().getConsole().info(sender, "Current Quantity: " + marketableMaterial.getQuantity());
        this.getMain().getConsole().info(sender, "Is Banned: " + !(marketableMaterial.getAllowed()));
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
