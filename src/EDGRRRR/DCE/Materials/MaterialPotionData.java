package edgrrrr.dce.materials;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A class for storing the potion data section of the config.
 */
public class MaterialPotionData {
    // The configuration section for potionData
    private final ConfigurationSection potionData;

    // The string keys for values
    private final String strExtended = "extended";
    private final String strUpgraded = "upgraded";
    private final String strType = "type";

    /**
     * Constructor
     *
     * @param potionData - The potion data
     */
    public MaterialPotionData(ConfigurationSection potionData) {
        this.potionData = potionData;
    }

    /**
     * Returns the extended variable
     *
     * @return boolean - Whether the potion is extended or not
     */
    public boolean getExtended() {
        return this.potionData.getBoolean(this.strExtended);
    }

    /**
     * Returns the type variable
     *
     * @return String - The type of potion
     */
    public String getType() {
        return this.potionData.getString(this.strType);
    }

    /**
     * Returns the upgraded variable
     *
     * @return boolean - Whether the potion is upgraded or not
     */
    public boolean getUpgraded() {
        return this.potionData.getBoolean(this.strUpgraded);
    }

    /**
     * Returns the configuration section
     *
     * @return ConfigurationSection - Returns the config section for the potion data
     */
    public ConfigurationSection getPotionData() {
        return this.potionData;
    }
}
