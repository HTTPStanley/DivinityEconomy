package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import org.bukkit.entity.Player;

/**
 * A command for getting information about enchants
 */
public class EnchantInfo extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantInfo(DEPlugin app) {
        super(app, "einfo", true, Setting.COMMAND_E_INFO_ENABLE_BOOLEAN);
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
        String enchantName;
        switch (args.length) {
            case 1:
                enchantName = args[0];
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        MarketableEnchant enchantData = getMain().getEnchMan().getEnchant(enchantName);
        if (enchantData == null) {
            getMain().getConsole().usage(sender, LangEntry.MARKET_UnknownEnchant.get(getMain(), enchantName), this.help.getUsages());
        } else {
            getMain().getConsole().info(sender, LangEntry.INFO_InformationFor.get(getMain()), enchantData.getName());
            getMain().getConsole().info(sender, LangEntry.INFO_IDInformation.get(getMain()), enchantData.getID());
            getMain().getConsole().info(sender, LangEntry.INFO_CurrentQuantityInformation.get(getMain()), enchantData.getQuantity());
            getMain().getConsole().info(sender, LangEntry.INFO_IsBannedInformation.get(getMain()), !(enchantData.getAllowed()));
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
