package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandMaterials;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.player.PlayerManager;
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
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        ItemStack heldItem = PlayerManager.getHeldItem(sender, new ItemStack(Material.AIR, 0));
        Material material = heldItem.getType();
        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(material.name());

        getMain().getConsole().info(sender, LangEntry.INFO_InformationFor.get(getMain()), marketableMaterial.getName());
        getMain().getConsole().info(sender, LangEntry.INFO_TypeInformation.get(getMain()), marketableMaterial.getManager().getType());
        getMain().getConsole().info(sender, LangEntry.INFO_IDInformation.get(getMain()), marketableMaterial.getID());
        getMain().getConsole().info(sender, LangEntry.INFO_CurrentQuantityInformation.get(getMain()), marketableMaterial.getQuantity());
        getMain().getConsole().info(sender, LangEntry.INFO_IsBannedInformation.get(getMain()), !(marketableMaterial.getAllowed()));
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
