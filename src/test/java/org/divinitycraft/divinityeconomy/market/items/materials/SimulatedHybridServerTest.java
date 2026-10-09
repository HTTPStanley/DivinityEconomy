package org.divinitycraft.divinityeconomy.market.items.materials;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises modded-item import against a simulated hybrid server.
 * <p>
 * MockBukkit only knows vanilla materials, so a fake {@link ModdedMaterialProvider} stands in for the mod items an
 * Arclight server would expose. Each modded id is backed by a real vanilla {@link Material} so the market can build
 * item stacks for it.
 */
class SimulatedHybridServerTest extends CommandTestBase {

    private final Map<String, Material> moddedMaterials = new LinkedHashMap<>();
    private MaterialManager manager;

    @BeforeEach
    void installFakeHybridServer() {
        manager = plugin.getMatMan();
        manager.setModdedMaterialProvider(() -> moddedMaterials);
    }

    private MarketableMaterial item(String name) {
        return manager.getItem(name);
    }

    private YamlConfiguration materialsFile() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "materials.yml"));
    }

    private String runModded() {
        assertTrue(sendConsoleCommand("modded"));
        return String.join("\n", drainMessagesStripped(server.getConsoleSender()));
    }

    @Test
    void importsModdedMaterialsAsBannedWithNoStockAndAnAlias() {
        moddedMaterials.put("testmod:ruby", Material.DIAMOND);

        assertTrue(runModded().contains("Imported or resolved 1 modded material(s)."));

        MarketableMaterial ruby = item("testmod:ruby");
        assertNotNull(ruby);
        assertEquals(Material.DIAMOND, ruby.getMaterial());
        assertFalse(ruby.getAllowed(), "imports must start banned");
        assertEquals(0, ruby.getQuantity());

        // The short alias resolves to the same item
        assertEquals(ruby, item("ruby"));
    }

    @Test
    void importIsPersistedToMaterialsFileAndAliasFile() {
        moddedMaterials.put("testmod:ruby", Material.DIAMOND);
        runModded();
        server.getScheduler().waitAsyncTasksFinished();

        YamlConfiguration file = materialsFile();
        assertEquals("testmod:ruby", file.getString("testmod:ruby.MATERIAL_ID"));
        assertFalse(file.getBoolean("testmod:ruby.ALLOWED"));
        assertEquals(0, file.getInt("testmod:ruby.QUANTITY"));

        YamlConfiguration aliases = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "materialAliases.yml"));
        assertEquals("testmod:ruby", aliases.getString("ruby"));
    }

    @Test
    void runningTwiceDoesNotImportAgain() {
        moddedMaterials.put("testmod:ruby", Material.DIAMOND);
        runModded();

        assertTrue(runModded().contains("No new modded materials were found."));
    }

    @Test
    void dottedIdsAreStoredUnderAnEncodedKeyKeepingTheRealId() {
        moddedMaterials.put("testmod:ruby.v2", Material.EMERALD);

        assertTrue(runModded().contains("Imported or resolved 1"));

        MarketableMaterial ruby = item("testmod:ruby_v2");
        assertNotNull(ruby);
        assertEquals(Material.EMERALD, ruby.getMaterial());

        YamlConfiguration file = materialsFile();
        assertEquals("testmod:ruby.v2", file.getString("testmod:ruby_v2.MATERIAL_ID"));
        assertNull(file.getConfigurationSection("testmod:ruby"), "the '.' must not create a nested section");
    }

    @Test
    void idsThatEncodeToTheSameKeyOnlyImportTheFirst() {
        moddedMaterials.put("testmod:a.b", Material.DIAMOND);
        moddedMaterials.put("testmod:a_b", Material.EMERALD);

        assertTrue(runModded().contains("Imported or resolved 1"));
        assertEquals(Material.DIAMOND, item("testmod:a_b").getMaterial());
    }

    @Test
    void aliasesNeverShadowVanillaItems() {
        moddedMaterials.put("testmod:diamond", Material.GOLD_INGOT);

        runModded();

        assertNotNull(item("testmod:diamond"));
        assertEquals(Material.DIAMOND, item("diamond").getMaterial(), "'diamond' must still mean the vanilla item");
    }

    @Test
    void entryWhoseModLoadsLateIsSkippedThenResolvedKeepingItsSettings() throws IOException {
        // The server's materials.yml already has an enabled, stocked entry for a mod that isn't loaded yet
        File materials = new File(plugin.getDataFolder(), "materials.yml");
        YamlConfiguration file = YamlConfiguration.loadConfiguration(materials);
        file.set("testmod:late.MATERIAL_ID", "testmod:late");
        file.set("testmod:late.ALLOWED", true);
        file.set("testmod:late.QUANTITY", 500);
        file.save(materials);
        assertTrue(sendConsoleCommand("reload materials"));
        drainMessagesStripped(server.getConsoleSender());

        assertNull(item("testmod:late"), "an unresolved material must never be in the market");

        moddedMaterials.put("testmod:late", Material.IRON_INGOT);
        assertTrue(runModded().contains("Imported or resolved 1"));

        MarketableMaterial late = item("testmod:late");
        assertNotNull(late);
        assertEquals(Material.IRON_INGOT, late.getMaterial());
        assertTrue(late.getAllowed());
        assertEquals(500, late.getQuantity());
    }

    @Test
    void importedItemsAndAliasesSurviveAReload() {
        moddedMaterials.put("testmod:ruby", Material.DIAMOND);
        runModded();
        server.getScheduler().waitAsyncTasksFinished();
        assertTrue(sendConsoleCommand("banitem ruby false"));
        manager.saveItems();

        // A restart/reload re-runs ConfigUpdater, which drops any key not in the bundled files unless preserved
        manager.loadItems();
        manager.loadAliases();

        MarketableMaterial ruby = item("ruby");
        assertNotNull(ruby, "the alias must survive");
        assertEquals(Material.DIAMOND, ruby.getMaterial());
        assertTrue(ruby.getAllowed(), "admin changes must survive");
    }

    @Test
    void banItemEnablesAnImportedItem() {
        moddedMaterials.put("testmod:ruby", Material.DIAMOND);
        runModded();
        assertFalse(item("ruby").getAllowed());

        assertTrue(sendConsoleCommand("banitem ruby false"));

        assertTrue(item("testmod:ruby").getAllowed());
    }

    @Test
    void unresolvedItemsHaveNoItemStacksToTradeWith() {
        // Nothing is exposed, so nothing can be imported or bought
        assertTrue(runModded().contains("No new modded materials were found."));
        assertNull(item("testmod:ruby"));
    }

    @Test
    void potionAndEntityManagersIgnoreTheModdedMaterials() {
        moddedMaterials.put("testmod:ruby", Material.DIAMOND);

        assertEquals(0, plugin.getPotMan().reloadModdedItems());
        assertEquals(0, plugin.getEntMan().reloadModdedItems());
        assertNull(plugin.getPotMan().getItem("testmod:ruby"));
        assertNull(plugin.getEntMan().getItem("testmod:ruby"));
    }
}
