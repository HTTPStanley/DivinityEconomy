package me.edgrrrr.de.market.items.materials.entity;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.MapKeys;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MarketableEntity extends MarketableMaterial {
    private static final Set<String> allowedMaterials = new HashSet<>(
            Arrays.asList(
                    "SPAWNER"
            )
    );

    private final EntityType entityType;

    public MarketableEntity(DEPlugin main, EntityManager itemManager, String ID, ConfigurationSection config, ConfigurationSection defaultConfig) {
        super(main, itemManager, ID, config, defaultConfig, allowedMaterials);

        EntityType entityType = null;
        try {
            entityType = EntityType.valueOf(config.getString(MapKeys.ENTITY_ID.key));
        } catch (IllegalArgumentException exception) {
            // Error is caught by manager
            this.error = exception.getMessage();
        }

        this.entityType = entityType;
    }

    /**
     * Return if the item has been configured correctly
     *
     * @return
     */
    @Override
    public boolean check() {
        return this.entityType != null;
    }

    public EntityType getEntity() {
        return this.entityType;
    }

    /**
     * Returns <amount> of this material as an itemstack
     *
     * @param amount
     * @return
     */
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = new ItemStack(this.material);
        BlockStateMeta bsm = (BlockStateMeta) itemStack.getItemMeta();
        CreatureSpawner spawner = (CreatureSpawner) bsm.getBlockState();
        spawner.setSpawnedType(this.entityType);
        bsm.setBlockState(spawner);
        itemStack.setItemMeta(bsm);
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
        if (material instanceof MarketableEntity) {
            MarketableEntity entity = (MarketableEntity) material;
            return (this.getMaterial().equals(entity.getMaterial()) &&
                    this.getEntity().equals(entity.getEntity()));
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
        if (itemMeta instanceof BlockStateMeta) {
            BlockState state = ((BlockStateMeta) itemMeta).getBlockState();
            if (state instanceof CreatureSpawner) {
                CreatureSpawner creatureSpawner = (CreatureSpawner) state;
                return creatureSpawner.getSpawnedType().equals(this.getEntity());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
