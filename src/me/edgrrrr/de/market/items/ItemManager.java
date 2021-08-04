package me.edgrrrr.de.market.items;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.TokenManager;
import me.edgrrrr.de.response.MultiValueResponse;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ItemManager extends TokenManager {

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and items with data from the program
     *
     * @param main      - The plugin
     * @param itemFile
     * @param aliasFile
     * @param itemMap
     */
    public ItemManager(DEPlugin main, String itemFile, String aliasFile, Map<String, ? extends MarketableItem> itemMap) {
        super(main, itemFile, aliasFile, itemMap);
    }

    /**
     * Returns the names and aliases for the itemstack given
     *
     * @param itemStack
     * @return
     */
    public abstract String[] getItemNames(ItemStack itemStack);

    /**
     * Returns the names and aliases for the itemstack given starting with startswith
     *
     * @param itemStack
     * @param startswith
     * @return
     */
    public abstract String[] getItemNames(ItemStack itemStack, String startswith);

    /**
     * Returns the combined sell value of all the items given
     *
     * @param itemStacks - The items to calculate the price for
     * @return ValueResponse - The value of the items, or not if an error occurred.
     */
    public ValueResponse getSellValue(ItemStack[] itemStacks) {
        double value = 0.0;

        // Loop through items and add up the sell value of each item
        Map<ItemStack, Integer> itemCounts = resolveItemStacks(itemStacks);
        for (ItemStack itemStack : itemCounts.keySet()) {
            ValueResponse mv = this.getSellValue(itemStack, itemCounts.get(itemStack));
            if (mv.isSuccess()) {
                value += mv.value;
            } else {
                return new ValueResponse(0.0, mv.responseType, mv.errorMessage);
            }
        }

        return new ValueResponse(value, EconomyResponse.ResponseType.SUCCESS, "");
    }

    /**
     * Returns the sell value for a single type of items.
     *
     * @param itemStack - The unique item to value
     * @param amount    - The amount of that item
     * @return
     */
    public abstract ValueResponse getSellValue(ItemStack itemStack, int amount);

    /**
     * Returns a sell MultiValueResponse of all the itemstacks given.
     *
     * @param itemStacks - The itemstacks to evaluate
     * @return MultiValueResponse
     */
    public abstract MultiValueResponse getBulkSellValue(ItemStack[] itemStacks);


    /**
     * Returns the price of buying the given items.
     *
     * @param itemStacks - The items to get the price for
     * @return MaterialValue
     */
    public ValueResponse getBuyValue(ItemStack[] itemStacks) {
        double value = 0.0;
        Map<ItemStack, Integer> itemCounts = resolveItemStacks(itemStacks);
        for (ItemStack itemStack : itemCounts.keySet()) {
            ValueResponse mv = this.getBuyValue(itemStack, itemCounts.get(itemStack));
            if (mv.isSuccess()) {
                value += mv.value;
            } else {
                return new ValueResponse(0.0, mv.responseType, mv.errorMessage);
            }
        }

        return new ValueResponse(value, EconomyResponse.ResponseType.SUCCESS, "");
    }

    /**
     * Returns the buy value for a single type of items.
     *
     * @param itemStack - The unique item to value
     * @param amount    - The amount of the item
     * @return
     */
    public abstract ValueResponse getBuyValue(ItemStack itemStack, int amount);

    /**
     * Returns the sums of the item stacks given
     *
     * @param itemStacks
     * @return
     */
    public static Map<ItemStack, Integer> resolveItemStacks(ItemStack[] itemStacks) {
        Map<ItemStack, Integer> result = new ConcurrentHashMap<>();
        for (ItemStack itemStack : itemStacks) {
            ItemStack clone = itemStack.clone();
            clone.setAmount(1);
            result.put(clone, result.getOrDefault(clone, 0) + itemStack.getAmount());
        }
        return result;
    }
}
