package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.enchants.EnchantValueResponse;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for valuing enchants
 */
public class EnchantValue extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantValue(DEPlugin app) {
        super(app, "evalue", true, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN);
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
        // The name of the enchant
        // The number of levels to sell
        // If all levels should be sold
        String enchantName;
        int enchantLevels = 1;

        switch (args.length) {
            // If user enters the name
            // sell maximum of enchant given
            case 1:
                enchantName = args[0];
                break;

            // If user enters name and level
            // Sell enchant level times
            case 2:
                enchantName = args[0];
                enchantLevels = Converter.getInt(args[1]);
                break;

            // If wrong number of arguments
            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // If only handling one enchant
        // Ensure enchant exists
        MarketableEnchant enchantData = getMain().getEnchMan().getEnchant(enchantName);
        if (enchantData == null) {
            getMain().getConsole().usage(sender, String.format(LangEntry.MARKET_InvalidEnchantName.get(getMain()), enchantName), this.help.getUsages());
            return true;
        }

        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);

        // Get value
        // Remove enchants, add quantity and add cash
        EnchantValueResponse evr1 = getMain().getEnchMan().getBuyValue(itemStack, enchantName, enchantLevels);
        itemStack.addUnsafeEnchantment(enchantData.getEnchantment(), enchantLevels);
        EnchantValueResponse evr2 = getMain().getEnchMan().getSellValue(itemStack, enchantName, enchantLevels);
        if (evr1.isFailure()) {
            getMain().getConsole().warn(sender, LangEntry.VALUE_BuyFailedResponse.get(getMain()), enchantLevels, enchantData.getName(), evr1.getErrorMessage());
        } else {
            getMain().getConsole().info(sender, LangEntry.VALUE_BuyResponse.get(getMain()), enchantLevels, enchantData.getName(), getMain().getConsole().formatMoney(evr1.getValue()));
        }
        if (evr1.isFailure()) {
            getMain().getConsole().warn(sender, LangEntry.VALUE_SellFailedResponse.get(getMain()), enchantLevels, enchantData.getName(), evr2.getErrorMessage());
        } else {
            getMain().getConsole().info(sender, LangEntry.VALUE_SellResponse.get(getMain()), enchantLevels, enchantData.getName(), getMain().getConsole().formatMoney(evr2.getValue()));
        }

        // Graceful exit :)
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
