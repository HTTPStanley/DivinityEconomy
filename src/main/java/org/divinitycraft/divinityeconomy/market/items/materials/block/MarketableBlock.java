package org.divinitycraft.divinityeconomy.market.items.materials.block;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.market.items.materials.MaterialManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * A class for storing material data
 */
public class MarketableBlock extends MarketableMaterial {

    /**
     * Constructor
     *
     * @param main              - The main
     * @param ID
     * @param configData        - The config section containing the data for this material
     * @param defaultConfigData - The default config
     */
    public MarketableBlock(DEPlugin main, MaterialManager itemManager, String ID, ConfigurationSection configData, ConfigurationSection defaultConfigData) {
        super(main, itemManager, ID, configData, defaultConfigData);
    }

    /**
     * Return if the item has been configured correctly
     *
     * @return
     */
    @Override
    public boolean check() {
        // Allow market entries for modded/non-vanilla items (material may be null)
        return true;
    }

    @Override
    public ItemStack getItemStack(int amount) {
        if (this.getMaterial() != null) {
            return new ItemStack(this.getMaterial(), amount);
        }

        // Material couldn't be resolved (modded item). Return a placeholder stack.
        // Admins can override this in materials.yml to point to a valid material
        // if they want buy/sell functionality.
        return new ItemStack(org.bukkit.Material.STONE, amount);
    }

    /**
     * Returns if the given material is equal to this
     *
     * @param material
     * @return
     */
    @Override
    public boolean equals(MarketableMaterial material) {
        if (material instanceof MarketableBlock) {
            return material.getMaterial().equals(this.getMaterial());
        } else {
            return false;
        }
    }

    /**
     * Returns if the given material is equal to this
     *
     * @param itemStack
     * @return
     */
    @Override
    public boolean equals(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof PotionMeta) {
            return false;
        } else {
            if (this.getMaterial() == null) return false;
            return itemStack.getType().equals(this.getMaterial());
        }
    }
}
