package edgrrrr.de.materials;

public enum MaterialType {
    MATERIAL("MATERIAL"),
    ENTITY("ENTITY"),
    POTION("POTION")
    ;

    public final String key;
    MaterialType(String key) {
        this.key = key;
    }
}
