package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.enchants.MarketableEnchant;
import org.divinitycraft.divinityeconomy.utils.Converter;
import org.bukkit.entity.Player;

/**
 * A command for setting the stock of a material
 */
public class BanEnchant extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public BanEnchant(DEPlugin app) {
        super(app, "banenchant", true, Setting.COMMAND_BAN_ENCHANT_ENABLE_BOOLEAN);
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
        MarketableEnchant enchantData;
        boolean isBanned;
        switch (args.length) {
            case 2:
                enchantData = getMain().getEnchMan().getEnchant(args[0]);
                isBanned = Converter.getBoolean(args[1]);
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure material exists
        if (enchantData == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidEnchantName.logLevel, LangEntry.MARKET_InvalidItemName.get(getMain()), args[0]);
            return true;
        }


        enchantData.getManager().setAllowed(enchantData, !isBanned);
        getMain().getConsole().send(sender, LangEntry.STOCK_BannedStatusChanged.logLevel, LangEntry.STOCK_BannedStatusChanged.get(getMain()), enchantData.getName(), isBanned);

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
