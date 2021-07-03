package me.edgrrrr.de.materials;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A class for storing the potion data section of the config.
 */
public class MaterialPotionData {
    // The configuration section for potionData
    private final ConfigurationSection potionData;

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
        return this.potionData.getBoolean(MaterialPotionKey.EXTENDED.key);
    }

    /**
     * Returns the type variable
     *
     * @return String - The type of potion
     */
    public String getType() {
        return this.potionData.getString(MaterialPotionKey.TYPE.key);
    }

    /**
     * Returns the upgraded variable
     *
     * @return boolean - Whether the potion is upgraded or not
     */
    public boolean getUpgraded() {
        return this.potionData.getBoolean(MaterialPotionKey.UPGRADED.key);
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
