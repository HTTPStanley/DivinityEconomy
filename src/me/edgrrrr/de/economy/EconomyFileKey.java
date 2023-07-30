package me.edgrrrr.de.economy;

import java.util.HashMap;
import java.util.Map;

public enum EconomyFileKey {
    // Keys
    BALANCE("balance"),
    NAME("name"),
    UUID("uuid"),
    ;
    //END
    public final String key;
    public static final Map<String, EconomyFileKey> keyMap = new HashMap<>();

    static {
        for (EconomyFileKey e : EconomyFileKey.values()) {
            keyMap.put(e.key, e);
        }
    }
    EconomyFileKey(String key) {
        this.key = key;
    }


    public static EconomyFileKey get(String key) {
        return keyMap.getOrDefault(key, null);
    }
}
