package me.edgrrrr.de.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.math.Math;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public abstract class TokenManager extends DivinityModule {
    // Stores the default items.json file location
    protected final String itemFile;
    protected final String aliasFile;
    // Save time scheduler
    protected BukkitRunnable saveTimer;
    // Stores the items and the aliases
    // Aliases are lower case
    // DivinityItem id's are upper case
    protected Map<String, String> aliasMap;
    protected Map<String, String[]> revAliasMap;
    protected Map<String, ? extends MarketableToken> itemMap;
    // Used for calculating inflation/deflation
    protected int totalItems;
    protected int defaultTotalItems;
    // Other settings
    protected double buyScale;
    protected double sellScale;
    protected double baseQuantity;
    protected boolean dynamicPricing;
    protected boolean wholeMarketInflation;
    // Stores config
    protected FileConfiguration config;

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and items with data from the program
     *
     * @param main - The plugin
     */
    public TokenManager(DEPlugin main, String itemFile, String aliasFile, Map<String, ? extends MarketableToken> itemMap) {
        super(main);
        this.itemFile = itemFile;
        this.aliasFile = aliasFile;
        this.aliasMap = new ConcurrentHashMap<>();
        this.revAliasMap = new ConcurrentHashMap<>();
        this.itemMap = itemMap;
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        this.buyScale = this.getConfMan().getDouble(Setting.MARKET_MATERIALS_BUY_TAX_FLOAT);
        this.sellScale = this.getConfMan().getDouble(Setting.MARKET_MATERIALS_SELL_TAX_FLOAT);
        this.baseQuantity = this.getConfMan().getInt(Setting.MARKET_MATERIALS_BASE_QUANTITY_INTEGER);
        this.dynamicPricing = this.getConfMan().getBoolean(Setting.MARKET_MATERIALS_DYN_PRICING_BOOLEAN);
        this.wholeMarketInflation = this.getConfMan().getBoolean(Setting.MARKET_MATERIALS_WHOLE_MARKET_INF_BOOLEAN);
        int timer = Math.getTicks(this.getConfMan().getInt(Setting.MARKET_SAVE_TIMER_INTEGER));
        this.saveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                saveItems();
            }
        };
        this.saveTimer.runTaskTimerAsynchronously(this.getMain(), timer, timer);
        this.loadItems();
        this.loadAliases();
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {
        this.saveTimer.cancel();
        this.saveItems();
    }

    /**
     * Returns the /buy price scaling
     */
    public double getBuyScale() {
        return this.buyScale;
    }

    /**
     * Returns the /sell price scaling
     */
    public double getSellScale() {
        return this.sellScale;
    }

    /**
     * Returns the type of the token given
     *
     * @return String
     */
    public abstract String getType();

    /**
     * Returns an item from the item HashMap, Will be none if no alias or
     * direct name is found.
     *
     * @param alias - The alias or name of the item to get.
     * @return ? extends DivinityItem - Returns the material data corresponding to the string supplied.
     */
    public MarketableToken getItem(String alias) {
        // Check aliases
        // If alias is empty then get directly from items list
        // get the material and return it, could be Null
        String matID = this.aliasMap.get(alias.toLowerCase());
        if (matID == null) matID = alias;
        return this.itemMap.get(matID.toLowerCase());
    }

    /**
     * Returns all exact item ids in the item map
     *
     * @return String[]
     */
    public String[] getItemIDs() {
        ArrayList<String> itemNames = new ArrayList<>(this.itemMap.keySet());
        itemNames.addAll(this.itemMap.keySet());
        return itemNames.toArray(new String[0]);
    }

    /**
     * Returns all exact item ids in the item map
     *
     * @return String[]
     */
    public String[] getItemIDs(String startsWith) {
        return filterItemNames(this.getItemIDs(), startsWith);
    }

    /**
     * Returns all the item names & aliases
     *
     * @return String[]
     */
    public String[] getItemNames() {
        ArrayList<String> itemNames = new ArrayList<>(this.itemMap.keySet());
        itemNames.addAll(this.aliasMap.keySet());
        return itemNames.toArray(new String[0]);
    }

    /**
     * Returns all the item names and aliases and filters them
     *
     * @param startsWith - The string to start with
     * @return String[]
     */
    public String[] getItemNames(String startsWith) {
        return this.filterItemNames(this.getItemNames(), startsWith.toLowerCase());
    }

    /**
     * Returns the aliases and names for the item ids given
     *
     * @param itemIDs
     * @return
     */
    public String[] getItemNames(String[] itemIDs) {
        ArrayList<String> itemNames = new ArrayList<>();
        Stream.of(itemIDs).filter(e -> this.revAliasMap.containsKey(e.toLowerCase())).forEach(e -> itemNames.addAll(Arrays.asList(this.revAliasMap.get(e.toLowerCase()))));
        return itemNames.toArray(new String[0]);
    }

    /**
     * Returns the aliases and names for the item ids given and filters them
     *
     * @param itemIds
     * @param startWith
     * @return
     */
    public String[] getItemNames(String[] itemIds, String startWith) {
        return this.filterItemNames(this.getItemNames(itemIds), startWith.toLowerCase());
    }

    /**
     * Filters through the given item names
     *
     * @param items
     * @param startsWith
     * @return
     */
    public String[] filterItemNames(String[] items, String startsWith) {
        ArrayList<String> itemNames = new ArrayList<>();
        Arrays.stream(items).filter(string -> string.toLowerCase().startsWith(startsWith.toLowerCase())).forEach(itemNames::add);
        return itemNames.toArray(new String[0]);
    }

    /**
     * Filters through the given item names and searches for the term
     *
     * @param items
     * @param term
     * @return
     */
    public String[] searchItemNames(String[] items, String term) {
        ArrayList<String> itemNames = new ArrayList<>();
        Arrays.stream(items).filter(string -> string.toLowerCase().contains(term.toLowerCase())).forEach(itemNames::add);
        return itemNames.toArray(new String[0]);
    }

    /**
     * Returns the number of unique items in the market
     *
     * @return int
     */
    public int getItemCount() {
        return this.itemMap.size();
    }

    /**
     * Returns the number of unique aliases in the market
     *
     * @return int
     */
    public int getAliasCount() {
        return this.aliasMap.size();
    }

    /**
     * Returns the market price based on stock
     *
     * @param stock - The stock of the item
     * @return double
     */
    public double getBuyPrice(double stock) {
        return this.getPrice(stock, this.buyScale, this.getInflation());
    }

    /**
     * Returns the user price based on stock
     *
     * @param stock - The stock of the item
     * @return double
     */
    public double getSellPrice(double stock) {
        return this.getPrice(stock, this.sellScale, this.getInflation());
    }

    /**
     * Sets the price of an item
     *
     * @param itemData - The material to set
     * @param value    - The value to set the price to
     */
    public void setPrice(MarketableToken itemData, double value) {
        itemData.setQuantity(this.calculateStock(value, this.buyScale, this.getInflation()));
    }

    /**
     * Edits the quantity of a item by quantity
     *
     * @param itemData - The item to edit
     * @param quantity - The quantity to edit by. Can be negative.
     */
    public void editQuantity(MarketableToken itemData, int quantity) {
        itemData.editQuantity(quantity);
        this.editTotalMaterials(quantity);
    }

    /**
     * Sets the quantity of item
     *
     * @param itemData - The item to set.
     * @param quantity - The quantity to edit by. Can be negative.
     */
    public void setQuantity(MarketableToken itemData, int quantity) {
        this.editQuantity(itemData, quantity - itemData.getQuantity());
    }

    /**
     * Calculates the price of a item * amount
     * This is not the same as price * amount -- Factors in price change and inflation change during purchase
     *
     * @param amount   - The amount to calculate the price for
     * @param stock    - The stock of the material
     * @param scale    - The scaling to apply, such as tax
     * @param purchase - Whether this is a purchase from or sale to the market
     * @return double
     */
    public double calculatePrice(double amount, double stock, double scale, boolean purchase) {
        return Math.calculatePrice(this.baseQuantity, stock, this.defaultTotalItems, this.totalItems, amount, scale, purchase, this.dynamicPricing, this.wholeMarketInflation);
    }

    /**
     * Returns the price for an item based on it's stock and the scale to apply
     * Scale of 1.2 = 20% additive Scale of .8 = 20% reduction
     *
     * @param stock     - The stock of the material
     * @param scale     - The scaling to apply to the price
     * @param inflation - The level of inflation
     * @return double - The price of the material
     */
    public double getPrice(double stock, double scale, double inflation) {
        if (!this.wholeMarketInflation) {
            inflation = 1.0;
        }
        return Math.getPrice(this.baseQuantity, stock, scale, inflation);
    }

    /**
     * Calculates the stock based on the price.
     *
     * @param price     - The price of the item
     * @param scale     - The scale of the price
     * @param inflation - The inflation of the price
     * @return int - The level of stock required for this price.
     */
    public int calculateStock(double price, double scale, double inflation) {
        return (int) ((this.baseQuantity / price) * scale * inflation);
    }

    /**
     * Gets the market-wide level of inflation
     *
     * @return double - The level of inflation
     */
    public double getInflation() {
        if (this.wholeMarketInflation) {
            return Math.getInflation(this.defaultTotalItems, this.totalItems);
        } else {
            return 1.0;
        }
    }

    /**
     * Adds or removes the amount from the stock
     * Used to track inflation
     *
     * @param amount - The amount to add or remove. Negative to remove.
     */
    private void editTotalMaterials(int amount) {
        this.totalItems += amount;
    }

    /**
     * Returns the total number of items in the market
     *
     * @return int
     */
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * Returns the total default number of items in the market
     *
     * @return int
     */
    public int getDefaultTotalItems() {
        return defaultTotalItems;
    }

    /**
     * Loads aliases from the aliases file into the aliases variable
     */
    public void loadAliases() {
        FileConfiguration config = this.getConfMan().loadFile(this.aliasFile);
        // Store the alias -> ItemID pairs
        Map<String, String> values = new ConcurrentHashMap<>();
        // Store ItemID -> arraylist to migrate to ItemID -> String[] pairs
        Map<String, ArrayList<String>> revBuildAliasValues = new ConcurrentHashMap<>();
        Map<String, String[]> revResultAliasValues = new ConcurrentHashMap<>();
        // Loop through keys in config
        for (String key : config.getKeys(false)) {
            // Get string item name
            // Format key/value
            key = key.toLowerCase().replace(" ", "");
            String value = config.getString(key);

            // If value is null, skip
            if (value == null) {
                if (!this.getConfMan().getBoolean(Setting.IGNORE_ALIAS_ERRORS_BOOLEAN))
                    this.getConsole().warn("Bad config value in %s: '%s' - Corresponding value is null", this.aliasFile, key);
                continue;
            }

            value = value.toLowerCase().replace(" ", "");

            // If the value is not stored in the items map, skip
            if (this.getItem(value) == null) {
                if (!this.getConfMan().getBoolean(Setting.IGNORE_ALIAS_ERRORS_BOOLEAN))
                    this.getConsole().warn("Bad config value in %s: '%s' - Corresponding value '%s' does not exist.", this.aliasFile, key, value);
                continue;
            }

            // Store the value under the key
            values.put(key, value);
            if (!values.containsKey(key)) values.put(key, key);

            // Store the value under the key (Value = materialID, key = alias)
            if (!revBuildAliasValues.containsKey(value)) {
                revBuildAliasValues.put(value, new ArrayList<>());
                revBuildAliasValues.get(value).add(value);
            }
            revBuildAliasValues.get(value).add(key);
        }
        this.aliasMap = values;

        // Migrate all keys-arraylist pairs to key-array pairs
        revBuildAliasValues.keySet().forEach(key -> revResultAliasValues.put(key, revBuildAliasValues.get(key).toArray(new String[0])));
        this.revAliasMap = revResultAliasValues;

        this.getConsole().info("Loaded %s item aliases from %s", values.size(), this.aliasFile);
    }

    /**
     * Loads the items from the items file into the items variable
     */
    public void loadItems() {
        // Load the config
        this.config = this.getConfMan().loadFile(this.itemFile);
        FileConfiguration defaultConf = this.getConfMan().readResource(this.itemFile);
        // Set material counts
        this.defaultTotalItems = 0;
        this.totalItems = 0;
        // Create a HashMap to store the values
        Map<String, MarketableToken> values = new ConcurrentHashMap<>();
        // Loop through keys and get data
        // Add data to a MaterialData and put in HashMap under key
        for (String key : this.config.getKeys(false)) {

            ConfigurationSection data = this.config.getConfigurationSection(key);
            ConfigurationSection defaultData = defaultConf.getConfigurationSection(key);

            if (data == null) {
                if (!this.getConfMan().getBoolean(Setting.IGNORE_ITEM_ERRORS_BOOLEAN))
                    this.getConsole().warn("Bad config value in %s: '%s' - Data is null", this.itemFile, key);
                continue;
            }

            MarketableToken itemData = this.loadItem(key, data, defaultData);
            if (!itemData.check()) {
                if (!this.getConfMan().getBoolean(Setting.IGNORE_ITEM_ERRORS_BOOLEAN))
                    this.getConsole().warn("Bad config value in %s for '%s': %s", this.itemFile, key, itemData.getError());
                continue;
            }

            // Format key
            key = key.toLowerCase().replace(" ", "");

            this.defaultTotalItems += itemData.getDefaultQuantity();
            this.totalItems += itemData.getQuantity();
            values.put(key, itemData);
        }
        // Copy values into items
        this.itemMap = values;
        this.getConsole().info("Loaded %s items (current/default quantities: %s / %s) from %s", values.size(), this.totalItems, this.defaultTotalItems, this.itemFile);
    }

    /**
     * Returns the item given as base class DivinityItem is abstract and cannot be instantiated
     *
     * @param data
     * @param defaultData
     * @return
     */
    public abstract MarketableToken loadItem(String ID, ConfigurationSection data, ConfigurationSection defaultData);

    /**
     * Saves a value to a key
     *
     * @param key   - The map key
     * @param value - The map value
     */
    private void setData(String key, Object value) {
        this.config.set(key, value);
    }

    /**
     * Saves the item data to the config
     *
     * @param itemData - The material to save
     */
    private void saveItem(MarketableToken itemData) {
        this.setData(itemData.getID(), itemData.getItemConfig());
    }

    /**
     * Loops through the items and saves their data to the config
     * Then saves the config to the config file
     */
    public void saveItems() {
        // Save items
        this.itemMap.values().forEach(this::saveItem);

        // save
        this.saveFile();
        this.getConsole().info("%s saved.", this.itemFile);
    }

    /**
     * Saves the config to the config file
     */
    private void saveFile() {
        // load back all info
        FileConfiguration config = this.getConfMan().loadFile(this.itemFile);
        for (String key : config.getKeys(false)) {
            String internalKey = key.toLowerCase().replace(" ", "");
            if (!this.itemMap.containsKey(internalKey)) continue;

            config.set(key, this.config.get(key));
        }

        this.getConfMan().saveFile(config, this.itemFile);
    }

}
