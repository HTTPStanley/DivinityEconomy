package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandMaterials;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.market.items.materials.MaterialValueResponse;
import org.divinitycraft.divinityeconomy.player.PlayerManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
        this.checkItemMarketEnabled = true;
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
        Set<MarketableMaterial> marketableMaterials = new HashSet<>();

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
                    MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(materialName);
                    if (marketableMaterial == null) {
                        getMain().getConsole().send(sender, LangEntry.MARKET_InvalidItemName.logLevel, LangEntry.MARKET_InvalidItemName.get(getMain()), materialName);
                        return true;
                    } else {
                        marketableMaterials.add(marketableMaterial);
                    }
                }
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Get player inventory
        // Copy all inventory items over to itemStacks that are either specified or blocked
        ItemStack[] playerInventory = PlayerManager.getInventoryMaterials(sender);
        ArrayList<ItemStack> itemStackList = new ArrayList<>();
        for (ItemStack itemStack : playerInventory) {
            MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(itemStack);
            if ((blocking && !marketableMaterials.contains(marketableMaterial)) || (!blocking && marketableMaterials.contains(marketableMaterial)) || (!blocking && marketableMaterials.size() == 0)) {
                itemStackList.add(itemStack);
            }
        }

        // Get item stacks
        // Remove enchanted items
        // Clone incase need to be refunded
        // Get valuation
        ItemStack[] allStacks = itemStackList.toArray(new ItemStack[0]);

        if (allStacks.length == 0) {
            getMain().getConsole().send(sender, LangEntry.MARKET_NothingToSell.logLevel, LangEntry.MARKET_NothingToSell.get(getMain()));
            return true;
        }


        MaterialValueResponse response = getMain().getMarkMan().getSellValue(allStacks);


        if (response.getTokenIds().size() == 0) {
            getMain().getConsole().send(sender, LangEntry.MARKET_NothingToSellAfterSkipping.logLevel, LangEntry.MARKET_NothingToSellAfterSkipping.get(getMain()));
            return true;
        }

        // If the response was unsuccessful, return
        if (!response.isSuccess()) {
            // Handles console, player message and mail
            getMain().getConsole().logFailedSale(sender, response.getQuantity(), LangEntry.W_items.get(getMain()), response.getErrorMessage());
            return true;
        }

        // Remove items from player and add cash
        PlayerManager.removePlayerItems(response.getItemStacksAsArray());
        EconomyResponse economyResponse = getMain().getEconMan().addCash(sender, response.getValue());

        // If the economy response was unsuccessful, return items to player and return
        if (!economyResponse.transactionSuccess()) {
            PlayerManager.addPlayerItems(sender, response.getClonesAsArray());
            // Handles console, player message and mail
            getMain().getConsole().logFailedSale(sender, response.getQuantity(), LangEntry.W_items.get(getMain()), economyResponse.errorMessage);
            return true;
        }


        // Loop through all materials and edit their quantity
        for (MarketableToken token : response.getQuantities().keySet()) {
            MarketableMaterial material = (MarketableMaterial) token;
            material.getManager().editQuantity(material, response.getQuantity(material));
        }

        // Handles console, player message and mail
        getMain().getConsole().logSale(sender, response.getQuantity(), response.getValue(), LangEntry.W_items.get(getMain()));

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
