package edgrrrr.de.materials;

public enum MaterialPotionKey {
    UPGRADED("upgraded"),
    EXTENDED("extended"),
    TYPE("type")
    ;

    public final String key;
    MaterialPotionKey(String key) {
        this.key = key;
    }
}
