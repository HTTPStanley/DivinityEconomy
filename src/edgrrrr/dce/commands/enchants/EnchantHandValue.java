package edgrrrr.dce.commands.enchants;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandEnchant;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.MultiValueResponse;
import edgrrrr.dce.response.ValueResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for valuing enchants
 */
public class EnchantHandValue extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandValue(DCEPlugin app) {
        super(app, "evalue", false, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN);
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
        boolean sellAllEnchants = false;

        switch (args.length) {
            // If user enters only the command
            // Sell all enchants on item
            case 0:
                sellAllEnchants = true;
                break;

            // If user enters the name
            // sell maximum of enchant given
            case 1:
                enchantName = args[0];
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

        // If sell all enchants is true
        // Then use MultiValueResponse and use getSellValue of entire item
        // Then add quantity of each enchant / remove enchant from item
        // Then add cash
        if (sellAllEnchants) {
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

            MultiValueResponse multiValueResponse1 = this.app.getEnchantmentManager().getSellValue(heldItem);
            MultiValueResponse multiValueResponse2 = this.app.getEnchantmentManager().getBuyValue(heldItem);
            if (multiValueResponse1.isFailure()) {
                this.app.getConsole().warn(sender, String.format("Couldn't determine buy value of &d Enchants(%s) because %s", multiValueResponse2.getTotalQuantity(), multiValueResponse2, multiValueResponse2.errorMessage));
            } else {
                this.app.getConsole().info(sender, String.format("Buy: %d Enchants(%s) costs £%,.2f", multiValueResponse2.getTotalQuantity(), multiValueResponse2, multiValueResponse2.getTotalValue()));
            }
            if (multiValueResponse1.isFailure()) {
                this.app.getConsole().warn(sender, String.format("Couldn't determine sell value of &d Enchants(%s) because %s", multiValueResponse1.getTotalQuantity(), multiValueResponse1, multiValueResponse1.errorMessage));
            } else {
                this.app.getConsole().info(sender, String.format("Sell: %d Enchants(%s) costs £%,.2f", multiValueResponse1.getTotalQuantity(), multiValueResponse1, multiValueResponse1.getTotalValue()));
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

            ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
            itemStack.addUnsafeEnchantment(enchantData.getEnchantment(), enchantLevels);

            // Get value
            // Remove enchants, add quantity and add cash
            ValueResponse valueResponse1 = this.app.getEnchantmentManager().getSellValue(itemStack, enchantName, enchantLevels);
            ValueResponse valueResponse2 = this.app.getEnchantmentManager().getBuyValue(enchantName, enchantLevels);
            if (valueResponse2.isFailure()) {
                this.app.getConsole().warn(sender, String.format("Couldn't determine buy value of &d Enchants(%s) because %s", enchantLevels, enchantName, valueResponse2.errorMessage));
            } else {
                this.app.getConsole().info(sender, String.format("Buy: %d Enchants(%s) costs £%,.2f", enchantLevels, enchantName, valueResponse2.value));
            }
            if (valueResponse1.isFailure()) {
                this.app.getConsole().warn(sender, String.format("Couldn't determine sell value of &d Enchants(%s) because %s", enchantLevels, enchantName, valueResponse1.errorMessage));
            } else {
                this.app.getConsole().info(sender, String.format("Sell: %d Enchants(%s) costs £%,.2f", enchantLevels, enchantName, valueResponse1.value));
            }
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
        return false;
    }
}
