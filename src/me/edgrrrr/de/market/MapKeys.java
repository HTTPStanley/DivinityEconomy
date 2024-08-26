package me.edgrrrr.de.market;

public enum MapKeys {
    ALLOWED("ALLOWED"),
    QUANTITY("QUANTITY"),
    MATERIAL_ID("MATERIAL_ID"),
    POTION_TYPE("POTION_TYPE"),
    MAX_LEVEL("MAX_LEVEL"),
    ENCHANT_ID("ENCHANT_ID"),
    ENTITY_ID("ENTITY_ID");
    public final String key;

    MapKeys(String key) {
        this.key = key;
    }
}
