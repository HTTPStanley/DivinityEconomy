package EDGRRRR.DCE.Economy.Materials;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A class for storing the potion data section of the config.
 */
public class MaterialPotionData {
    // The configuration section for potionData
    private ConfigurationSection potionData;

    // The string keys for values
    private String strExtended = "extended";
    private String strUpgraded = "upgraded";
    private String strType = "type";

    /**
     * Constructor
     * @param potionData
     */
    public MaterialPotionData(ConfigurationSection potionData) {
        this.potionData = potionData;
    }

    /**
     * Returns the extended variable
     * @return
     */
    public boolean getExtended() {
        return potionData.getBoolean(strExtended);
    }

    /**
     * Returns the type variable
     * @return
     */
    public String getType() {
        return potionData.getString(strType);
    }

    /**
     * Returns the upgraded variable
     * @return
     */
    public boolean getUpgraded() {
        return potionData.getBoolean(strUpgraded);
    }

    /**
     * Returns the configuration section
     * @return
     */
    public ConfigurationSection getPotionData() {
        return this.potionData;
    }
}
