package edgrrrr.de.materials;

public enum MaterialKey {
    ALLOWED("ALLOWED"),
    QUANTITY("QUANTITY"),
    MATERIAL_ID("MATERIAL"),
    POTION_DATA("POTION_DATA"),
    CLEAN_NAME("CLEAN_NAME"),
    ENTITY_ID("ENTITY")
    ;
    public final String key;
    MaterialKey(String key){
        this.key = key;
    }
}
