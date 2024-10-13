package org.divinitycraft.divinityeconomy.market.items.enchants;

import org.divinitycraft.divinityeconomy.market.TokenValueResponse;
import org.divinitycraft.divinityeconomy.market.items.ItemManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantValueResponse extends TokenValueResponse {
    private Map<String, ArrayList<MarketableEnchant>> enchantMap = new ConcurrentHashMap<>();
    private ArrayList<ItemStack> itemStacks = new ArrayList<>();
    private ArrayList<String> itemStackIDs = new ArrayList<>();
    private ArrayList<ItemStack> clones = new ArrayList<>();


    public EnchantValueResponse() {
        super();
    }


    public EnchantValueResponse(EconomyResponse.ResponseType type, String message) {
        super(type, message);
    }


    public EnchantValueResponse addToken(MarketableEnchant enchant, int quantity, double value, ItemStack itemStack) {
        // Use super (Does not affect item stacks)
        super.addToken(enchant, quantity, value);
        return addItemStack(enchant, itemStack);
    }


    public EnchantValueResponse addResponse(EnchantValueResponse response) {
        // Merge enchant map
        for (ItemStack itemStack : response.getItemStacks()) {
            for (MarketableEnchant enchant : response.getEnchants(itemStack)) {
                this.addItemStack(enchant, itemStack);
            }
        }


        // Use super (Does not affect item stacks)
        return (EnchantValueResponse) super.addResponse(response);
    }


    /**
     * Get the enchant map
      * @return
     */
    public Map<String, ArrayList<MarketableEnchant>> getEnchantMap() {
        return this.enchantMap;
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
     * Get the enchants for an item stack
     * @param itemStack
     * @return
     */
    @Nonnull
    public List<MarketableEnchant> getEnchants(ItemStack itemStack) {
        return this.enchantMap.getOrDefault(ItemManager.getOrSetIdentity(itemStack), new ArrayList<>());
    }


    /**
     * Get the item stacks
     * @return
     */
    public List<ItemStack> getItemStacks() {
        return this.itemStacks;
    }


    /**
     * Get the item stacks as an array
     * @return
     */
    public ItemStack[] getItemStacksAsArray() {
        return this.itemStacks.toArray(new ItemStack[0]);
    }


    /**
     * Adds an item stack to the response
     * @param enchant
     * @param itemStack
     * @return
     */
    private EnchantValueResponse addItemStack(MarketableEnchant enchant, ItemStack itemStack) {
        // Get identity
        String identity = ItemManager.getOrSetIdentity(itemStack);

        // Add to enchant map
        ArrayList<MarketableEnchant> enchants = this.enchantMap.getOrDefault(identity, new ArrayList<>());
        enchants.add(enchant);
        this.enchantMap.put(identity, enchants);

        // Add to item stacks
        if (!this.itemStackIDs.contains(identity)) {
            this.itemStacks.add(itemStack);
            this.itemStackIDs.add(identity);
            this.clones.add(ItemManager.clone(itemStack));
        }

        return this;
    }
}
