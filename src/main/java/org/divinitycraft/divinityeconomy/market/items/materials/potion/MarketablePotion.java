package org.divinitycraft.divinityeconomy.market.items.materials.potion;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.market.MapKeys;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class MarketablePotion extends MarketableMaterial {

    private final PotionType potionType;

    public MarketablePotion(DEPlugin main, PotionManager itemManager, String ID, ConfigurationSection config, ConfigurationSection defaultConfig) {
        super(main, itemManager, ID, config, defaultConfig);
        PotionType potionType = null;
        try {
            potionType = PotionType.valueOf(config.getString(MapKeys.POTION_TYPE.key));
        } catch (IllegalArgumentException exception) {
            // Error is caught by manager
            this.error = exception.getMessage();
        }

        this.potionType = potionType;
    }

    /**
     * Return if the item has been configured correctly
     *
     * @return
     */
    @Override
    public boolean check() {
        return this.material != null && this.potionType != null;
    }

    /**
     * Returns the potion effect type
     *
     * @return
     */
    public PotionType getType() {
        return this.potionType;
    }


    @Override
    public ItemStack getItemStack(int amount) {
        // Create the ItemStack with the given material and amount
        ItemStack itemStack = new ItemStack(this.material, amount);

        // Get potion meta
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();

        // Add potion data
        potionMeta.setBasePotionType(this.potionType);

        // Add data to item stack
        itemStack.setItemMeta(potionMeta);

        // Return the item stack
        return itemStack;
    }


    /**
     * Returns if the given material is equal to this
     *
     * @param material
     * @return
     */
    @Override
    public boolean equals(MarketableMaterial material) {
        // Check if the object is the same reference
        if (this == material) {
            return true;
        }

        // Ensure the object is of type MarketablePotion
        if (!(material instanceof MarketablePotion)) {
            return false;
        }

        // Cast the object to MarketablePotion
        MarketablePotion potion = (MarketablePotion) material;

        // Check for nulls and compare all properties
        return this.material.equals(potion.material) &&
                this.potionType.equals(potion.potionType);
    }


    /**
     * Returns if the given material is equal to this
     * @param itemStack
     * @return
     */
    @Override
    public boolean equals(ItemStack itemStack) {
        // Null check for itemStack
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return false;
        }

        // Get item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // If not a PotionMeta, return false
        if (!(itemMeta instanceof PotionMeta)) {
            return false;
        }

        // Cast to PotionMeta
        PotionMeta potionMeta = (PotionMeta) itemMeta;

        // Check if the potion type is the same
        return itemStack.getType().equals(this.material) && potionMeta.getBasePotionType().equals(((PotionMeta) this.getItemStack(1).getItemMeta()).getBasePotionType());
    }
}
