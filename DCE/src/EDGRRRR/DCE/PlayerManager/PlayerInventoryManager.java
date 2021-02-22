package EDGRRRR.DCE.PlayerManager;

import EDGRRRR.DCE.Main.DCEPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerInventoryManager {
    private final DCEPlugin app;

    public PlayerInventoryManager(DCEPlugin app) {
        this.app = app;
    }

    /**
     * Removes the specified number of materials from the players inventory
     * @param player - The player to remove from
     * @param material - The material to remove
     * @param amount - The amount to remove
     */
    public void removeMaterialsFromPlayer(Player player, Material material, int amount) {
        ItemStack[] itemStacks = this.getMaterialSlots(player, material);
        this.removeMaterialsFromPlayer(itemStacks, amount);
    }

    /**
     * Removes the specified number of materials from the players inventory
     * The players inventory is parsed via itemStacks
     * @param itemStacks - The players inventory
     * @param amount - The amount to remove
     */
    public void removeMaterialsFromPlayer(ItemStack[] itemStacks, int amount) {
        int amountLeft = amount;
        for (ItemStack itemStack : itemStacks) {
            int stackAmount = itemStack.getAmount();
            int amountRemoved;
            if (amountLeft > stackAmount) {
                amountRemoved = stackAmount;
                itemStack.setAmount(0);
            } else {
                int change = stackAmount - amountLeft;
                amountRemoved = amountLeft;
                itemStack.setAmount(change);
            }
            amountLeft -= amountRemoved;
            if (amountLeft == 0) {
                break;
            }
        }
    }

    public void addMaterialToPlayer(Player player, Material material, int amount) {
        for (int i=0; i < amount;) {
            ItemStack newStack = new ItemStack(material);
            int amountLeft = amount - i;
            if (amountLeft > material.getMaxStackSize()) {
                newStack.setAmount(material.getMaxStackSize());
                i += material.getMaxStackSize();
            } else {
                newStack.setAmount(amountLeft);
                i += amountLeft;
            }
            player.getInventory().addItem(newStack);
        }
    }

    /**
     * Returns the number of empty slots in a players inventory.
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
     * @param player - The player to check
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
     * @param player - The player to check
     * @param material - The material to check
     * @return int - Total count of materials
     */
    public int getMaterialCount(Player player, Material material) {
        return this.getMaterialCount(this.getMaterialSlots(player, material));
    }

    /**
     * Calculates the total count of all materials in the ItemStacks
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
     * @param player - The player to check
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
