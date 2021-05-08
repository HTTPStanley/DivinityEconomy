package edgrrrr.dce.enchants;

public enum EnchantKey {
    ALLOWED("ALLOWED"),
    MAX_LEVEL("MAX_LEVEL"),
    CLEAN_NAME("CLEAN_NAME"),
    ENCHANT_ID("ID"),
    QUANTITY("QUANTITY")
    ;

    public final String key;
    EnchantKey(String key) {
        this.key = key;
    }
}
