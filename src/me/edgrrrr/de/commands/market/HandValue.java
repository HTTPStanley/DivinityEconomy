package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.response.ValueResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for valuing the item in the users hand
 */
public class HandValue extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public HandValue(DEPlugin app) {
        super(app, "handvalue", false, Setting.COMMAND_HAND_VALUE_ENABLE_BOOLEAN);
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
        int amount = 1;
        boolean valueAll = false;
        boolean valueHand = false;
        switch (args.length) {
            case 0:
                valueHand = true;
                break;

            case 1:
                String firstArg = args[0].toLowerCase();
                if (firstArg.equals("max")) {
                    valueAll = true;
                } else {
                    amount = Math.getInt(firstArg);
                }
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure amount is greater than 0
        if (amount < 0) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }

        ItemStack heldItem = PlayerManager.getHeldItem(sender);

        // Ensure user is holding an item
        if (heldItem == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidItemHeld.defaultLogLevel, CommandResponse.InvalidItemHeld.message);
            return true;
        }

        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(heldItem);
        ItemStack[] buyStacks;
        ItemStack[] sellStacks;
        ItemStack[] itemStacks = marketableMaterial.getMaterialSlots(sender);

        if (valueHand) {
            amount = heldItem.getAmount();
            buyStacks = marketableMaterial.getItemStacks(amount);
            sellStacks = new ItemStack[1];
            sellStacks[0] = heldItem;
        } else if (valueAll) {
            amount = MarketableMaterial.getMaterialCount(itemStacks);
            sellStacks = itemStacks;
            buyStacks = marketableMaterial.getItemStacks(amount);
        } else {
            sellStacks = marketableMaterial.getItemStacks(amount);
            buyStacks = sellStacks;
        }

        ValueResponse buyResponse = marketableMaterial.getManager().getBuyValue(buyStacks);
        ValueResponse sellResponse = marketableMaterial.getManager().getSellValue(sellStacks);

        if (buyResponse.isSuccess()) {
            this.getMain().getConsole().info(sender, "Buy: %d %s costs £%,.2f", amount, marketableMaterial.getCleanName(), buyResponse.value);

        } else {
            this.getMain().getConsole().info(sender, "Couldn't determine buy price of %d %s because %s", amount, marketableMaterial.getCleanName(), buyResponse.errorMessage);
        }

        if (sellResponse.isSuccess()) {
            this.getMain().getConsole().info(sender, "Sell: %d %s costs £%,.2f", amount, marketableMaterial.getCleanName(), sellResponse.value);

        } else {
            this.getMain().getConsole().info(sender, "Couldn't determine sell price of %d %s because %s", amount, marketableMaterial.getCleanName(), sellResponse.errorMessage);
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
