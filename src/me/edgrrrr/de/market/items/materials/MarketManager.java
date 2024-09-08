package me.edgrrrr.de.market.items.materials;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.utils.Converter;
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
    public Set<String> getItemNames() {
        Set<String> itemNames = new HashSet<>();
        managers.forEach(man -> itemNames.addAll(man.getItemNames()));
        return itemNames;
    }

    /**
     * Returns all the item names that start with startsWith
     *
     * @param startsWith
     * @return String[]
     */
    public Set<String> getItemNames(String startsWith) {
        Set<String> itemNames = new HashSet<>();
        managers.forEach(man -> itemNames.addAll(man.getItemNames(startsWith)));
        return itemNames;
    }

    /**
     * Returns the aliases and names for the item ids given
     *
     * @param itemIDs
     * @return
     */
    public Set<String> getItemNames(Set<String> itemIDs) {
        Set<String> itemNames = new HashSet<>();
        managers.forEach(man -> itemNames.addAll(man.getItemNames(itemIDs)));
        return itemNames;
    }

    /**
     * Returns the aliases and names for the item ids given and filters them
     *
     * @param itemIds
     * @param startWith
     * @return
     */
    public Set<String> getItemNames(Set<String> itemIds, String startWith) {
        return this.searchItemNames(this.getItemNames(itemIds), startWith.toLowerCase());
    }

    /**
     * @param items
     * @param term
     * @return
     */
    public Set<String> searchItemNames(Set<String> items, String term) {
        Set<String> itemNames = new HashSet<>();

        for (MaterialManager man : this.managers) {
            itemNames.addAll(man.searchItemNames(items, term));
        }

        return itemNames;
    }

    public Set<String> searchItemNames(String term) {
        Set<String> strings = new HashSet<>();
        this.managers.forEach(manager -> strings.addAll(manager.searchItemNames(manager.getItemIDs(), term)));
        return strings;
    }

    /**
     * Returns a sell MultiValueResponse of all the itemstacks given.
     *
     * @param itemStacks - The itemstacks to bulk sell
     * @return MultiValueResponse
     */
    public MaterialValueResponse getSellValue(ItemStack[] itemStacks) {
        // Create response
        MaterialValueResponse response = new MaterialValueResponse(EconomyResponse.ResponseType.SUCCESS, null);

        // Loop through itemstacks
        for (ItemStack itemStack : itemStacks) {
            // Get the marketable material
            MarketableMaterial marketableMaterial = this.getItem(itemStack);


            // if marketable material is null
            if (marketableMaterial == null) {
                return (MaterialValueResponse) response.setFailure(String.format("item does not exist '%s'", itemStack.getType().name()));
            }

            MaterialValueResponse thisResponse = (MaterialValueResponse) marketableMaterial.getManager().getSellValue(itemStack, itemStack.getAmount());

            if (thisResponse.isFailure()) continue;

            // Add to response
            response.addResponse(thisResponse);
        }


        // Return response
        return response;
    }


    public String getName(ItemStack itemStack) {
        MarketableMaterial marketableMaterial = this.getItem(itemStack);
        if (marketableMaterial == null) {
            return itemStack.getType().name();
        }
        return marketableMaterial.getName();
    }

    public double getInflation() {
        double inflation = 0;
        for (MaterialManager manager : this.managers) {
            inflation += manager.getInflation();
        }
        return Converter.constrainDouble(((inflation * 100) / this.managers.size()), 0, 100);
    }
}
