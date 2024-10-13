package org.divinitycraft.divinityeconomy.market.items.materials.entity;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.persistence.PersistentDataType;

public class NBTFuncs {
    public static final String SPAWNER_TAG = "divinity_economy_handle_me";
    public static final String SPAWNER_TAG_VALUE = "yes";

    public static void setSpawnerTag(DEPlugin main, CreatureSpawner spawner) {
        spawner.getPersistentDataContainer().set(
            new NamespacedKey(main, SPAWNER_TAG),
            PersistentDataType.STRING,
            SPAWNER_TAG_VALUE
        );
    }

    public static boolean hasSpawnerTag(DEPlugin main, CreatureSpawner spawner) {
        return SPAWNER_TAG_VALUE.equals(
            spawner.getPersistentDataContainer().get(
                new NamespacedKey(main, SPAWNER_TAG),
                PersistentDataType.STRING
            )
        );
    }

    public static void unsetSpawnerTag(DEPlugin main, CreatureSpawner spawner) {
        spawner.getPersistentDataContainer().remove(
            new NamespacedKey(main, SPAWNER_TAG)
        );
    }
}
