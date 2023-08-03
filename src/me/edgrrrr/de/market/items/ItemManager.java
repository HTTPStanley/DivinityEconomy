package me.edgrrrr.de.market.items;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.TokenManager;
import me.edgrrrr.de.market.TokenValueResponse;
import me.edgrrrr.de.market.items.enchants.EnchantManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class ItemManager extends TokenManager {

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and items with data from the program
     *
     * @param main      - The plugin
     * @param itemFile
     * @param aliasFile
     * @param itemMap
     */
    public ItemManager(DEPlugin main, String itemFile, String aliasFile, Map<String, ? extends MarketableItem> itemMap) {
        super(main, itemFile, aliasFile, itemMap);
    }

    /**
     * Returns if the item is named
     * @param itemStack - The item stack to check
     * @return If the item is named
     */
    public static boolean itemIsNamed(@Nonnull ItemStack itemStack) {
        return itemStack.getItemMeta().hasDisplayName();
    }

    /**
     * Returns if the item has lore
     * @param itemStack - The item stack to check
     * @return If the item has lore
     */
    public static boolean itemHasLore(@Nonnull ItemStack itemStack) {
        return itemStack.getItemMeta().hasLore();
    }

    

    /**
     * Returns the number of books required to make the level provided
     * @param itemStacks - The item stacks to check
     * @return The number of books required to make the level provided
     */
    public static ItemStack[] removeEnchantedItems(ItemStack[] itemStacks) {
        ArrayList<ItemStack> nonEnchanted = new ArrayList<>();
        Arrays.stream(itemStacks).forEach(stack -> {
            if (EnchantManager.getEnchantments(stack).isEmpty()) {
                nonEnchanted.add(stack);
            } else {
                if (stack.getItemMeta() instanceof EnchantmentStorageMeta meta) {
                    if (meta.getStoredEnchants().isEmpty()) {
                        nonEnchanted.add(stack);
                    }
                }
            }
        });
        return nonEnchanted.toArray(new ItemStack[0]);
    }


    /**
     * Removes all items that are named or have lore
     * @param itemStacks
     * @return
     */
    public static ItemStack[] removeNamedItems(ItemStack[] itemStacks) {
        ArrayList<ItemStack> nonNamed = new ArrayList<>();
        Arrays.stream(itemStacks).forEach(stack -> {
            if (!(itemIsNamed(stack) || itemHasLore(stack))) {
                nonNamed.add(stack);
            }
        });
        return nonNamed.toArray(new ItemStack[0]);
    }

    /**
     * Clones the given array and returns a non-related array
     *
     * @param itemStacks - The items to clone
     */
    public static ItemStack[] cloneItems(ItemStack[] itemStacks) {
        ArrayList<ItemStack> clones = new ArrayList<>();
        Arrays.stream(itemStacks).forEach(stack -> clones.add(clone(stack)));
        return clones.toArray(new ItemStack[0]);
    }

    public static ItemStack clone(ItemStack itemStack) {
        // Create a new item stack
        ItemStack newItemStack = new ItemStack(itemStack.getType(), itemStack.getAmount());

        // Get item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // If item meta is enchantment storage meta
        if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            // Create a new enchantment storage meta
            EnchantmentStorageMeta newEnchantmentStorageMeta = (EnchantmentStorageMeta) newItemStack.getItemMeta();

            // Add all enchantments
            for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
                newEnchantmentStorageMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }

            // Set item meta
            newItemStack.setItemMeta(newEnchantmentStorageMeta);
        }

        // If item meta is potion meta
        else if (itemMeta instanceof PotionMeta potionMeta) {
            // Create a new potion meta
            PotionMeta newPotionMeta = (PotionMeta) newItemStack.getItemMeta();

            // Add all custom effects
            for (PotionEffect effect : potionMeta.getCustomEffects()) {
                newPotionMeta.addCustomEffect(effect, true);
            }

            // Set item meta
            newItemStack.setItemMeta(newPotionMeta);
        }


        // If item meta is entity meta
        else if (itemMeta instanceof BlockStateMeta) {
            // Create a new block state meta
            BlockStateMeta newBlockStateMeta = (BlockStateMeta) newItemStack.getItemMeta();

            // Set block state
            newBlockStateMeta.setBlockState(((BlockStateMeta) itemMeta).getBlockState());

            // Set item meta
            newItemStack.setItemMeta(newBlockStateMeta);
        }

        // If item meta is item meta
        else {
            // Set item meta
            newItemStack.setItemMeta(itemMeta);
        }


        newItemStack.addUnsafeEnchantments(EnchantManager.getEnchantments(itemStack));
        newItemStack.setData(itemStack.getData());
        return newItemStack;
    }

    /**
     * Calculates the total count of all materials in the ItemStacks
     *
     * @param iStacks - The array of item stacks
     * @return int - Total count of materials
     */
    public static int getMaterialCount(ItemStack[] iStacks) {
        int count = 0;
        for (ItemStack iStack : iStacks) {
            count += iStack.getAmount();
        }

        return count;
    }


    /**
     * Checks if the given item has been assigned a UUID
     * @param itemStack
     * @return
     */
    public static boolean itemIsUnidentified(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(NamespacedKey.minecraft("de-uuid"), PersistentDataType.STRING);
    }


    /**
     * Returns the UUID of the given item stack
     */
    public static String getIdentity(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.get(NamespacedKey.minecraft("de-uuid"), PersistentDataType.STRING);
    }


    /**
     * Identifies the given item stack with a UUID
     * @param itemStack
     * @return
     */
    public static void generateIdentity(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(NamespacedKey.minecraft("de-uuid"), PersistentDataType.STRING, UUID.randomUUID().toString());
        itemStack.setItemMeta(itemMeta);
    }


    public static String getOrSetIdentity(ItemStack itemStack) {
        if (itemIsUnidentified(itemStack)) {
            return getIdentity(itemStack);
        } else {
            generateIdentity(itemStack);
            return getIdentity(itemStack);
        }
    }


    public static List<ItemStack> removeIdentity(List<ItemStack> itemStacks) {
        List<ItemStack> nonIdentified = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            nonIdentified.add(removeIdentity(itemStack));
        }
        return nonIdentified;
    }


    public static ItemStack removeIdentity(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.remove(NamespacedKey.minecraft("de-uuid"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    /**
     * Returns the names and aliases for the itemstack given
     *
     * @param itemStack
     * @return
     */
    public abstract Set<String> getItemNames(ItemStack itemStack);

    /**
     * Returns the names and aliases for the itemstack given starting with startswith
     *
     * @param itemStack
     * @param startswith
     * @return
     */
    public abstract Set<String> getItemNames(ItemStack itemStack, String startswith);

    /**
     * Returns the combined sell value of all the items given
     *
     * @param itemStacks - The items to calculate the price for
     * @return ValueResponse - The value of the items, or not if an error occurred.
     */
    public abstract TokenValueResponse getSellValue(ItemStack[] itemStacks);

    /**
     * Returns the sell value for a single type of items.
     *
     * @param itemStack - The unique item to value
     * @param amount    - The amount of that item
     * @return
     */
    public abstract TokenValueResponse getSellValue(ItemStack itemStack, int amount);


    /**
     * Returns the price of buying the given items.
     *
     * @param itemStacks - The items to get the price for
     * @return MaterialValue
     */
    public abstract TokenValueResponse getBuyValue(ItemStack[] itemStacks);

    /**
     * Returns the buy value for a single type of items.
     *
     * @param itemStack - The unique item to value
     * @param amount    - The amount of the item
     * @return
     */
    public abstract TokenValueResponse getBuyValue(ItemStack itemStack, int amount);
}
