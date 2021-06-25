package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.materials.MaterialData;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.player.PlayerInventoryManager;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for selling items to the market
 */
public class Sell extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public Sell(DEPlugin app) {
        super(app, "sell", false, Setting.COMMAND_SELL_ITEM_ENABLE_BOOLEAN);
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
        int amountToSell = 1;

        switch (args.length) {
            // Just material, used default amount of 1
            case 1:
                materialName = args[0];
                break;

            // Material & Amount
            case 2:
                materialName = args[0];
                amountToSell = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Check amount is greater than  0
        if (amountToSell < 1) {
            this.app.getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }


        // Check material given exists
        MaterialData materialData = this.app.getMaterialManager().getMaterial(materialName);
        if (materialData == null) {
            this.app.getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, String.format(CommandResponse.InvalidItemName.message, materialName));
            return true;
        }

        // Ensure player has enough of the material to sell.
        Material material = materialData.getMaterial();
        int materialCount = PlayerInventoryManager.getMaterialCount(PlayerInventoryManager.getMaterialSlots(sender, material));
        if (materialCount < amountToSell) {
            this.app.getConsole().logFailedSale(sender, amountToSell, materialData.getCleanName(), String.format(CommandResponse.InvalidInventoryStock.message, materialCount, amountToSell));
            return true;
        }

        // Get item stacks
        // Clone incase need to be refunded
        // Get valuation
        ItemStack[] itemStacks = PlayerInventoryManager.getMaterialSlotsToCount(sender, material, amountToSell);
        ItemStack[] itemStacksClone = PlayerInventoryManager.cloneItems(itemStacks);
        ValueResponse response = this.app.getMaterialManager().getSellValue(itemStacks);

        if (response.isSuccess()) {
            PlayerInventoryManager.removePlayerItems(itemStacks);

            EconomyResponse economyResponse = this.app.getEconomyManager().addCash(sender, response.value);
            if (!economyResponse.transactionSuccess()) {
                PlayerInventoryManager.addPlayerItems(sender, itemStacksClone);
                // Handles console, player message and mail
                this.app.getConsole().logFailedSale(sender, amountToSell, materialData.getCleanName(), economyResponse.errorMessage);
            }
            else {
                this.app.getMaterialManager().editQuantity(materialData, amountToSell);
                // Handles console, player message and mail
                this.app.getConsole().logSale(sender, amountToSell, response.value, materialData.getCleanName());
            }
        }
        else {
            // Handles console, player message and mail
            this.app.getConsole().logFailedSale(sender, amountToSell, materialData.getCleanName(), response.errorMessage);
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
