package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
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

        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(materialName);
        if (marketableMaterial == null) {
            this.getMain().getConsole().usage(sender, String.format("Unknown Item: %s", materialName), this.help.getUsages());
        } else {
            this.getMain().getConsole().info(sender, "==[Information for %s]==", marketableMaterial.getCleanName());
            this.getMain().getConsole().info(sender, "Type: %s", marketableMaterial.getManager().getType());
            this.getMain().getConsole().info(sender, "ID: %s", marketableMaterial.getID());
            this.getMain().getConsole().info(sender, "Current Quantity: %s", marketableMaterial.getQuantity());
            this.getMain().getConsole().info(sender, "Is Banned: %s", !(marketableMaterial.getAllowed()));
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
