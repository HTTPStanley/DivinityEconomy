package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandMaterials;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.materials.MaterialData;
import me.edgrrrr.de.player.PlayerInventoryManager;
import me.edgrrrr.de.response.MultiValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * A command for selling all items to the market
 */
public class SellAll extends DivinityCommandMaterials {

    /**
     * Constructor
     *
     * @param app
     */
    public SellAll(DEPlugin app) {
        super(app, "sellall", false, Setting.COMMAND_SELLALL_ITEM_ENABLE_BOOLEAN);
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
        // Whether the material names are items to sell or blocked materials
        boolean blocking = false;
        // The material data
        ArrayList<Material> materials = new ArrayList<>();

        switch (args.length) {
            case 0:
                break;

            // with specifying args
            case 1:
                String arg = args[0];
                if (arg.startsWith("!")) {
                    blocking = true;
                    arg = arg.replaceFirst("!", "");
                }
                for (String materialName : arg.split(",")) {
                    MaterialData materialData = this.getMain().getMaterialManager().getMaterial(materialName);
                    if (materialData == null) {
                        this.getMain().getConsole().send(sender, CommandResponse.InvalidItemName.defaultLogLevel, CommandResponse.InvalidItemName.message, materialName);
                        return true;
                    } else {
                        materials.add(materialData.getMaterial());
                    }
                }
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        // Get player inventory
        // Copy all inventory items over to itemStacks that are either specified or blocked
        ItemStack[] playerInventory = PlayerInventoryManager.getNonNullItemsInInventory(sender);
        ArrayList<ItemStack> itemStackList = new ArrayList<>();
        for (ItemStack itemStack : playerInventory) {
            if ((blocking && !materials.contains(itemStack.getType())) || (!blocking && materials.contains(itemStack.getType())) || (!blocking && materials.size() == 0)) {
                itemStackList.add(itemStack);
            }
        }

        // Get item stacks
        // Clone incase need to be refunded
        // Get valuation
        ItemStack[] itemStacks = itemStackList.toArray(new ItemStack[0]);
        ItemStack[] itemStacksClone = PlayerInventoryManager.cloneItems(itemStacks);
        MultiValueResponse response = this.getMain().getMaterialManager().getBulkSellValue(itemStacks);

        if (response.isSuccess()) {
            PlayerInventoryManager.removePlayerItems(itemStacks);

            EconomyResponse economyResponse = this.getMain().getEconomyManager().addCash(sender, response.getTotalValue());
            if (!economyResponse.transactionSuccess()) {
                PlayerInventoryManager.addPlayerItems(sender, itemStacksClone);
                // Handles console, player message and mail
                this.getMain().getConsole().logFailedSale(sender, response.getTotalQuantity(), "items", economyResponse.errorMessage);
            }
            else {
                for (ItemStack itemStack : itemStacksClone) {
                    this.getMain().getMaterialManager().editQuantity(this.getMain().getMaterialManager().getMaterialID(itemStack.getType().name()), response.quantities.get(itemStack.getType().name()));
                }

                // Handles console, player message and mail
                this.getMain().getConsole().logSale(sender, response.getTotalQuantity(), response.getTotalValue(), "items");
            }
        }
        else {
            // Handles console, player message and mail
            this.getMain().getConsole().logFailedSale(sender, response.getTotalQuantity(), "items", response.errorMessage);
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
