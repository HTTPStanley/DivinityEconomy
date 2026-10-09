package org.divinitycraft.divinityeconomy.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigKeysTest {

    @Test
    void safe_replacesDots() {
        assertEquals("mod:item_v2", ConfigKeys.safe("mod:item.v2"));
        assertEquals("a_b_c", ConfigKeys.safe("a.b.c"));
    }

    @Test
    void safe_leavesPlainKeysAndNullAlone() {
        assertEquals("cobblemon:mago_berry", ConfigKeys.safe("cobblemon:mago_berry"));
        assertNull(ConfigKeys.safe(null));
    }

    @Test
    void safe_keyStaysTopLevelInYaml() {
        YamlConfiguration config = new YamlConfiguration();
        String key = ConfigKeys.safe("mod:item.v2");
        config.set(key + ".material", "mod:item.v2");

        assertTrue(config.getKeys(false).contains(key));
        assertEquals(1, config.getKeys(false).size());
        assertEquals("mod:item.v2", config.getString(key + ".material"));
    }
}
