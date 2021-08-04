package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.response.ValueResponse;
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
                amount = Math.getInt(args[1]);
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Ensure given material exists
        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(materialName);
        if (marketableMaterial == null) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, CommandResponse.InvalidItemName.message, materialName);
            return true;
        }

        // Create items
        // Get buy & sell value
        ItemStack[] itemStacks = marketableMaterial.getItemStacks(amount);
        ValueResponse buyResponse = marketableMaterial.getManager().getBuyValue(itemStacks);
        ValueResponse sellResponse = marketableMaterial.getManager().getSellValue(itemStacks);

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
        return this.onPlayerCommand(null, args);
    }
}
