package me.edgrrrr.de.market;

import com.tchristofferson.configupdater.ConfigUpdater;
import me.edgrrrr.de.Constants;
import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final int MAX_SEARCH_DEPTH_INT = 64;
    private static final long MAX_SEARCH_NANO_LONG = 50000000L; // 50ms
    private TokenLRUCache itemNamesCache;
    protected Map<String, Set<String>> revAliasMap;
    protected Map<String, ? extends MarketableToken> itemMap;
    // Used for calculating inflation/deflation
    protected long totalItems;
    protected long defaultTotalItems;
    // Other settings
    protected double maxItemValue;
    protected double minItemValue;
    protected double buyScale;
    protected double sellScale;
    protected double baseQuantity;
    protected boolean dynamicPricing;
    protected boolean wholeMarketInflation;
    protected boolean ignoreNamedItems;
    protected boolean saveMessagesDisabled;
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
        this.itemNamesCache = new TokenLRUCache(main);
        this.itemMap = itemMap;
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
     * Returns maximum item value
     */
    public double getMaxItemValue() {
        return this.maxItemValue;
    }

    /**
     * Returns minimum item value
     */
    public double getMinItemValue() {
        return this.minItemValue;
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
    public Set<String> getItemIDs() {
        Set<String> itemNames = new HashSet<>(this.itemMap.keySet());
        itemNames.addAll(this.itemMap.keySet());
        return itemNames;
    }

    /**
     * Returns all exact item ids in the item map
     *
     * @return String[]
     */
    public Set<String> getItemIDs(String startsWith) {
        return searchItemNames(this.getItemIDs(), startsWith);
    }

    /**
     * Returns all the item names & aliases
     *
     * @return String[]
     */
    public Set<String> getItemNames() {
        Set<String> itemNames = new HashSet<>(this.itemMap.keySet());
        itemNames.addAll(this.aliasMap.keySet());
        return itemNames;
    }

    /**
     * Returns all the item names and aliases and filters them
     *
     * @param startsWith - The string to start with
     * @return String[]
     */
    public Set<String> getItemNames(String startsWith) {
        return this.searchItemNames(this.getItemNames(), startsWith.toLowerCase());
    }

    /**
     * Returns the aliases and names for the item ids given
     *
     * @param itemIDs
     * @return
     */
    public Set<String> getItemNames(Set<String> itemIDs) {
        Set<String> itemNames = new HashSet<>();
        for (String itemID : itemIDs) {
            if (this.revAliasMap.containsKey(itemID.toLowerCase())) {
                itemNames.addAll(this.revAliasMap.get(itemID.toLowerCase()));
            }
        }
        return itemNames;
    }

    /**
     * Returns the aliases and names for the item ids given and filters them
     *
     * @param itemIds
     * @param startWith
     * @return
     */
    public Set<String> getItemNames(Set<String> itemIds, String startWith) {
        return this.searchItemNames(this.getItemNames(itemIds), startWith.toLowerCase());
    }


    public Set<String> searchItemNames(Set<String> items, String term) {
        Set<String> itemNames = this.itemNamesCache.get(term);
        if (itemNames == null) {
            itemNames = this.searchItemNamesUncached(items, term);
            this.itemNamesCache.put(term, itemNames);
        }

        return itemNames;
    }


    /**
     * Filters through the given item names
     *
     * @param items
     * @param term
     * @return
     */
    private Set<String> searchItemNamesUncached(Set<String> items, String term) {
        term = term.toLowerCase().strip(); // Standardise term
        Set<String> itemNames = new HashSet<>(); // Create itemNames array

        // Priority store
        Set<String> priority0ArrayList = new HashSet<>();
        Set<String> priority1ArrayList = new HashSet<>();
        Set<String> priority2ArrayList = new HashSet<>();
        Set<String> priority3ArrayList = new HashSet<>();

        // Counter
        long startTime = System.nanoTime();

        // Loop through items, add any item that
        // - contains <term>
        // - equals <term>
        // - startswith <term>
        // - endswith <term>
        for (String item : items) {
            // Check max search depth
            int depth = priority0ArrayList.size() + priority1ArrayList.size() + priority2ArrayList.size() + priority3ArrayList.size();
            if (depth > MAX_SEARCH_DEPTH_INT) {
                this.getConsole().debug("Max search depth reached, stopping search.");
                break;
            }


            // Check max search time
            if (System.nanoTime() - startTime > MAX_SEARCH_NANO_LONG) {
                this.getConsole().debug("Max search time reached, stopping search.");
                break;
            }

            String thisItem = item.toLowerCase().strip(); // Get & Standardise item

            // Matches - priority 0
            if (thisItem.equalsIgnoreCase(term)) {
                priority0ArrayList.add(item);
                continue;
            }

            // Begins with - priority 1
            if (thisItem.startsWith(term)) {
                priority1ArrayList.add(item);
                continue;
            }

            // Contains - priority 2
            if (thisItem.contains(term)) {
                priority2ArrayList.add(item);
                continue;
            }

            // Endswith - priority 3
            if (thisItem.endsWith(term)) {
                priority3ArrayList.add(item);
                continue;
            }
        }

        // Add by priority
        itemNames.addAll(priority0ArrayList);
        itemNames.addAll(priority1ArrayList);
        itemNames.addAll(priority2ArrayList);
        itemNames.addAll(priority3ArrayList);

        // Debug
        this.getConsole().debug("Found %s items matching '%s' in %s milliseconds.", itemNames.size(), term, (System.nanoTime() - startTime) / 1000000);


        // Return
        return itemNames;
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
     * Returns the allowed state of the item
     * @param itemData
     * @return
     */
    public boolean getAllowed(MarketableToken itemData) {
        return itemData.getAllowed();
    }


    /**
     * Sets the allowed state of the item
     * @param allowed
     * @return
     */
    public boolean setAllowed(MarketableToken itemData, boolean allowed) {
        itemData.setAllowed(allowed);
        return this.getAllowed(itemData);
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
        return calculatePrice(this.baseQuantity, stock, this.defaultTotalItems, this.totalItems, amount, scale, purchase, this.dynamicPricing, this.wholeMarketInflation);
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
        return this.fitPriceToConstraints(getPrice(this.baseQuantity, stock, scale, inflation));
    }

    /**
     * Returns the price of an item fit to the max and min constraints
     * @param price
     * @return
     */
    public double fitPriceToConstraints(double price) {
        return Converter.constrainDouble(price, this.minItemValue, this.maxItemValue);

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
        // Reverse fitPriceToConstraints and inflation/scaling factors to get the raw price
        double rawPrice = price / (scale * inflation);

        // Since the raw price is calculated as (scale * 10) + (scale * 5)
        // We need to isolate the scale, which is baseQuantity / currentQuantity
        double finalScale = rawPrice / 15;

        // Now, use the scale to find the current quantity
        double newQuantity = this.baseQuantity / finalScale;

        return (int) newQuantity;
    }


    /**
     * Gets the market-wide level of inflation
     *
     * @return double - The level of inflation
     */
    public double getInflation() {
        if (this.wholeMarketInflation) {
            return Converter.constrainDouble(getInflation(this.defaultTotalItems, this.totalItems), 0 , 100);
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
    public long getTotalItems() {
        return totalItems;
    }

    /**
     * Returns the total default number of items in the market
     *
     * @return int
     */
    public long getDefaultTotalItems() {
        return defaultTotalItems;
    }

    /**
     * Loads aliases from the aliases file into the aliases variable
     */
    public void loadAliases() {
        // Update config
        try {
            // Create alias file, if necessary
            File aliasFile = this.getConfMan().getFile(this.aliasFile);
            if (!aliasFile.exists()) {
                aliasFile.createNewFile();
            }

            // Run Update
            ConfigUpdater.update(getMain(), this.aliasFile, aliasFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load config
        FileConfiguration config = this.getConfMan().loadFile(this.aliasFile);

        // Store the alias -> ItemID pairs
        Map<String, String> values = new ConcurrentHashMap<>();
        // Store ItemID -> arraylist to migrate to ItemID -> String[] pairs
        Map<String, Set<String>> revBuildAliasValues = new ConcurrentHashMap<>();
        Map<String, Set<String>> revResultAliasValues = new ConcurrentHashMap<>();
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
                revBuildAliasValues.put(value, new HashSet<>());
                revBuildAliasValues.get(value).add(value);
            }
            revBuildAliasValues.get(value).add(key);
        }
        this.aliasMap = values;

        // Migrate all keys-arraylist pairs to key-array pairs
        revBuildAliasValues.keySet().forEach(key -> revResultAliasValues.put(key, revBuildAliasValues.get(key)));
        this.revAliasMap = revResultAliasValues;

        this.getConsole().info(LangEntry.MARKET_ItemAliasesLoaded.get(getMain()), values.size(), this.aliasFile);
    }

    /**
     * Loads the items from the items file into the items variable
     */
    public void loadItems() {
        // Update config
        try {
            // Create item file, if necessary
            File itemFile = this.getConfMan().getFile(this.itemFile);
            if (!itemFile.exists()) {
                itemFile.createNewFile();
            }

            // Run Update
            ConfigUpdater.update(getMain(), this.itemFile, this.getConfMan().getFile(this.itemFile), Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load the config
        this.config = this.getConfMan().loadFile(this.itemFile);
        FileConfiguration defaultConf = this.getConfMan().readResource(this.itemFile);

        // Set material counts
        this.defaultTotalItems = 0L;
        this.totalItems = 0L;
        // Create a HashMap to store the values
        Map<String, MarketableToken> values = new ConcurrentHashMap<>();
        // Loop through keys and get data
        // Add data to a MaterialData and put in HashMap under key
        for (String key : defaultConf.getKeys(false)) {

            ConfigurationSection data = this.config.getConfigurationSection(key);
            ConfigurationSection defaultData = defaultConf.getConfigurationSection(key);

            if (data == null) {
                if (!this.getConfMan().getBoolean(Setting.IGNORE_ITEM_ERRORS_BOOLEAN))
                    this.getConsole().warn("Bad config value in %s: '%s' - Data is null, setting default.", this.itemFile, key);
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
        this.getConsole().info(LangEntry.MARKET_ItemsLoaded.get(getMain()), values.size(), this.totalItems, this.defaultTotalItems, this.itemFile);
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
        if (!this.saveMessagesDisabled) {
            this.getConsole().info(LangEntry.GENERIC_FileSaved.get(getMain()), this.itemFile);
        }
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


    /**
     * Calculates the price of an amount of items
     *
     * @param baseQuantity      - The base quantity of the item
     * @param currentQuantity   - The current quantity of the item
     * @param defaultMarketSize - The default market size
     * @param marketSize        - The current market size
     * @param amount            - The amount of the item to buy
     * @param scale             - The price scaling (e.g. tax)
     * @param purchase          - Whether this is a purchase or a sale.
     * @return double
     */
    public double calculatePrice(double baseQuantity, double currentQuantity, double defaultMarketSize, double marketSize, double amount, double scale, boolean purchase, boolean dynamic, boolean marketInflation) {
        double value = 0;
        double inflation = 1.0;

        // Loop for amount
        // Get the price and add it to the value
        // if purchase = true
        // remove 1 stock to simulate decrease
        // if purchase = false
        // add 1 stock to simulate increase
        for (int i = 0; i < Converter.constrainInt((int) amount, Constants.MIN_VALUE_AMOUNT, Constants.MAX_VALUE_AMOUNT); i++) {
            if (marketInflation) {
                inflation = getInflation(defaultMarketSize, marketSize);
            }

            if (purchase) {
                value += getPrice(baseQuantity, currentQuantity, scale, inflation);
                if (dynamic) currentQuantity -= 1;
                if (marketInflation) marketSize -= 1;

            } else {
                value += getPrice(baseQuantity, currentQuantity + 1, scale, inflation);
                if (dynamic) currentQuantity += 1;
                if (marketInflation) marketSize += 1;
            }
        }

        return value;
    }

    /**
     * Returns the price of a product considering base and current quantities,
     * scale factor, and inflation.
     *
     * @param baseQuantity    - Base quantity of the product in the market.
     * @param currentQuantity - Current quantity of the product in the market.
     * @param scale           - Scaling factor to apply to the price.
     * @param inflation       - Inflation factor in the market.
     * @return double - The final price of the product.
     */
    public double getPrice(double baseQuantity, double currentQuantity, double scale, double inflation) {
        // if currentQuantity is zero, increment it by one to avoid division by zero
        currentQuantity = Math.max(currentQuantity, 1);

        // get the raw price
        double rawPrice = getRawPrice(baseQuantity, currentQuantity);

        // apply scaling and inflation factors, then fit the price to the required constraints
        return fitPriceToConstraints(rawPrice * scale * inflation);
    }

    /**
     * Calculates the raw price of the product based on base and current quantities.
     *
     * @param baseQuantity    - Base quantity of the product.
     * @param currentQuantity - Current quantity of the product.
     * @return double - The raw price of the product.
     */
    private double getRawPrice(double baseQuantity, double currentQuantity) {
        // calculate scale and apply it to the price
        double scale = getScale(baseQuantity, currentQuantity);

        // calculate raw price and fit it to the required constraints
        return fitPriceToConstraints(scale * 15);
    }

    /**
     * Calculates the inflation factor based on default and actual market sizes.
     * It's essentially a wrapper around getScale function.
     *
     * @param defaultMarketSize - Default quantity of materials in the market.
     * @param actualMarketSize - Actual quantity of materials in the market.
     * @return double - The inflation factor.
     */
    public double getInflation(double defaultMarketSize, double actualMarketSize) {
        return getScale(defaultMarketSize, actualMarketSize);
    }

    /**
     * Calculates the scale of a number based on its base value.
     * It's essentially the ratio of base quantity to the current quantity.
     * Uses caching to store previously calculated values.
     *
     * @param baseQuantity    - Base quantity of the product.
     * @param currentQuantity - Current quantity of the product.
     * @return double - The scale factor.
     */
    public double getScale(double baseQuantity, double currentQuantity) {
        return baseQuantity / currentQuantity;
    }

}
