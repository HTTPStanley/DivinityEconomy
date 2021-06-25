package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.player.PlayerInventoryManager;
import me.edgrrrr.de.response.MultiValueResponse;
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
    public EnchantHandValue(DEPlugin app) {
        super(app, "ehandvalue", false, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN);
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

        // If sell all enchants is true
        // Then use MultiValueResponse and use getSellValue of entire item
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

        MultiValueResponse multiValueResponse1 = this.app.getEnchantmentManager().getBuyValue(heldItem);
        if (multiValueResponse1.isFailure()) {
            this.app.getConsole().warn(sender, String.format("Couldn't determine buy value of &d Enchants(%s) because %s", multiValueResponse1.getTotalQuantity(), multiValueResponse1, multiValueResponse1.errorMessage));
        } else {
            this.app.getConsole().info(sender, String.format("Buy: %d enchants costs £%,.2f", multiValueResponse1.getTotalQuantity(), multiValueResponse1.getTotalValue()));
            for (String enchant : multiValueResponse1.getItemIds()) {
                this.app.getConsole().info(sender, String.format("  -Buy: %d %s costs £%,.2f", multiValueResponse1.quantities.get(enchant), enchant, multiValueResponse1.values.get(enchant)));
            }
        }

        MultiValueResponse multiValueResponse2 = this.app.getEnchantmentManager().getSellValue(heldItem);
        if (multiValueResponse2.isFailure()) {
            this.app.getConsole().warn(sender, String.format("Couldn't determine sell value of &d Enchants(%s) because %s", multiValueResponse2.getTotalQuantity(), multiValueResponse2, multiValueResponse2.errorMessage));
        } else {
            this.app.getConsole().info(sender, String.format("Sell: %d enchants costs £%,.2f", multiValueResponse2.getTotalQuantity(), multiValueResponse2.getTotalValue()));
            for (String enchant : multiValueResponse2.getItemIds()) {
                this.app.getConsole().info(sender, String.format("  -Sell: %d %s costs £%,.2f", multiValueResponse2.quantities.get(enchant), enchant, multiValueResponse2.values.get(enchant)));
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
