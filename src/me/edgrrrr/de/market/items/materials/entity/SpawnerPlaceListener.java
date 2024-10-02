package me.edgrrrr.de.market.items.materials.entity;

import me.edgrrrr.de.DEPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerPlaceListener implements Listener {
    private final DEPlugin main;

    public SpawnerPlaceListener(DEPlugin main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Check if the placed block is a spawner
        Block placedBlock = event.getBlockPlaced();
        if (placedBlock.getType() == Material.SPAWNER) {
            // Get the ItemStack from the event
            ItemStack itemStack = event.getItemInHand();
            ItemMeta meta = itemStack.getItemMeta();

            if (meta == null) return;

            if (meta instanceof BlockStateMeta) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
                BlockState blockState = placedBlock.getState();

                if (blockState instanceof CreatureSpawner) {
                    CreatureSpawner spawner = (CreatureSpawner) blockState;
                    CreatureSpawner itemSpawner = (CreatureSpawner) blockStateMeta.getBlockState();

                    // Check namespace
                    if (!NBTFuncs.hasSpawnerTag(main, itemSpawner)) {
                        return;
                    }
                    NBTFuncs.unsetSpawnerTag(main, itemSpawner);

                    // Transfer properties from the item to the placed block
                    spawner.setSpawnedType(itemSpawner.getSpawnedType());
                    spawner.setDelay(itemSpawner.getDelay());
                    spawner.setMinSpawnDelay(itemSpawner.getMinSpawnDelay());
                    spawner.setMaxSpawnDelay(itemSpawner.getMaxSpawnDelay());
                    spawner.setSpawnCount(itemSpawner.getSpawnCount());
                    spawner.setMaxNearbyEntities(itemSpawner.getMaxNearbyEntities());
                    spawner.setRequiredPlayerRange(itemSpawner.getRequiredPlayerRange());
                    spawner.setSpawnRange(itemSpawner.getSpawnRange());

                    // Update the block state in the world
                    spawner.update();
                }
            }
        }
    }
}
