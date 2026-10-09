package org.divinitycraft.divinityeconomy.market.items.materials;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Modded-item import walks Bukkit's Material registry, so it must only run for the block/item manager.
 * Potions and entities are keyed by PotionType/EntityType and can't be built from a Material.
 */
class ModdedItemSupportTest extends CommandTestBase {

    @Test
    void onlyTheBlockManagerSupportsModdedItems() {
        assertTrue(plugin.getMatMan().supportsModdedItems());
        assertFalse(plugin.getPotMan().supportsModdedItems());
        assertFalse(plugin.getEntMan().supportsModdedItems());
    }

    @Test
    void potionAndEntityManagersNeverScanOrChangeTheirConfig() {
        int potions = plugin.getPotMan().getItemNames().size();
        int entities = plugin.getEntMan().getItemNames().size();

        assertEquals(0, plugin.getPotMan().reloadModdedItems());
        assertEquals(0, plugin.getEntMan().reloadModdedItems());

        // The delayed startup scan (200 ticks) must also leave them untouched
        server.getScheduler().performTicks(400);
        assertEquals(potions, plugin.getPotMan().getItemNames().size());
        assertEquals(entities, plugin.getEntMan().getItemNames().size());
    }

    @Test
    void moddedCommandReportsNothingFoundWhenNoModdedMaterialsExist() {
        assertTrue(sendConsoleCommand("modded"));
        assertAllMessagesContain(server.getConsoleSender(), "No new modded materials were found.");
    }
}
