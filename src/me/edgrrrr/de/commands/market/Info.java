package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.materials.MaterialData;
import me.edgrrrr.de.materials.MaterialPotionData;
import org.bukkit.entity.Player;

/**
 * A command for getting information about a material
 */
public class Info extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public Info(DEPlugin app) {
        super(app, "information", true, Setting.COMMAND_INFO_ENABLE_BOOLEAN);
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
        String materialName;
        switch (args.length) {
            case 1:
                materialName = args[0];
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        MaterialData material = this.getMain().getMaterialManager().getMaterial(materialName);
        if (material == null) {
            this.getMain().getConsole().usage(sender, String.format("Unknown Item: %s", materialName), this.help.getUsages());
        } else {
            this.getMain().getConsole().info(sender, "==[Information for %s]==", material.getCleanName());
            this.getMain().getConsole().info(sender, "ID: %s", material.getMaterialID());
            this.getMain().getConsole().info(sender, "Type: %s", material.getType());
            this.getMain().getConsole().info(sender, "Current Quantity: %s", material.getQuantity());
            this.getMain().getConsole().info(sender, "Is Banned: %s", !(material.getAllowed()));
            if (material.getEntityName() != null)
                this.getMain().getConsole().info(sender, "Entity Name: %s", material.getEntityName());
            MaterialPotionData pData = material.getPotionData();
            if (pData != null) {
                this.getMain().getConsole().info(sender, "Potion type: %s", pData.getType());
                this.getMain().getConsole().info(sender, "Upgraded potion: %s", pData.getUpgraded());
                this.getMain().getConsole().info(sender, "Extended potion: %s", pData.getExtended());
            }
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
        return this.onPlayerCommand(null, args);
    }
}
