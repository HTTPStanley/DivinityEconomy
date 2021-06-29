package me.edgrrrr.de.enchants;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

/**
 * A class that represents an enchant within the economy
 */
public class EnchantData {
    // The configuration section for this enchant
    private final ConfigurationSection configurationSection;
    // The default configuration section for this enchant
    private final ConfigurationSection defaultConfigurationSection;

    private final Enchantment enchantment;

    /**
     * Constructor
     *
     * @param configurationSection - The configuration section of this enchant
     * @param defaultConfigurationSection - The default config section of this enchant
     */
    public EnchantData(ConfigurationSection configurationSection, ConfigurationSection defaultConfigurationSection) {
        this.configurationSection = configurationSection;
        this.defaultConfigurationSection = defaultConfigurationSection;
        this.enchantment = Enchantment.getByKey(NamespacedKey.fromString(this.getID()));
    }

    /**
     * Returns the enchant that this represents
     * @return Enchantment
     */
    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    /**
     * Returns if the enchant has enough stock to remove amount
     * @param levels - The amount desired in levels
     * @return Enchant has enough stock
     */
    public boolean has(int levels) {
        return this.getQuantity() >= EnchantData.levelsToBooks(0, levels);
    }

//    /**
//     * Returns if the enchant has enough stock to remove amount
//     * @param books - The amount desired in books
//     * @return Enchant has enough stock
//     */
//    public boolean has (double books) {
//        return this.has(EnchantData.booksToLevels((int) books));
//    }

    /**
     * Returns the configuration section containing the data of this enchant
     * @return ConfigurationSection
     */
    public ConfigurationSection getConfigurationSection() {
        return this.configurationSection;
    }

    /**
     * Returns the default configuration section containing the data of this enchant
     * @return ConfigurationSection
     */
    public ConfigurationSection getDefaultConfigurationSection() {
        return this.defaultConfigurationSection;
    }

    /**
     * Returns the clean user-friendly name of the enchant
     * @return String
     */
    public String getCleanName() {
        return this.configurationSection.getString(EnchantKey.CLEAN_NAME.key);
    }

    /**
     * Returns the quantity of this enchant in the market
     * @return int
     */
    public int getQuantity() {
        return this.configurationSection.getInt(EnchantKey.QUANTITY.key);
    }

    /**
     * Returns the default quantity of this enchant
     * @return int
     */
    public int getDefaultQuantity() {
        return this.defaultConfigurationSection.getInt(EnchantKey.QUANTITY.key);
    }

    /**
     * Edits the quantity of the enchant based on the amount provided
     * @param amount - The amount to edit (positive for add, negative for remove)
     */
    public void editQuantity(int amount) {
        this.setQuantity(this.getQuantity() + amount);
    }

    /**
     * Sets the quantity of the material to the amount given.
     * @param amount - The amount
     */
    public void setQuantity(int amount) {
        this.setData(EnchantKey.QUANTITY.key, amount);
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
    public String getID() {
        return this.configurationSection.getString(EnchantKey.ENCHANT_ID.key);
    }

    /**
     * Returns the maximum user-defined level of this enchant. Supports impossible enchants such as protection 10.
     * Defaults to the max game-defined level.
     * @return int
     */
    public int getMaxLevel() {
        return this.configurationSection.getInt(EnchantKey.MAX_LEVEL.key);
    }

    /**
     * Returns whether the item is allowed or not.
     * @return boolean
     */
    public boolean getAllowed() {
        return this.configurationSection.getBoolean(EnchantKey.ALLOWED.key);
    }

    /**
     * Returns the number of books required to make the level provided
     * @param currentLevels - The current number of levels
     * @param newLevels - The new number of levels
     * @return The number of books required to make the level provided
     */
    public static int levelsToBooks(int currentLevels, int newLevels) {
        int newTotal = (int) Math.pow(2, newLevels);
        int oldTotal = (int) Math.pow(2, currentLevels);
        int delta = newTotal - oldTotal;
        if (delta > 0) return delta;
        else return -delta;
    }

//    /**
//     * Returns the number of levels that can be made from the books provided
//     * Rounds to the floor (4/3 = 1)
//     * @param books - The number of books
//     * @return The number of levels that can be created from the books provided
//     */
//    public static int booksToLevels(int books) {
//        return Math.floorDiv((int) Math.log10(books), (int) Math.log10(2));
//    }
}
