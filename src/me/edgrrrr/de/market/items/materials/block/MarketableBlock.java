package me.edgrrrr.de.market.items.materials.block;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialManager;
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
        return this.material != null;
    }

    @Override
    public ItemStack getItemStack(int amount) {
        return new ItemStack(this.getMaterial(), amount);
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
            return itemStack.getType().equals(this.getMaterial());
        }
    }
}
