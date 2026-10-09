package org.divinitycraft.divinityeconomy.market.items.materials;

import org.bukkit.Material;

import java.util.Map;

/**
 * Supplies the modded (non-vanilla) materials a server exposes through Bukkit.
 * <p>
 * The default implementation reads Bukkit's Material registry, which is only populated with mod items on hybrid
 * servers (Arclight etc.). It is an interface so tests can simulate a hybrid server.
 */
public interface ModdedMaterialProvider {

    /**
     * @return Every modded material, keyed by its namespaced id (e.g. "cobblemon:mago_berry")
     */
    Map<String, Material> getModdedMaterials();

    /**
     * Resolves a configured material id to a modded material
     *
     * @param id - The namespaced id, matched case-insensitively
     * @return The material, or null if the server doesn't expose it (yet)
     */
    default Material resolve(String id) {
        if (id == null) return null;
        for (Map.Entry<String, Material> entry : this.getModdedMaterials().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(id)) return entry.getValue();
        }
        return null;
    }
}
