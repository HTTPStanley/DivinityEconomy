package me.edgrrrr.de.market.items.materials.potion;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.MapKeys;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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


    /**
     * Returns the potion effect type
     *
     * @return
     */
    public PotionEffectType getEffectType() {
        return this.potionType.getEffectType();
    }

    /**
     * Returns the extended status of the potion
     *
     * @return
     */
    public boolean getExtended() {
        return this.itemConfig.getBoolean(MapKeys.EXTENDED.key);
    }

    /**
     * Returns the upgraded status of the potion
     *
     * @return
     */
    public boolean getUpgraded() {
        return this.itemConfig.getBoolean(MapKeys.UPGRADED.key);
    }


    /**
     * Returns the potion effect
     *
     * @return
     */
    public org.bukkit.potion.PotionEffect createPotionEffect() {
        return new PotionEffect(this.getEffectType(), 1, 1, this.getExtended(), this.getUpgraded());
    }


    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = new ItemStack(this.material, amount);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.setBasePotionType(this.potionType);
        potionMeta.addCustomEffect(this.createPotionEffect(), true);
        itemStack.setItemMeta(potionMeta);
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
        if (material instanceof MarketablePotion) {
            MarketablePotion potion = (MarketablePotion) material;
            return ((potion.getExtended() == this.getExtended()) &&
                    (potion.getUpgraded() == this.getUpgraded()) &&
                    (potion.getType().equals(this.getType())) &&
                    (potion.getMaterial().equals(this.getMaterial())));
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
            PotionMeta potionMeta = (PotionMeta) itemMeta;
            return (
                    (potionMeta.getBasePotionType() == this.potionType) &&
                    (potionMeta.hasCustomEffect(this.createPotionEffect().getType())) &&
                    (itemStack.getType().equals(this.getMaterial())));
        } else {
            return false;
        }
    }
}
