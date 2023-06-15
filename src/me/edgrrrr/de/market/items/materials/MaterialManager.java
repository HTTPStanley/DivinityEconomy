package me.edgrrrr.de.market.items.materials;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.items.ItemManager;
import me.edgrrrr.de.response.MultiValueResponse;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
        this.saveMessagesDisabled = this.getConfMan().getBoolean(Setting.IGNORE_SAVE_MESSAGE_BOOLEAN);
        this.buyScale = this.getConfMan().getDouble(Setting.MARKET_MATERIALS_BUY_TAX_FLOAT);
        this.sellScale = this.getConfMan().getDouble(Setting.MARKET_MATERIALS_SELL_TAX_FLOAT);
        this.baseQuantity = this.getConfMan().getInt(Setting.MARKET_MATERIALS_BASE_QUANTITY_INTEGER);
        this.dynamicPricing = this.getConfMan().getBoolean(Setting.MARKET_MATERIALS_DYN_PRICING_BOOLEAN);
        this.wholeMarketInflation = this.getConfMan().getBoolean(Setting.MARKET_MATERIALS_WHOLE_MARKET_INF_BOOLEAN);
        this.maxItemValue = this.getConfMan().getDouble(Setting.MARKET_MAX_ITEM_VALUE_DOUBLE);
        if (this.maxItemValue < 0) {
            this.maxItemValue = Double.MAX_VALUE;
        }
        this.minItemValue = this.getConfMan().getDouble(Setting.MARKET_MIN_ITEM_VALUE_DOUBLE);
        if (this.minItemValue < 0) {
            this.minItemValue = Double.MIN_VALUE;
        }
        int timer = Converter.getTicks(this.getConfMan().getInt(Setting.MARKET_SAVE_TIMER_INTEGER));
        this.saveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                saveItems();
            }
        };
        this.saveTimer.runTaskTimerAsynchronously(this.getMain(), timer, timer);
        this.loadItems();
        this.loadAliases();
        this.getMarkMan().addManager(this);
    }

    @Override
    public void deinit() {
        this.saveTimer.cancel();
        this.saveItems();
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
        return this.searchItemNames(this.getItemNames(itemStack), startswith);
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
        for (MarketableToken thisMat : this.itemMap.values()) {
            MarketableMaterial genMat = (MarketableMaterial) thisMat;
            if (genMat.equals(itemStack)) return genMat;
        }

        return null;
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
