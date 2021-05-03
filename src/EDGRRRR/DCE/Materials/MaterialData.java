package edgrrrr.dce.materials;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A class for storing material data
 */
public class MaterialData {
    // The material manager
    // The configuration section for this material
    private final ConfigurationSection configData;
    // The configuration section for this material
    private final ConfigurationSection defaultConfigData;
    // The potionData for this material
    private final MaterialPotionData potionData;

    // Settings
    public final int minQuantity = 0;


    /**
     * Constructor
     *
     * @param configData - The config section containing the data for this material
     * @param defaultConfigData - The default config
     */
    public MaterialData(ConfigurationSection configData, ConfigurationSection defaultConfigData) {
        this.configData = configData;
        this.defaultConfigData = defaultConfigData;
        ConfigurationSection pData = configData.getConfigurationSection(MaterialKey.POTION_DATA.key);
        if (pData == null) {
            this.potionData = null;
        } else {
            this.potionData = new MaterialPotionData(pData);
        }
    }

    /**
     * Returns the config section
     *
     * @return ConfigurationSection - Returns the config data section for this material
     */
    public ConfigurationSection getConfigData() {
        return this.configData;
    }

    /**
     * Returns the clean name for the material.
     * Ideal for messaging or returning to the user.
     *
     * @return String - Returns the clean name for this item
     */
    public String getCleanName() {
        return this.configData.getString(MaterialKey.CLEAN_NAME.key);
    }

    /**
     * Returns the quantity of this material in the market
     *
     * @return int - The quantity of this item in stock
     */
    public int getQuantity() {
        return this.configData.getInt(MaterialKey.QUANTITY.key);
    }

    /**
     * Sets the quantity to <amount>
     *
     * @param amount - The amount to set the internal stock of this item to
     */
    public boolean setQuantity(int amount) {
        if (amount >= this.minQuantity) {
            this.setData(MaterialKey.QUANTITY.key, amount);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the default quantity of this material from the config
     *
     * @return int - The default quantity of this item in stock
     */
    public int getDefaultQuantity() {
        return this.defaultConfigData.getInt(MaterialKey.QUANTITY.key);
    }


    /**
     * Returns the banned state of the material
     * True means the item is allowed
     * False means the item is banned
     *
     * @return boolean - Whether the item is allowed to be bought/sold or not
     */
    public boolean getAllowed() {
        return this.configData.getBoolean(MaterialKey.ALLOWED.key);
    }

    /**
     * Returns the material ID
     * Examples: "AIR", "OAK_WOOD_PLANKS"
     *
     * @return String - The internal material name
     */
    public String getMaterialID() {
        return this.configData.getString(MaterialKey.MATERIAL_ID.key);
    }

    /**
     * Returns the potiondata for the material
     * Is null if the material is not a potion
     *
     * @return MaterialPotionData - The potion data for this material, if not potion may be null.
     */
    public MaterialPotionData getPotionData() {
        return this.potionData;
    }

    /**
     * Returns the internal Material from the materialID
     *
     * @return Material - Returns the material object for this material.
     */
    public Material getMaterial() {
        return Material.getMaterial(this.getMaterialID());
    }

    /**
     * Returns the internal Entity name
     *
     * @return String - The entity name of this material if an entity.
     */
    public String getEntityName() {
        return this.configData.getString(MaterialKey.ENTITY_ID.key);
    }

    public boolean has(int amount) {
        return this.getQuantity() >= amount;
    }

    /**
     * Returns the type of material
     * Examples:
     * MATERIAL = block/item
     * POTION = a potion that requires potionData
     * ENTITY = an entity based item
     *
     * @return String - The type of object this is.
     */
    public String getType() {
        // If potiondata isn't null, Potion
        // else if EntityName isn't null, Entity
        // else, Material
        if (!(potionData == null)) return MaterialType.POTION.key;
        else if (!(this.getEntityName() == null)) return MaterialType.ENTITY.key;
        else return MaterialType.MATERIAL.key;
    }

    /**
     * Sets a data key to value
     *
     * @param key   - The key
     * @param value - The value
     */
    private void setData(String key, Object value) {
        this.configData.set(key, value);
    }
}
