package me.edgrrrr.de.market.items.materials;

import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.TokenValueResponse;
import me.edgrrrr.de.market.items.ItemManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MaterialValueResponse extends TokenValueResponse {
    private Map<MarketableMaterial, ArrayList<ItemStack>> itemStackMap = new ConcurrentHashMap<>();
    private ArrayList<ItemStack> itemStacks = new ArrayList<>();
    private ArrayList<String> itemStackIDs = new ArrayList<>();
    private ArrayList<ItemStack> clones = new ArrayList<>();


    public MaterialValueResponse() {
        super();
    }


    public MaterialValueResponse(EconomyResponse.ResponseType type, String message) {
        super(type, message);
    }


    /**
     * Adds a token to the response
     * @param token
     * @param quantity
     * @param value
     * @param itemStacks
     * @return
     */
    public MaterialValueResponse addToken(MarketableMaterial token, int quantity, double value, ItemStack[] itemStacks) {
        super.addToken(token, quantity, value);
        return addItemStacks(token, itemStacks);
    }


    /**
     * Adds a token to the response
     * @param response
     * @return
     */
    public MaterialValueResponse addResponse(MaterialValueResponse response) {
        // Add all tokens
        for (MarketableToken token : response.getTokens()) {
            this.addToken((MarketableMaterial) token, response.getQuantity(token), response.getValue(token), response.getItemStacksAsArray(token));
        }

        // If response is a failure, set this response as a failure
        if (response.isFailure()) {
            this.setFailure(response.getErrorMessage());
        }

        // return this response
        return this;
    }


    /**
     * Returns the itemstack map
     * @return
     */
    public Map<MarketableMaterial, ArrayList<ItemStack>> getItemStackMap() {
        return itemStackMap;
    }


    /**
     * Returns the itemstacks for all tokens
     */
    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }


    /**
     * Returns the itemstacks for all tokens as an array
     * @return
     */
    public ItemStack[] getItemStacksAsArray() {
        return itemStacks.toArray(new ItemStack[0]);
    }


    /**
     * Returns clones of the itemstacks for all tokens
     */
    public List<ItemStack> getClones() {
        return this.clones;
    }


    /**
     * Returns clones of the itemstacks for all tokens as an array
     * @return
     */
    public ItemStack[] getClonesAsArray() {
        return this.clones.toArray(new ItemStack[0]);
    }


    /**
     * Returns the itemstacks for the token
     * @param token
     * @return
     */
    @Nonnull
    public List<ItemStack> getItemStacks(@Nonnull MarketableToken token) {
        return itemStackMap.getOrDefault(token, new ArrayList<>());
    }


    /**
     * Returns the itemstacks for the token as an array
     */
    @Nonnull
    public ItemStack[] getItemStacksAsArray(@Nonnull MarketableToken token) {
        return getItemStacks(token).toArray(new ItemStack[0]);
    }


    /**
     * Returns the itemstacks for the item, by ID
     */
    @Nonnull
    public List<ItemStack> getItemStacks(@Nonnull String id) {
        MarketableToken token = getTokenById(id);
        if (token == null) {
            return new ArrayList<>();
        }
        return getItemStacks(token);
    }


    /**
     * Returns the itemstacks for the item, by ID as an array
     */
    @Nonnull
    public ItemStack[] getItemStacksAsArray(@Nonnull String id) {
        return getItemStacks(id).toArray(new ItemStack[0]);
    }


    /**
     * Add a list of itemstacks to the list of itemstacks for the token
     * @param token
     * @param itemStacks
     */
    private MaterialValueResponse addItemStacks(MarketableMaterial token, ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            addItemStack(token, itemStack);
        }
        return this;
    }


    /**
     * Add an itemstack to the list of itemstacks for the token
     * @param token
     * @param itemStack
     */
    private MaterialValueResponse addItemStack(MarketableMaterial token, ItemStack itemStack) {
        // Get identity
        String id = ItemManager.getOrSetIdentity(itemStack);

        // If id not found, add
        if (!this.itemStackIDs.contains(id)) {
            // Add itemstack to map
            ArrayList<ItemStack> itemStacks = this.itemStackMap.getOrDefault(token, new ArrayList<>());
            itemStacks.add(itemStack);
            this.itemStackMap.put(token, itemStacks);

            // Add itemstack to list
            this.itemStackIDs.add(id);
            this.itemStacks.add(itemStack);
            this.clones.add(ItemManager.clone(itemStack));
        }

        return this;
    }
}
