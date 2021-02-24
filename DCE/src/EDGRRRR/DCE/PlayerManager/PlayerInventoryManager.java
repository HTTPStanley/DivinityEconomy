package EDGRRRR.DCE.PlayerManager;

import EDGRRRR.DCE.Main.DCEPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class PlayerInventoryManager {
    private final DCEPlugin app;

    public PlayerInventoryManager(DCEPlugin app) {
        this.app = app;
    }

    /**
     * Removes the specified number of materials from the players inventory
     * The players inventory is parsed via itemStacks
     *
     * @param amount - The amount to remove
     */
    public void removeMaterialsFromPlayer(Player player, Material material, int amount) {
        ItemStack[] itemStacks = this.getMaterialSlotsToCount(player, material, amount);
        this.removeMaterialsFromPlayer(itemStacks);
    }

    /**
     * Loops through the items and removes them from the players inventory
     *
     * @param itemStacks - The items to remove
     */
    public void removeMaterialsFromPlayer(ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            itemStack.setAmount(0);
        }
    }

    /**
     * gets the specified materials of the specified amount from the specified player
     * Note if the player does not have enough, it will return all of their materials of this type
     *
     * @param player   - The player
     * @param material - The material
     * @param amount   - The amount
     * @return ItemStack[]
     */
    public ItemStack[] getMaterialSlotsToCount(Player player, Material material, int amount) {
        ItemStack[] materialStacks = this.getMaterialSlots(player, material);
        ItemStack[] itemStacks = new ItemStack[this.getStackCount(material, amount)];
        int amountLeft = amount;
        int idx = 0;
        for (ItemStack itemStack : materialStacks) {
            int stackAmount = itemStack.getAmount();
            int amountRemoved;
            if (amountLeft > stackAmount) {
                amountRemoved = stackAmount;
            } else {
                int change = stackAmount - amountLeft;
                amountRemoved = amountLeft;
                ItemMeta meta = itemStack.getItemMeta();
                itemStack.setAmount(change);
                itemStack = new ItemStack(material, amountLeft);
                itemStack.setItemMeta(meta);
            }
            itemStacks[idx] = itemStack;
            amountLeft -= amountRemoved;
            if (amountLeft == 0) {
                break;
            }
            idx += 1;
        }

        return itemStacks;
    }

    /**
     * Returns the amount of slots an amount of materials will take up
     *
     * @param material - The material to calculate for
     * @param amount   - The amount of that material
     * @return int
     */
    public int getStackCount(Material material, int amount) {
        int itemPerStack = material.getMaxStackSize();
        return (int) Math.ceil(amount / (double) itemPerStack);
    }

    /**
     * Creates an array of ItemStacks of material with the correct amount in each stack.
     *
     * @param material - The material to create for
     * @param amount   - The amount to get
     * @return ItemStack[]
     */
    public ItemStack[] createItemStacks(Material material, int amount) {
        ItemStack[] itemStacks = new ItemStack[this.getStackCount(material, amount)];
        int idx = 0;
        for (int i = 0; i < amount; ) {
            ItemStack newStack = new ItemStack(material);
            int amountLeft = amount - i;
            if (amountLeft > material.getMaxStackSize()) {
                newStack.setAmount(material.getMaxStackSize());
                i += material.getMaxStackSize();
            } else {
                newStack.setAmount(amountLeft);
                i += amountLeft;
            }
            itemStacks[idx] = newStack;
            idx += 1;
        }

        return itemStacks;
    }

    /**
     * Adds the specified amount of the specified material to the specified player
     * Note that this does not calculate if all of the materials will fit in the players inventory
     *
     * @param player   - The player to add the materials to
     * @param material - The material to add
     * @param amount   - The amount to add
     * @return
     */
    public ItemStack[] addItemsToPlayer(Player player, Material material, int amount) {
        ItemStack[] itemStacks = this.createItemStacks(material, amount);
        this.addItemsToPlayer(player, itemStacks);
        return itemStacks;
    }

    /**
     * Adds the itemstacks in itemStacks to player
     * Note that this does not calculate if all of the materials will fit in the players inventory
     *
     * @param player     - The player to add the materials to
     * @param itemStacks - The itemStacks to add
     */
    public void addItemsToPlayer(Player player, ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            player.getInventory().addItem(itemStack);
        }
    }

    /**
     * Returns the number of empty slots in a players inventory.
     *
     * @param player - The player to check
     * @return int - The number of empty slots
     */
    public int getEmptySlots(Player player) {
        int count = 0;
        ItemStack[] inventory = player.getInventory().getStorageContents();

        for (ItemStack iStack : inventory) {
            if (iStack == null) {
                count += 1;
            }
        }

        return count;
    }

    /**
     * Returns the ItemStacks of a specific material in a player
     *
     * @param player   - The player to check
     * @param material - The material to check
     * @return ItemStack[] - An array of the ItemStack's in the player of material
     */
    public ItemStack[] getMaterialSlots(Player player, Material material) {
        HashMap<Integer, ? extends ItemStack> inventory = player.getInventory().all(material);
        ItemStack[] iStacks = new ItemStack[inventory.size()];
        int idx = 0;
        for (ItemStack iStack : inventory.values()) {
            iStacks[idx] = iStack;
            idx += 1;
        }

        return iStacks;
    }

    /**
     * Returns the total count of a material in a player
     *
     * @param player   - The player to check
     * @param material - The material to check
     * @return int - Total count of materials
     */
    public int getMaterialCount(Player player, Material material) {
        return this.getMaterialCount(this.getMaterialSlots(player, material));
    }

    /**
     * Calculates the total count of all materials in the ItemStacks
     *
     * @param iStacks - The array of item stacks
     * @return int - Total count of materials
     */
    public int getMaterialCount(ItemStack[] iStacks) {
        int count = 0;
        for (ItemStack iStack : iStacks) {
            count += iStack.getAmount();
        }

        return count;
    }

    /**
     * Calculates the total available space for a material in the players inventory
     *
     * @param player   - The player to check
     * @param material - The material to check
     * @return int - The total space that can be further occupied by a material
     */
    public int getAvailableSpace(Player player, Material material) {
        //Get empty slots
        //Get total slots used by material
        //Get total count of materials in those slots
        int emptySlots = this.getEmptySlots(player);
        ItemStack[] iStacks = this.getMaterialSlots(player, material);
        int materialCount = this.getMaterialCount(iStacks);

        // Instantiate space
        // Add the total space occupied by the number of slots filled less the actual space filled
        // Add the empty slot space
        int availableSpace = 0;
        availableSpace += (iStacks.length * material.getMaxStackSize()) - materialCount;
        availableSpace += emptySlots * material.getMaxStackSize();

        return availableSpace;
    }
}
