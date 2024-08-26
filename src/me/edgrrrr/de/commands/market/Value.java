package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialValueResponse;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for getting the value of items
 */
public class Value extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public Value(DEPlugin app) {
        super(app, "value", true, Setting.COMMAND_VALUE_ENABLE_BOOLEAN);
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
        int amount = 1;
        switch (args.length) {
            case 1:
                materialName = args[0];
                break;

            case 2:
                materialName = args[0];
                amount = Converter.getInt(args[1]);
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Ensure given material exists
        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(materialName);
        if (marketableMaterial == null) {
            getMain().getConsole().send(sender, LangEntry.MARKET_InvalidItemName.logLevel, LangEntry.MARKET_InvalidItemName.get(getMain()), materialName);
            return true;
        }

        // Create items
        // Get buy & sell value
        ItemStack[] itemStacks = marketableMaterial.getItemStacks(amount);
        MaterialValueResponse buyResponse = marketableMaterial.getManager().getBuyValue(itemStacks);
        MaterialValueResponse sellResponse = marketableMaterial.getManager().getSellValue(itemStacks);
        buyResponse.cleanup();
        sellResponse.cleanup();

        if (buyResponse.isSuccess()) {
            getMain().getConsole().info(sender, LangEntry.VALUE_BuyResponse.get(getMain()), amount, marketableMaterial.getName(), getMain().getConsole().formatMoney(buyResponse.getValue()));

        } else {
            getMain().getConsole().info(sender, LangEntry.VALUE_BuyFailedResponse.get(getMain()), amount, marketableMaterial.getName(), buyResponse.getErrorMessage());
        }

        if (sellResponse.isSuccess()) {
            getMain().getConsole().info(sender, LangEntry.VALUE_SellResponse.get(getMain()), amount, marketableMaterial.getName(), getMain().getConsole().formatMoney(sellResponse.getValue()));

        } else {
            getMain().getConsole().info(sender, LangEntry.VALUE_SellFailedResponse.get(getMain()), amount, marketableMaterial.getName(), sellResponse.getErrorMessage());
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
