package edgrrrr.dce.commands.enchants;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandEnchant;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.Response;
import edgrrrr.dce.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for buying enchants for the item held in a users hand
 */
public class EnchantHandBuy extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandBuy(DCEPlugin app) {
        super(app, "ebuy", false, Setting.COMMAND_E_BUY_ENABLE_BOOLEAN);
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
        int enchantLevels;
        // How to use
        switch (args.length){
            case 2:
                enchantName = args[0];
                enchantLevels = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount is more than one
        if (enchantLevels < 1) {
            this.app.getConsole().warn(sender, CommandResponse.InvalidAmountGiven.message);
            return true;
        }

        // Ensure user is holding an item
        ItemStack heldItem = PlayerInventoryManager.getHeldItem(sender);
        if (heldItem == null) {
            this.app.getConsole().warn(sender, CommandResponse.InvalidItemHeld.message);
            return true;
        }

        // Ensure item valuation was successful
        ValueResponse valueResponse = this.app.getEnchantmentManager().getBuyValue(enchantName, enchantLevels);
        if (valueResponse.isFailure()) {
            this.app.getConsole().logFailedPurchase(sender, enchantLevels, enchantName, valueResponse.errorMessage);
            return true;
        }

        // Ensure given enchant exists
        EnchantData enchantData = this.app.getEnchantmentManager().getEnchant(enchantName);
        if (enchantData == null) {
            this.app.getConsole().logFailedPurchase(sender, enchantLevels, enchantName, String.format(CommandResponse.InvalidEnchantName.message, enchantName));
            return true;
        }


        double startingBalance = this.app.getEconomyManager().getBalance(sender);
        EconomyResponse economyResponse = this.app.getEconomyManager().remCash(sender, valueResponse.value);

        if (!economyResponse.transactionSuccess()) {
            this.app.getConsole().logFailedPurchase(sender, enchantLevels, enchantName, economyResponse.errorMessage);
            this.app.getEconomyManager().setCash(sender, startingBalance);
        } else {
            Response response = this.app.getEnchantmentManager().addEnchantToItem(heldItem, enchantData.getEnchantment(), enchantLevels);
            if (response.isFailure()) {
                this.app.getConsole().logFailedPurchase(sender, enchantLevels, enchantName, response.errorMessage);
                this.app.getEconomyManager().setCash(sender, startingBalance);
            } else {
                this.app.getConsole().logPurchase(sender, enchantLevels, valueResponse.value, enchantName);
                this.app.getEnchantmentManager().editQuantity(enchantData, -enchantLevels);
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
