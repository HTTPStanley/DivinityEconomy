package me.edgrrrr.de.commands.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.items.enchants.EnchantValueResponse;
import me.edgrrrr.de.market.items.enchants.MarketableEnchant;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.player.PlayerManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A command for selling enchants on held items
 */
public class EnchantSellAll extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantSellAll(DEPlugin app) {
        super(app, "esellall", false, Setting.COMMAND_E_SELL_ALL_ENABLE_BOOLEAN);
        this.checkEnchantMarketEnabled = true;
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

        EnchantValueResponse response = getMain().getEnchMan().getSellValue(allStacks);

        if (response.getTokenIds().size() == 0) {
            getMain().getConsole().send(sender, LangEntry.MARKET_NothingToSellAfterSkipping.logLevel, LangEntry.MARKET_NothingToSellAfterSkipping.get(getMain()));
            return true;
        }

        // If the response was unsuccessful, return
        if (response.isFailure()) {
            // Handles console, player message and mail
            getMain().getConsole().logFailedSale(sender, response.getQuantity(), LangEntry.MARKET_EnchantList.get(getMain(), response.listNames()), response.getErrorMessage());
            return true;
        }

        // If success
        for (ItemStack itemStack : response.getItemStacks()) {
            getMain().getEnchMan().removeEnchantsFromItem(itemStack);
        }

        // Loop through all tokens and edit their quantity
        for (MarketableToken token : response.getQuantities().keySet()) {
            MarketableEnchant enchant = (MarketableEnchant) token;
            enchant.getManager().editQuantity(enchant, response.getQuantity(enchant));
        }


        // Add cash to player
        EconomyResponse economyResponse = getMain().getEconMan().addCash(sender, response.getValue());

        // If the economy response was unsuccessful, return items to player and return
        if (!economyResponse.transactionSuccess()) {
            PlayerManager.removePlayerItems(response.getItemStacksAsArray());
            PlayerManager.addPlayerItems(sender, response.getClonesAsArray());
            // Handles console, player message and mail
            getMain().getConsole().logFailedSale(sender, response.getQuantity(), LangEntry.MARKET_EnchantList.get(getMain(), response.listNames()), economyResponse.errorMessage);
            return true;
        }

        // Handles console, player message and mail
        getMain().getConsole().logSale(sender, response.getQuantity(), response.getValue(), LangEntry.MARKET_EnchantList.get(getMain(), response.listNames()));

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
