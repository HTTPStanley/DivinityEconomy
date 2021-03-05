package EDGRRRR.DCE.Enchants;

import com.sun.istack.internal.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.enchantments.Enchantment;

/**
 * A class that represents an enchant within the economy
 */
public class EnchantData {
    // The enchant manager
    final EnchantmentManager enchantmentManager;
    // The configuration section for this enchant
    private final ConfigurationSection configurationSection;
    // The default configuration section for this enchant
    private final ConfigurationSection defaultConfigurationSection;

    private Enchantment enchantment;

    // String key names for values
    private final String strAllowed = "ALLOWED";
    private final String strMaxLevel = "MAX_LEVEL";
    private final String strCleanName = "CLEAN_NAME";
    private final String strID = "ID";
    private final String strQuantity = "QUANTITY";

    /**
     * Constructor
     *
     * @param enchantmentManager - The enchantment manager
     * @param configurationSection - The configuration section of this enchant
     * @param defaultConfigurationSection - The default config section of this enchant
     */
    public EnchantData(EnchantmentManager enchantmentManager, ConfigurationSection configurationSection, ConfigurationSection defaultConfigurationSection) {
        this.enchantmentManager = enchantmentManager;
        this.configurationSection = configurationSection;
        this.defaultConfigurationSection = defaultConfigurationSection;
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.getKey().getKey().equals(this.getID())) {
                this.enchantment = enchantment;
                break;
            }
        }
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    /**
     * Returns the configuration section containing the data of this enchant
     * @return ConfigurationSection
     */
    @NotNull
    public ConfigurationSection getConfigurationSection() {
        return this.configurationSection;
    }

    /**
     * Returns the default configuration section containing the data of this enchant
     * @return ConfigurationSection
     */
    @NotNull
    public ConfigurationSection getDefaultConfigurationSection() {
        return this.defaultConfigurationSection;
    }

    /**
     * Returns the clean user-friendly name of the enchant
     * @return String
     */
    @Nullable
    public String getCleanName() {
        return this.configurationSection.getString(this.strCleanName);
    }

    /**
     * Returns the quantity of this enchant in the market
     * @return int
     */
    @NotNull
    public int getQuantity() {
        return this.configurationSection.getInt(this.strQuantity);
    }

    /**
     * Returns the default quantity of this enchant
     * @return int
     */
    @NotNull
    public int getDefaultQuantity() {
        return this.defaultConfigurationSection.getInt(this.strQuantity);
    }

    /**
     * Sets the quantity of this enchant in the economy to the amount specified.
     * Also edits the total amount of items in the market.
     * This does not save unless the manager saves the material.
     * @param amount - The amount to add or remove. Can be positive or negative.
     */
    public void setQuantity(int amount) {
        int oldQuantity = this.getQuantity();
        this.setData(this.strQuantity, amount);
        int change = oldQuantity - amount;
        this.enchantmentManager.editTotalEnchants(change);
    }

    /**
     * Edits the quantity of the enchant based on the level provided.
     * @param enchantLevel - The enchantment level
     */
    public void editLevelQuantity(int enchantLevel) {
        this.setQuantity(this.getQuantity() + this.enchantmentManager.getEnchantAmount(enchantLevel));
    }

    /**
     * Adds the quantity of the enchant based on the level provided.
     * @param enchantLevel - The enchantment level
     */
    public void addLevelQuantity(int enchantLevel) {
        this.editLevelQuantity(enchantLevel);
    }

    /**
     * Removes the quantity of the enchant based on the level provided.
     * @param enchantLevel - The enchantment level
     */
    public void remLevelQuantity(int enchantLevel) {
        this.editLevelQuantity(-enchantLevel);
    }

    /**
     * Edits the quantity of the enchant based on the amount provided
     * @param amount - The amount to edit (positive for add, negative for remove)
     */
    public void editQuantity(int amount) {
        this.setQuantity(this.getQuantity() + amount);
    }

    /**
     * Adds the quantity of the enchant based on the amount provided
     * @param amount - The amount to add
     */
    public void addQuantity(int amount) {
        this.editQuantity(amount);
    }

    /**
     * Removes the quantity of the enchant based on the amount provided
     * @param amount - The amount to add
     */
    public void remQuantity(int amount) {
        this.editQuantity(-amount);
    }

    /**
     * Sets the config key to the value provided
     * @param key - The dictionary key
     * @param value - The dictionary value
     */
    public void setData(String key, Object value) {
        this.configurationSection.set(key, value);
    }

    /**
     * Returns the ID of this enchant
     * @return String
     */
    @Nullable
    public String getID() {
        return this.configurationSection.getString(this.strID);
    }

    /**
     * Returns the maximum user-defined level of this enchant. Supports impossible enchants such as protection 10.
     * Defaults to the max game-defined level.
     * @return int
     */
    @NotNull
    public int getMaxLevel() {
        return this.configurationSection.getInt(this.strMaxLevel);
    }

    /**
     * Returns whether the item is allowed or not.
     * @return boolean
     */
    @NotNull
    public boolean getAllowed() {
        return this.configurationSection.getBoolean(this.strAllowed);
    }
}
