package org.divinitycraft.divinityeconomy.utils;

/**
 * Helpers for building config keys.
 * Bukkit's ConfigurationSection treats '.' as a path separator and offers no escape, so a literal '.' in a
 * top-level key (e.g. a modded item id such as "mod:item.v2") would be split into nested sections.
 */
public final class ConfigKeys {

    private ConfigKeys() {
    }

    /**
     * Returns the key with every '.' replaced by '_' so it is stored as a single top-level key.
     *
     * @param key - The raw key (may be null)
     * @return The safe key, or null if the key was null
     */
    public static String safe(String key) {
        return key == null ? null : key.replace('.', '_');
    }
}
