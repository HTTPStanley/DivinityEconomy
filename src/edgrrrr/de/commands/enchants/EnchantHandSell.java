package edgrrrr.de.commands.enchants;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandEnchant;
import edgrrrr.de.config.Setting;
import edgrrrr.de.enchants.EnchantData;
import edgrrrr.de.math.Math;
import edgrrrr.de.player.PlayerInventoryManager;
import edgrrrr.de.response.MultiValueResponse;
import edgrrrr.de.response.ValueResponse;
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
        String enchantName= "";
        int enchantLevels = 1;
        boolean sellAllLevels = false;
        boolean sellAllEnchants = false;

        switch (args.length) {
            case 1:
                enchantName = args[0];
                if (enchantName.equals("*")) {
                    sellAllEnchants = true;
                    sellAllLevels = true;
                } else {
                    this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                    return true;
                }
                break;


            // If user enters name and level
            // Sell enchant level times
            case 2:
                enchantName = args[0];
                enchantLevels = Math.getInt(args[1]);
                break;

            // If wrong number of arguments
            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // get the item the user is holding.
        // ensure it is not null
        ItemStack heldItem = PlayerInventoryManager.getHeldItem(sender);
        if (heldItem == null) {
            this.app.getConsole().usage(sender, CommandResponse.InvalidItemHeld.message, this.help.getUsages());
            return true;
        }

        // Ensure item is enchanted
        if (!this.app.getEnchantmentManager().isEnchanted(heldItem)){
            this.app.getConsole().usage(sender, CommandResponse.InvalidItemHeld.message, this.help.getUsages());
            return true;
        }

        // Create clone in-case of imburse error
        ItemStack heldItemCopy = PlayerInventoryManager.cloneItem(heldItem);

        // If sell all enchants is true
        // Then use MultiValueResponse and use getSellValue of entire item
        // Then add quantity of each enchant / remove enchant from item
        // Then add cash
        // if cash add fails - reimburse old item
        if (sellAllEnchants) {
            MultiValueResponse multiValueResponse = this.app.getEnchantmentManager().getSellValue(heldItem);
            if (multiValueResponse.isFailure()) {
                this.app.getConsole().logFailedSale(sender, multiValueResponse.getTotalQuantity(), multiValueResponse.toString("Enchants: "), multiValueResponse.errorMessage);
            } else {
                for (String enchantID : multiValueResponse.getItemIds()) {
                    EnchantData enchantmentData = this.app.getEnchantmentManager().getEnchant(enchantID);
                    this.app.getEnchantmentManager().editLevelQuantity(enchantmentData, multiValueResponse.quantities.get(enchantID));
                    this.app.getEnchantmentManager().removeEnchantLevelsFromItem(heldItem, enchantmentData.getEnchantment(), multiValueResponse.quantities.get(enchantID));
                }
                EconomyResponse economyResponse = this.app.getEconomyManager().addCash(sender, multiValueResponse.getTotalValue());
                if (economyResponse.transactionSuccess()) {
                    this.app.getConsole().logSale(sender, multiValueResponse.getTotalQuantity(), multiValueResponse.getTotalValue(), String.format("enchants(%s)", multiValueResponse));
                } else {
                    PlayerInventoryManager.replaceItemStack(sender, heldItem, heldItemCopy);
                    this.app.getConsole().logFailedSale(sender, multiValueResponse.getTotalQuantity(), multiValueResponse.toString("Enchants: "), multiValueResponse.errorMessage);
                }
            }
        }
        else {
            // If only handling one enchant
            // Ensure enchant exists
            EnchantData enchantData = this.app.getEnchantmentManager().getEnchant(enchantName);
            if (enchantData == null) {
                this.app.getConsole().usage(sender, String.format(CommandResponse.InvalidEnchantName.message, enchantName), this.help.getUsages());
                return true;
            }

            // Update enchantLevels to the max if sellAllEnchants is true
            if (sellAllLevels) {
                enchantLevels = heldItem.getEnchantmentLevel(enchantData.getEnchantment());
            }

            // Get value
            ValueResponse valueResponse = this.app.getEnchantmentManager().getSellValue(heldItem, enchantName, enchantLevels);

            // Ensure valuation was successful
            if (valueResponse.isFailure()) {
                this.app.getConsole().logFailedSale(sender, enchantLevels, enchantName, valueResponse.errorMessage);
                return true;
            }

            // Remove enchants, add quantity and add cash
            this.app.getEnchantmentManager().removeEnchantLevelsFromItem(heldItem, enchantData.getEnchantment(), enchantLevels);
            EconomyResponse economyResponse = this.app.getEconomyManager().addCash(sender, valueResponse.value);

            // Edit enchant quantity & log
            if (economyResponse.transactionSuccess()) {
                this.app.getEnchantmentManager().editLevelQuantity(enchantData, enchantLevels);
                this.app.getConsole().logSale(sender, enchantLevels, valueResponse.value, enchantName);
            }
            // Failed funding of account, refund enchant & log
            else {
                PlayerInventoryManager.replaceItemStack(sender, heldItem, heldItemCopy);
                this.app.getConsole().logFailedSale(sender, enchantLevels, economyResponse.errorMessage, enchantName);
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
        return false;
    }
}
