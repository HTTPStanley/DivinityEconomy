package me.edgrrrr.de.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import org.bukkit.configuration.ConfigurationSection;

public abstract class MarketableToken extends DivinityModule {
    // The item data
    // Config & defaults-
    protected final ConfigurationSection itemConfig;
    protected final ConfigurationSection defaultItemConfig;
    protected final TokenManager tokenManager;
    protected final String ID;
    protected String error = "No Error";

    public MarketableToken(DEPlugin main, TokenManager tokenManager, String ID, ConfigurationSection config, ConfigurationSection defaultConfig) {
        super(main, false);

        this.itemConfig = config;
        this.defaultItemConfig = defaultConfig;
        this.tokenManager = tokenManager;
        this.ID = ID;
    }

    @Override
    public void init() {

    }

    @Override
    public void deinit() {

    }

    /**
     * Returns the manager of this item
     */
    public TokenManager getManager() {
        return this.tokenManager;
    }

    /**
     * Returns the ID of this item
     *
     * @return
     */
    public String getID() {
        return this.ID;
    }

    /**
     * Return if the item has been configured correctly
     *
     * @return
     */
    public abstract boolean check();

    /**
     * If an error has occurred, this will return it.
     */
    public String getError() {
        return this.error;
    }

    /**
     * Returns the default item config
     *
     * @return
     */
    public ConfigurationSection getDefaultItemConfig() {
        return this.defaultItemConfig;
    }

    /**
     * Returns the item config
     *
     * @return
     */
    public ConfigurationSection getItemConfig() {
        return this.itemConfig;
    }

    /**
     * Returns the clean name for the item.
     * Ideal for messaging or returning to the user.
     *
     * @return String - Returns the clean name for this item
     */
    public String getCleanName() {
        return this.itemConfig.getString(MapKeys.CLEAN_NAME.key);
    }

    /**
     * Returns the quantity of this item in the market
     *
     * @return int - The quantity of this item in stock
     */
    public int getQuantity() {
        return this.itemConfig.getInt(MapKeys.QUANTITY.key);
    }

    /**
     * Sets the quantity to <amount>
     *
     * @param amount - The amount to set the internal stock of this item to
     */
    protected void setQuantity(int amount) {
        this.setData(MapKeys.QUANTITY.key, amount);
    }

    /**
     * Edits the quantity by <amount>
     *
     * @param amount - The amount to edit by. Can be negative.
     */
    protected void editQuantity(int amount) {
        this.setQuantity(this.getQuantity() + amount);
    }

    /**
     * Returns the default quantity of this item from the config
     *
     * @return int - The default quantity of this item in stock
     */
    public int getDefaultQuantity() {
        return this.defaultItemConfig.getInt(MapKeys.QUANTITY.key);
    }

    /**
     * Returns the banned state of the item
     * True means the item is allowed
     * False means the item is banned
     *
     * @return boolean - Whether the item is allowed to be bought/sold or not
     */
    public boolean getAllowed() {
        return this.itemConfig.getBoolean(MapKeys.ALLOWED.key);
    }

    /**
     * Returns if the item has enough stock to remove amount
     *
     * @param amount - The desired amount
     * @return - If there is enough stock
     */
    public boolean has(int amount) {
        return this.getQuantity() >= amount;
    }

    /**
     * Sets a data key to value
     *
     * @param key   - The key
     * @param value - The value
     */
    private void setData(String key, Object value) {
        this.itemConfig.set(key, value);
    }
}
