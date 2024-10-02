package me.edgrrrr.de.market.items.materials;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.items.ItemManager;
import me.edgrrrr.de.market.items.MarketableItem;
import me.edgrrrr.de.player.PlayerManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public abstract class MarketableMaterial extends MarketableItem {
    protected final Material material;

    public MarketableMaterial(DEPlugin main, MaterialManager itemManager, String ID, ConfigurationSection config, ConfigurationSection defaultConfig) {
        super(main, itemManager, ID, config, defaultConfig);
        Material material;
        try {
            material = Material.valueOf(ID);
        } catch (IllegalArgumentException | NullPointerException exception) {
            material = null;
        }
        this.material = material;
    }


    @Override
    public MaterialManager getManager() {
        return (MaterialManager) this.tokenManager;
    }

    /**
     * Returns the material this object represents
     *
     * @return
     */
    public Material getMaterial() {
        return this.material;
    }

    /**
     * Returns <amount> of this material as an itemstack
     *
     * @param amount
     * @return
     */
    public abstract ItemStack getItemStack(int amount);

    /**
     * Returns if the given material is equal to this
     *
     * @param material
     * @return
     */
    public abstract boolean equals(MarketableMaterial material);

    /**
     * Returns if the given material is equal to this
     *
     * @param itemStack
     * @return
     */
    public abstract boolean equals(ItemStack itemStack);

    /**
     * Returns an array of itemstacks containing this material
     *
     * @param amount
     * @return
     */
    public ItemStack[] getItemStacks(int amount) {
        // Get the max stack size and the number of stacks
        int maxStackSize = this.getMaterial().getMaxStackSize();
        int stacks = (int) Math.ceil((double) amount / maxStackSize);

        // Create the array
        ItemStack[] itemStacks = new ItemStack[stacks];

        // Loop through stacks, creating the itemstacks until the amount is 0
        for (int i = 0; i < stacks && amount > 0; i++) {
            itemStacks[i] = this.getItemStack(Math.min(maxStackSize, amount));
            amount -= maxStackSize;
        }

        // return the array
        return itemStacks;
    }

    /**
     * Returns the ItemStacks of a specific material in a player's inventory
     *
     * @param player - The player to check
     * @return ItemStack[] - An array of the ItemStack's in the player of material
     */
    public ItemStack[] getMaterialSlots(Player player) {
        Map<Integer, ? extends ItemStack> inventory = player.getInventory().all(this.material);
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (ItemStack itemStack : inventory.values()) {
            if (this.equals(itemStack)) {
                itemStacks.add(itemStack);
            }
        }

        return itemStacks.toArray(new ItemStack[0]);
    }

    /**
     * Returns the total count of a material in a player
     *
     * @param player - The player to check
     * @return int - Total count of materials
     */
    public int getMaterialCount(Player player) {
        return ItemManager.getMaterialCount(this.getMaterialSlots(player));
    }

    /**
     * Calculates the total available space for a material in the players inventory
     *
     * @param player - The player to check
     * @return int - The total space that can be further occupied by a material
     */
    public int getAvailableSpace(Player player) {
        //Get empty slots
        //Get total slots used by material
        //Get total count of materials in those slots
        int emptySlots = PlayerManager.getEmptySlots(player);
        ItemStack[] iStacks = this.getMaterialSlots(player);
        int materialCount = ItemManager.getMaterialCount(iStacks);

        // Instantiate space
        // Add the total space occupied by the number of slots filled less the actual space filled
        // Add the empty slot space
        int availableSpace = 0;
        availableSpace += (iStacks.length * this.material.getMaxStackSize()) - materialCount;
        availableSpace += emptySlots * this.material.getMaxStackSize();

        return availableSpace;
    }

    /**
     * gets the specified materials of the specified amount from the specified player
     * Note if the player does not have enough, it will return all of their materials of this type
     */
    public ItemStack[] getMaterialSlotsToCount(Player player, int amount) {
        ItemStack[] materialStacks = this.getMaterialSlots(player);
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        int amountLeft = amount;
        for (ItemStack materialStack : materialStacks) {
            ItemStack itemStack;
            int stackAmount = materialStack.getAmount();
            if (amountLeft >= stackAmount) {
                itemStack = materialStack;

            } else {
                itemStack = this.getItemStack(amountLeft);
                itemStack.setItemMeta(materialStack.getItemMeta());
                materialStack.setAmount(stackAmount - amountLeft);
            }
            itemStacks.add(itemStack);
            amountLeft -= itemStack.getAmount();
            if (amountLeft == 0) {
                break;
            }
        }

        return itemStacks.toArray(new ItemStack[0]);
    }

    /**
     * Adds the specified amount of the specified material to the specified player
     * Note that this does not calculate if all of the materials will fit in the players inventory
     *
     * @param player - The player to add the materials to
     * @param amount - The amount to add
     * @return ItemStack[]
     */
    public ItemStack[] addPlayerMaterials(Player player, int amount) {
        ItemStack[] itemStacks = this.getItemStacks(amount);
        PlayerManager.addPlayerItems(player, itemStacks);
        return itemStacks;
    }

    /**
     * Removes the specified number of materials from the players inventory
     * The players inventory is parsed via itemStacks
     */
    public void removePlayerMaterials(Player player, int amount) {
        ItemStack[] itemStacks = this.getMaterialSlotsToCount(player, amount);
        PlayerManager.removePlayerItems(itemStacks);
    }

    /**
     * Returns the amount of slots an amount of materials will take up
     *
     * @param amount - The amount of that material
     * @return int
     */
    public int getStackCount(int amount) {
        int itemPerStack = this.material.getMaxStackSize();
        return (int) Math.ceil(amount / (double) itemPerStack);
    }
}
