package org.divinitycraft.divinityeconomy.market.items.materials;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads modded materials from Bukkit's Material registry: everything outside the "minecraft" namespace.
 */
public class BukkitModdedMaterialProvider implements ModdedMaterialProvider {

    @Override
    public Map<String, Material> getModdedMaterials() {
        Map<String, Material> modded = new LinkedHashMap<>();
        for (Material material : Material.values()) {
            try {
                NamespacedKey key = material.getKey();
                if (key == null || "minecraft".equals(key.getNamespace())) continue;
                modded.putIfAbsent(key.toString(), material);
            } catch (Exception ignored) {
                // Some legacy/unregistered materials have no key; they are not modded items
            }
        }
        return modded;
    }
}
