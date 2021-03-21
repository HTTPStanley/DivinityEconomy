package edgrrrr.dce.player;

import com.sun.istack.internal.NotNull;
import edgrrrr.dce.DCEPlugin;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PlayerInventoryManager {
    private final DCEPlugin app;

    public PlayerInventoryManager(DCEPlugin app) {
        this.app = app;
    }


    /**
     * Returns the item the user is holding
     *
     * @param player - The player to get the item for
     * @return ItemStack - The item stack the player is holding
     */
    @Nullable
    public ItemStack getHeldItem(Player player) {
        int slotIdx = player.getInventory().getHeldItemSlot();
        return player.getInventory().getItem(slotIdx);
    }

    public ItemStack getHeldItemNotNull(Player player, ItemStack fallback) {
        ItemStack heldItem = this.getHeldItem(player);
        if (heldItem == null) heldItem = fallback;
        return heldItem;
    }

    public ItemStack getHeldItemNotNull(Player player) {
        ItemStack heldItem = this.getHeldItem(player);
        if (heldItem == null) heldItem = new ItemStack(Material.AIR, 0);
        return heldItem;
    }

    public String[] getInventoryMaterials(Player player) {
        ItemStack[] materials = player.getInventory().getStorageContents();
        ArrayList<String> materialIDs = new ArrayList<>();
        for (ItemStack iStack : materials) {
            if (iStack != null) {
                materialIDs.add(iStack.getType().toString());
            }
        }
        Set<String> uniqueMaterialIDs = new HashSet<>(materialIDs);
        return uniqueMaterialIDs.toArray(new String[0]);
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
    @NotNull
    public ItemStack[] getMaterialSlotsToCount(Player player, Material material, int amount) {
        ItemStack[] materialStacks = this.getMaterialSlots(player, material);
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        int amountLeft = amount;
        for (ItemStack materialStack : materialStacks) {
            ItemStack itemStack;
            int stackAmount = materialStack.getAmount();
            if (amountLeft >= stackAmount) {
                itemStack = materialStack;

            } else {
                itemStack = new ItemStack(material, amountLeft);
                itemStack.setItemMeta(materialStack.getItemMeta());
                materialStack.setAmount(stackAmount - amountLeft);
            }
            itemStacks.add(itemStack);
            amountLeft -= itemStack.getAmount();
            if (amountLeft == 0) {
                break;
            }
        }

        DCEPlugin.CONSOLE.debug("Fulfilled slot request of " + amount + " " + material.name() + " from " + player.getName() + ": " + itemStacks.toString());
        return itemStacks.toArray(new ItemStack[0]);
    }

    /**
     * Returns the amount of slots an amount of materials will take up
     *
     * @param material - The material to calculate for
     * @param amount   - The amount of that material
     * @return int
     */
    @NotNull
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
    @NotNull
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
     * @return ItemStack[]
     */
    @NotNull
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
    @NotNull
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
    @NotNull
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
    @NotNull
    public int getMaterialCount(Player player, Material material) {
        return this.getMaterialCount(this.getMaterialSlots(player, material));
    }

    /**
     * Calculates the total count of all materials in the ItemStacks
     *
     * @param iStacks - The array of item stacks
     * @return int - Total count of materials
     */
    @NotNull
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
    @NotNull
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
