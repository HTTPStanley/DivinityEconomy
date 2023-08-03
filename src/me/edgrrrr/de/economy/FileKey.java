package me.edgrrrr.de.economy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * The file keys for the economy object
 */
public enum FileKey {
    // Keys -- Shared
    BALANCE("balance"),
    NAME("name"),
    UUID("uuid"),

    // Keys -- Bank
    MEMBERS("members"),
    // END
    ;

    // Object Fields
    private final String key;


    // Static Fields


    // Store all keys in a map for quick access
    private static final Map<String, FileKey> keyMap = new HashMap<>();
    static {
        for (FileKey e : FileKey.values()) {
            keyMap.put(e.key, e);
        }
    }

    FileKey(String key) {
        this.key = key;
    }

    /**
     * Returns the key value for this enum
     * @return String
     */
    public String getKey() {
        return this.key;
    }


    /**
     * Returns the key from the map
     * @param key
     * @return FileKey | null
     */
    @Nullable
    public static FileKey get(@Nonnull String key) {
        return keyMap.getOrDefault(key.toLowerCase().strip(), null);
    }
}