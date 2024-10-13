package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
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
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(materialName);
        if (marketableMaterial == null) {
            getMain().getConsole().usage(sender, LangEntry.MARKET_UnknownItem.get(getMain(), materialName), this.help.getUsages());
        } else {
            getMain().getConsole().info(sender, LangEntry.INFO_InformationFor.get(getMain()), marketableMaterial.getName());
            getMain().getConsole().info(sender, LangEntry.INFO_TypeInformation.get(getMain()), marketableMaterial.getManager().getType());
            getMain().getConsole().info(sender, LangEntry.INFO_IDInformation.get(getMain()), marketableMaterial.getID());
            getMain().getConsole().info(sender, LangEntry.INFO_CurrentQuantityInformation.get(getMain()), marketableMaterial.getQuantity());
            getMain().getConsole().info(sender, LangEntry.INFO_IsBannedInformation.get(getMain()), !(marketableMaterial.getAllowed()));
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
