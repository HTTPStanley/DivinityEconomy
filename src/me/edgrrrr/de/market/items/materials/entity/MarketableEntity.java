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

public class MarketableEntity extends MarketableMaterial {

    private final EntityType entityType;

    public MarketableEntity(DEPlugin main, EntityManager itemManager, String ID, ConfigurationSection config, ConfigurationSection defaultConfig) {
        super(main, itemManager, ID, config, defaultConfig);

        // Try to retrieve the entity type from the config safely
        EntityType entityType = null;
        try {
            String entityId = config.getString(MapKeys.ENTITY_ID.key);
            if (entityId != null) {
                entityType = EntityType.valueOf(entityId);
            }
        } catch (IllegalArgumentException exception) {
            // Error is caught by manager, storing the error message
            this.error = exception.getMessage();
        }

        this.entityType = entityType;
    }

    /**
     * Return if the item has been configured correctly.
     *
     * @return true if the entityType is valid, false otherwise
     */
    @Override
    public boolean check() {
        return this.entityType != null;
    }

    /**
     * Get the configured entity type.
     *
     * @return the EntityType of this marketable entity
     */
    public EntityType getEntity() {
        return this.entityType;
    }

    /**
     * Returns <amount> of this material as an ItemStack with a CreatureSpawner set to spawn the specified entity.
     *
     * @param amount The amount of items in the stack
     * @return the ItemStack representing the spawner
     */
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = new ItemStack(this.material, amount);

        // Check if the item meta is BlockStateMeta and handle it accordingly
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof BlockStateMeta) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) meta;

            BlockState blockState = blockStateMeta.getBlockState();
            if (blockState instanceof CreatureSpawner) {
                CreatureSpawner spawner = (CreatureSpawner) blockState;

                // Set the spawner to spawn the configured entity type
                spawner.setSpawnedType(this.entityType);

                // Set default spawner properties based on Minecraft defaults
                spawner.setDelay(20);  // Optional, for first spawn after placement

                // Default spawner settings for Minecraft
                spawner.setMinSpawnDelay(200);      // Default minimum spawn delay
                spawner.setMaxSpawnDelay(800);      // Default maximum spawn delay
                spawner.setSpawnCount(4);           // Default number of entities per spawn
                spawner.setMaxNearbyEntities(6);    // Default max nearby entities
                spawner.setRequiredPlayerRange(16); // Default range player must be in to spawn
                spawner.setSpawnRange(4);           // Default spawn range

                // Add the spawner tag to the spawner
                NBTFuncs.setSpawnerTag(getMain(), spawner);

                // Update the spawner to reflect these changes
                spawner.update();

                // Update the block state back into the meta and the item stack
                blockStateMeta.setBlockState(spawner);
                itemStack.setItemMeta(blockStateMeta);
            }
        }

        return itemStack;
    }


    /**
     * Compares this material with another to check if they represent the same entity.
     *
     * @param material The other MarketableMaterial
     * @return true if both are MarketableEntity and have the same entity and material type, false otherwise
     */
    @Override
    public boolean equals(MarketableMaterial material) {
        if (material instanceof MarketableEntity) {
            MarketableEntity entity = (MarketableEntity) material;
            return this.getMaterial().equals(entity.getMaterial()) &&
                    this.getEntity().equals(entity.getEntity());
        }
        return false;
    }

    /**
     * Compares this material with an ItemStack to check if they represent the same entity type.
     *
     * @param itemStack The ItemStack to compare with
     * @return true if the ItemStack's spawner spawns the same entity type, false otherwise
     */
    @Override
    public boolean equals(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof BlockStateMeta) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;

            BlockState blockState = blockStateMeta.getBlockState();
            if (blockState instanceof CreatureSpawner) {
                CreatureSpawner spawner = (CreatureSpawner) blockState;

                // Compare the spawned entity types
                return spawner.getSpawnedType().equals(this.getEntity());
            }
        }

        return false;
    }
}
