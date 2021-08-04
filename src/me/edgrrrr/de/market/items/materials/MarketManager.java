package me.edgrrrr.de.market.items.materials;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.market.items.ItemManager;
import me.edgrrrr.de.response.MultiValueResponse;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MarketManager extends DivinityModule {
    private final Set<MaterialManager> managers;

    public MarketManager(DEPlugin main) {
        super(main, false);
        this.managers = new HashSet<>();
    }

    public void addManager(MaterialManager manager) {
        this.managers.add(manager);
    }

    public void removeManager(MaterialManager manager) {
        this.managers.remove(manager);
    }

    /**
     * Initialisation of the object
     */
    @Override
    protected void init() {

    }

    /**
     * Shutdown of the object
     */
    @Override
    protected void deinit() {

    }

    public String[] getItemIDs() {
        ArrayList<String> strings = new ArrayList<>();
        this.managers.forEach(manager -> strings.addAll(Arrays.asList(manager.getItemIDs())));
        return strings.toArray(new String[0]);
    }

    public String[] getItemIDs(String startsWith) {
        ArrayList<String> strings = new ArrayList<>();
        this.managers.forEach(manager -> strings.addAll(Arrays.asList(manager.getItemIDs(startsWith))));
        return strings.toArray(new String[0]);
    }

    public String[] searchItemNames(String term) {
        ArrayList<String> strings = new ArrayList<>();
        this.managers.forEach(manager -> strings.addAll(Arrays.asList(manager.searchItemNames(manager.getItemIDs(), term))));
        return strings.toArray(new String[0]);
    }

    public MarketableMaterial getItem(ItemStack itemStack) {
        MarketableMaterial material = null;

        for (MaterialManager man : this.managers) {
            material = man.getItem(itemStack);
            if (material != null) {
                this.getConsole().debug("Market Manager for '%s'(%s) = %s", itemStack, material.getID(), material.getManager().getClass().getCanonicalName());
                break;
            }

        }

        return material;
    }

    public MarketableMaterial getItem(String alias) {
        MarketableMaterial material = null;

        for (MaterialManager man : this.managers) {
            material = man.getItem(alias);
            if (material != null) {
                this.getConsole().debug("Market Manager for '%s'(%s) = %s", alias, material.getID(), material.getManager().getClass().getCanonicalName());
                break;
            }

        }

        return material;
    }

    /**
     * Returns all the item names
     *
     * @return String[]
     */
    public String[] getItemNames() {
        ArrayList<String> itemNames = new ArrayList<>();
        managers.forEach(man -> itemNames.addAll(Arrays.asList(man.getItemNames())));
        return itemNames.toArray(new String[0]);
    }

    /**
     * Returns all the item names that start with startsWith
     *
     * @param startsWith
     * @return String[]
     */
    public String[] getItemNames(String startsWith) {
        ArrayList<String> itemNames = new ArrayList<>();
        managers.forEach(man -> itemNames.addAll(Arrays.asList(man.getItemNames(startsWith))));
        return itemNames.toArray(new String[0]);
    }

    /**
     * Returns the aliases and names for the item ids given
     *
     * @param itemIDs
     * @return
     */
    public String[] getItemNames(String[] itemIDs) {
        ArrayList<String> itemNames = new ArrayList<>();
        managers.forEach(man -> itemNames.addAll(Arrays.asList(man.getItemNames(itemIDs))));
        return itemNames.toArray(new String[0]);
    }

    /**
     * Returns the aliases and names for the item ids given and filters them
     *
     * @param itemIds
     * @param startWith
     * @return
     */
    public String[] getItemNames(String[] itemIds, String startWith) {
        return this.filterItemNames(this.getItemNames(itemIds), startWith.toLowerCase());
    }

    /**
     * @param items
     * @param startsWith
     * @return
     */
    public String[] filterItemNames(String[] items, String startsWith) {
        ArrayList<String> itemNames = new ArrayList<>();

        for (MaterialManager man : this.managers) {
            itemNames.addAll(Arrays.asList(man.filterItemNames(items, startsWith)));
        }

        return itemNames.toArray(new String[0]);
    }

    /**
     * Returns a sell MultiValueResponse of all the itemstacks given.
     *
     * @param itemStacks - The itemstacks to bulk sell
     * @return MultiValueResponse
     */
    public MultiValueResponse getBulkSellValue(ItemStack[] itemStacks) {
        // Store values
        Map<String, Double> values = MultiValueResponse.createValues();
        // Store quantities
        Map<String, Integer> quantities = MultiValueResponse.createQuantities();
        // Error
        String error = "";
        // Error type
        EconomyResponse.ResponseType response = EconomyResponse.ResponseType.SUCCESS;
        Map<ItemStack, Integer> itemCounts = ItemManager.resolveItemStacks(itemStacks);

        for (ItemStack itemStack : itemCounts.keySet()) {
            MarketableMaterial marketableMaterial = this.getItem(itemStack);

            if (marketableMaterial != null) {
                // Get this stack value
                ValueResponse valueResponse = marketableMaterial.getManager().getSellValue(itemStack, itemCounts.get(itemStack));

                // If valuation succeeded
                if (valueResponse.isSuccess()) {
                    // get material id
                    String ID = marketableMaterial.getID();

                    // add value response
                    values.put(ID, valueResponse.value);
                    quantities.put(ID, itemStack.getAmount());

                } else {
                    response = valueResponse.responseType;
                    error = valueResponse.errorMessage;
                    break;
                }
            } else {
                response = EconomyResponse.ResponseType.FAILURE;
                error = String.format("item does not exist '%s'", itemStack.getType().name());
                break;
            }
        }

        return new MultiValueResponse(values, quantities, response, error);
    }
}
