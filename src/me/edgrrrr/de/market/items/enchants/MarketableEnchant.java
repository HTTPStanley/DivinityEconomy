package me.edgrrrr.de.market.items.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.MapKeys;
import me.edgrrrr.de.market.items.MarketableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

/**
 * A class that represents an enchant within the economy
 */
public class MarketableEnchant extends MarketableItem {
    // The enchant that this represents
    private final Enchantment enchantment;

    /**
     * Constructor
     *
     * @param main          - The main
     * @param ID
     * @param config        - The configuration section of this enchant
     * @param defaultConfig - The default config section of this enchant
     */
    public MarketableEnchant(DEPlugin main, EnchantManager itemManager, String ID, ConfigurationSection config, ConfigurationSection defaultConfig) {
        super(main, itemManager, ID, config, defaultConfig);
        Enchantment enchantment = null;
        try {
            enchantment = Enchantment.getByKey(NamespacedKey.fromString(config.getString(MapKeys.ENCHANT_ID.key)));
        } catch (IllegalArgumentException e) {
            // Error handled by manager
            this.error = e.getMessage();
        }
        this.enchantment = enchantment;
    }

    /**
     * Returns the number of books required to make the level provided
     *
     * @param currentLevels - The current number of levels
     * @param newLevels     - The new number of levels
     * @return The number of books required to make the level provided
     */
    public static int levelsToBooks(int currentLevels, int newLevels) {
        int currentBooks;
        int newBooks;
        int delta;

        if (currentLevels == 1) {
            currentBooks = 1;
        } else {
            currentBooks = (int) Math.pow(2, currentLevels);
        }
        if (newLevels == 1) {
            newBooks = 1;
        } else {
            newBooks = (int) Math.pow(2, newLevels);
        }

        if (currentLevels == 0) {
            delta = newBooks;
        } else if (newLevels == 0) {
            delta = currentBooks;
        } else {
            delta = newBooks - currentBooks;
        }

        if (delta > 0) return delta;
        else return -delta;
    }

    /**
     * Returns the manager of this item
     */
    @Override
    public EnchantManager getManager() {
        return (EnchantManager) super.getManager();
    }

    /**
     * Returns the enchant that this represents
     *
     * @return Enchantment
     */
    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    /**
     * Returns if the enchant has enough stock to remove amount
     *
     * @param levels - The amount desired in levels
     * @return Enchant has enough stock
     */
    public boolean has(int levels) {
        return this.getQuantity() >= MarketableEnchant.levelsToBooks(0, levels);
    }

    /**
     * Returns the ID of this enchant
     *
     * @return String
     */
    public Enchantment getEnchant() {
        return this.enchantment;
    }

    /**
     * Returns the maximum user-defined level of this enchant. Supports impossible enchants such as protection 10.
     * Defaults to the max game-defined level.
     *
     * @return int
     */
    public int getMaxLevel() {
        return this.itemConfig.getInt(MapKeys.MAX_LEVEL.key);
    }

    /**
     * Return if the item has been configured correctly
     *
     * @return
     */
    @Override
    public boolean check() {
        return this.enchantment != null;
    }
}
