package me.edgrrrr.de.market.items.materials;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.items.ItemManager;
import me.edgrrrr.de.response.MultiValueResponse;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class MaterialManager extends ItemManager {

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and items with data from the program
     *
     * @param main      - The plugin
     * @param itemFile
     * @param aliasFile
     * @param itemMap
     */
    public MaterialManager(DEPlugin main, String itemFile, String aliasFile, Map<String, ? extends MarketableMaterial> itemMap) {
        super(main, itemFile, aliasFile, itemMap);
    }

    @Override
    public void init() {
        super.init();
        this.getMarkMan().addManager(this);
    }

    @Override
    public void deinit() {
        super.deinit();
        this.getMarkMan().removeManager(this);
    }

    /**
     * Returns the names and aliases for the itemstack given
     *
     * @param itemStack
     * @return
     */
    @Override
    public String[] getItemNames(ItemStack itemStack) {
        return this.getItemNames(this.getItem(itemStack).getID());
    }

    /**
     * Returns the names and aliases for the itemstack given starting with startswith
     *
     * @param itemStack
     * @param startswith
     * @return
     */
    @Override
    public String[] getItemNames(ItemStack itemStack, String startswith) {
        return this.filterItemNames(this.getItemNames(itemStack), startswith);
    }

    /**
     * Returns an item from the item HashMap, Will be none if no alias or
     * direct name is found.
     *
     * @param alias - The alias or name of the item to get.
     * @return ? extends DivinityItem - Returns the material data corresponding to the string supplied.
     */
    @Override
    public MarketableMaterial getItem(String alias) {
        return (MarketableMaterial) super.getItem(alias);
    }

    /**
     * Returns the DivinityMaterial for the itemstack given
     *
     * @param itemStack - The itemstack to get
     * @return ? extends DivinityMaterial
     */
    public MarketableMaterial getItem(ItemStack itemStack) {
        MarketableMaterial result = null;
        for (MarketableToken thisMat : this.itemMap.values()) {
            MarketableMaterial genMat = (MarketableMaterial) thisMat;
            if (genMat.equals(itemStack)) {
                result = genMat;
                break;
            }
        }

        return result;
    }

    /**
     * Returns a sell MultiValueResponse of all the itemstacks given.
     *
     * @param itemStacks - The itemstacks to bulk sell
     * @return MultiValueResponse
     */
    @Override
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
            // Get this stack value
            ValueResponse valueResponse = this.getSellValue(itemStack, itemCounts.get(itemStack));

            // If valuation succeeded
            if (valueResponse.isSuccess()) {
                // get material id
                MarketableToken itemData = this.getItem(itemStack);
                String ID = itemData.getID();

                // add value response
                values.put(ID, valueResponse.value);
                quantities.put(ID, itemStack.getAmount());

            } else {
                response = valueResponse.responseType;
                error = valueResponse.errorMessage;
                break;
            }
        }

        return new MultiValueResponse(values, quantities, response, error);
    }
}
