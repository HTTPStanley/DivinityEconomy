package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.ItemManager;
import me.edgrrrr.de.market.items.enchants.EnchantValueResponse;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for selling enchants on held items
 */
public class EnchantHandSell extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandSell(DEPlugin app) {
        super(app, "esell", false, Setting.COMMAND_E_SELL_ENABLE_BOOLEAN);
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
        boolean sellAllLevels = false;
        boolean sellAllEnchants = false;

        switch (args.length) {
            case 1:
                enchantName = args[0];
                if (enchantName.equalsIgnoreCase("max")) {
                    sellAllEnchants = true;
                    sellAllLevels = true;
                }
                break;


            // If user enters name and level
            // Sell enchant level times
            case 2:
                enchantName = args[0];
                enchantLevels = Converter.getInt(args[1]);
                break;

            // If wrong number of arguments
            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // get the item the user is holding.
        // ensure it is not null
        ItemStack heldItem = PlayerManager.getHeldItem(sender);
        if (heldItem == null) {
            this.getMain().getConsole().usage(sender, CommandResponse.InvalidItemHeld.message, this.help.getUsages());
            return true;
        }

        // Ensure item is enchanted
        if (!this.getMain().getEnchMan().isEnchanted(heldItem)) {
            this.getMain().getConsole().usage(sender, CommandResponse.InvalidItemHeld.message, this.help.getUsages());
            return true;
        }

        // Create clone in-case of reimburse error
        ItemStack heldItemCopy = ItemManager.clone(heldItem);

        // If sell all enchants is true
        // Then use MultiValueResponse and use getSellValue of entire item
        // Then add quantity of each enchant / remove enchant from item
        // Then add cash
        // if cash add fails - reimburse old item
        if (sellAllEnchants) {
            EnchantValueResponse evr = this.getMain().getEnchMan().getSellValue(new ItemStack[]{heldItem});

            // If failed to get value
            if (evr.isFailure()) {
                this.getMain().getConsole().logFailedSale(sender, evr.getQuantity(), String.format("enchants( %s )", evr.listNames()), evr.getErrorMessage());
                return true;
            }


            // If successful
            for (String enchantID : evr.getTokenIds()) {
                MarketableEnchant enchantmentData = this.getMain().getEnchMan().getEnchant(enchantID);
                this.getMain().getEnchMan().editLevelQuantity(enchantmentData, evr.getQuantity(enchantID));
                this.getMain().getEnchMan().removeEnchantLevelsFromItem(heldItem, enchantmentData.getEnchantment(), evr.getQuantity(enchantID));
            }

            // Add the cash
            EconomyResponse economyResponse = this.getMain().getEconMan().addCash(sender, evr.getValue());

            // If failed to add cash
            if (!economyResponse.transactionSuccess()) {
                PlayerManager.replaceItemStack(sender, heldItem, heldItemCopy);
                this.getMain().getConsole().logFailedSale(sender, evr.getQuantity(), String.format("enchants( %s )", evr.listNames()), economyResponse.errorMessage);
                return true;
            }


            this.getMain().getConsole().logSale(sender, evr.getQuantity(), evr.getValue(), String.format("enchants( %s )", evr.listNames()));
            return true;
        }


        // If only handling one enchant
        // Ensure enchant exists
        MarketableEnchant enchantData = this.getMain().getEnchMan().getEnchant(enchantName);
        if (enchantData == null) {
            this.getMain().getConsole().usage(sender, String.format(CommandResponse.InvalidEnchantName.message, enchantName), this.help.getUsages());
            return true;
        }

        // Update enchantLevels to the max if sellAllEnchants is true
        //noinspection ConstantConditions
        if (sellAllLevels) {
            enchantLevels = heldItem.getEnchantmentLevel(enchantData.getEnchantment());
        }

        // Get value
        EnchantValueResponse evr = this.getMain().getEnchMan().getSellValue(heldItem, enchantData.getID(), enchantLevels);

        // Ensure valuation was successful
        if (evr.isFailure()) {
            this.getMain().getConsole().logFailedSale(sender, enchantLevels, enchantData.getCleanName(), evr.getErrorMessage());
            return true;
        }

        // Remove enchants, add quantity and add cash
        this.getMain().getEnchMan().removeEnchantLevelsFromItem(heldItem, enchantData.getEnchantment(), enchantLevels);
        EconomyResponse economyResponse = this.getMain().getEconMan().addCash(sender, evr.getValue());


        // If failed to add cash
        if (!economyResponse.transactionSuccess()) {
            PlayerManager.replaceItemStack(sender, heldItem, heldItemCopy);
            this.getMain().getConsole().logFailedSale(sender, enchantLevels, enchantData.getCleanName(), economyResponse.errorMessage);
            return true;
        }

        // Edit enchant quantity & log
        this.getMain().getEnchMan().editLevelQuantity(enchantData, enchantLevels);
        this.getMain().getConsole().logSale(sender, enchantLevels, evr.getValue(), enchantData.getCleanName());

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
