package me.edgrrrr.de.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.response.MultiValueResponse;
import me.edgrrrr.de.response.Response;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Enchant manager
 * Used for managing prices and quantity of enchants
 */
public class EnchantmentManager extends DivinityModule {
    // Scheduler for saving enchant data
    private BukkitRunnable saveTimer;
    // The file config
    private FileConfiguration config;
    // Stores the enchants
    private HashMap<String, EnchantData> enchants;
    // File and resource filename
    private final String enchantFile = "enchantments.yml";
    // total and default total enchants in market
    private int totalEnchants;
    private int defaultTotalEnchants;
    // Settings
    private double buyScale;
    private double sellScale;
    private int baseQuantity;
    private boolean dynamicPricing;
    private boolean wholeMarketInflation;


    /**
     * Constructor
     * Note that this does not automatically load the enchants.
     * Use #loadEnchants() to read the enchants into memory.
     * @param main The main class
     */
    public EnchantmentManager(DEPlugin main) {
        super(main);
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        this.buyScale = this.getConfig().getDouble(Setting.MARKET_ENCHANTS_BUY_TAX_FLOAT);
        this.sellScale = this.getConfig().getDouble(Setting.MARKET_ENCHANTS_SELL_TAX_FLOAT);
        this.baseQuantity = this.getConfig().getInt(Setting.MARKET_ENCHANTS_BASE_QUANTITY_INTEGER);
        this.dynamicPricing = this.getConfig().getBoolean(Setting.MARKET_ENCHANTS_DYN_PRICING_BOOLEAN);
        this.wholeMarketInflation = this.getConfig().getBoolean(Setting.MARKET_ENCHANTS_WHOLE_MARKET_INF_BOOLEAN);
        int timer = Math.getTicks(this.getConfig().getInt(Setting.MARKET_SAVE_TIMER_INTEGER));
        this.saveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                saveEnchants();
            }
        };
        this.saveTimer.runTaskTimer(this.getMain(), timer, timer);
        this.loadEnchants();
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {
        this.saveTimer.cancel();
        this.saveEnchants();
    }

    /**
     * Returns the /ebuy price scaling
     */
    public double getBuyScale() {
        return this.buyScale;
    }

    /**
     * Returns the /esell price scaling
     */
    public double getSellScale() {
        return this.sellScale;
    }

    /**
     * Returns an array of the names of the enchantments given.
     */
    public String[] getEnchantNames(Set<Enchantment> enchantmentSet) {
        ArrayList<EnchantData> enchants = new ArrayList<>();
        for (Enchantment enchantment : enchantmentSet) {
            enchants.add(this.getEnchant(enchantment.getKey().getKey()));
        }
        return this.getEnchantNames(enchants.toArray(new EnchantData[0]));
    }

    /**
     * Returns if the enchantment given is supported by the itemstack given.
     */
    public boolean supportsEnchant(ItemStack itemStack, Enchantment enchantment) {
        for (EnchantData enchantData : this.getCompatibleEnchants(itemStack)) {
            if (enchantData.getEnchantment().equals(enchantment)) return true;
        }
        return false;
    }

    /**
     * Returns an array of names of the enchants stored.
     */
    public String[] getEnchantNames() {
        return this.getEnchantNames(this.enchants.values().toArray(new EnchantData[0]));
    }

    /**
     * Returns an array of the names of the enchants given.
     */
    public String[] getEnchantNames(EnchantData[] enchants) {
        ArrayList<String> enchantNames = new ArrayList<>();
        for (EnchantData enchantData : enchants) {
            enchantNames.add(enchantData.getID().toLowerCase());
        }

        return enchantNames.toArray(new String[0]);
    }

    /**
     * Returns an array of enchants supported by the given itemstack
     */
    public EnchantData[] getCompatibleEnchants(ItemStack itemStack) {
        ArrayList<EnchantData> enchants = new ArrayList<>();
        for (EnchantData enchantData : this.enchants.values()) {
            Enchantment enchantment = enchantData.getEnchantment();
            if (enchantment.canEnchantItem(itemStack)) enchants.add(enchantData);
        }
        return enchants.toArray(new EnchantData[0]);
    }

    /**
     * Returns an array of the names of enchants that start with startsWith
     */
    public String[] getCompatibleEnchants(ItemStack itemStack, String startsWith) {
        return this.getEnchantNames(this.getEnchantNames(this.getCompatibleEnchants(itemStack)), startsWith);
    }

    /**
     * Returns an array of the names of enchants that start with startsWith
     */
    public String[] getEnchantNames(String[] enchants, String startsWith) {
        ArrayList<String> enchantNames = new ArrayList<>();
        for (String enchantName : enchants) {
            if (enchantName.toLowerCase().startsWith(startsWith.toLowerCase())) {
                enchantNames.add(enchantName);
            }
        }

        return enchantNames.toArray(new String[0]);
    }

    /**
     * Returns an array of the names of enchants that start with startsWith
     */
    public String[] getEnchantNames(String startsWith) {
        return this.getEnchantNames(this.getEnchantNames(), startsWith);
    }

    /**
     * Returns the number of enchants, each enchant being worth one enchant, not the stock of the enchants.
     */
    public int getEnchantCount() {
        return this.enchants.size();
    }

    /**
     * Loads all enchants in the config file into the internal cache.
     */
    public void loadEnchants() {
        // Load the config
        this.config = this.getConfig().loadFile(this.enchantFile);
        FileConfiguration defaultConf = this.getConfig().readResource(this.enchantFile);
        // Set material counts
        this.defaultTotalEnchants = 0;
        this.totalEnchants = 0;
        // Create a HashMap to store the values
        HashMap<String, EnchantData> values = new HashMap<>();
        // Loop through keys and get data
        // Add data to a MaterialData and put in HashMap under key
        for (String key : this.config.getKeys(false)) {
            ConfigurationSection data = this.config.getConfigurationSection(key);
            ConfigurationSection defaultData = defaultConf.getConfigurationSection(key);
            if (data == null) {
                if (!this.getConfig().getBoolean(Setting.IGNORE_ENCHANT_ERRORS_BOOLEAN)) this.getConsole().warn("Bad config value in %s: '%s' - Data is null", this.enchantFile, key);
                continue;
            }
            EnchantData enchantData = new EnchantData(data, defaultData);
            if (Enchantment.getByKey(NamespacedKey.fromString(key)) == null) {
                if (!this.getConfig().getBoolean(Setting.IGNORE_ENCHANT_ERRORS_BOOLEAN)) this.getConsole().warn("Bad config value in %s: '%s' - Enchantment does not exist.", this.enchantFile, key);
                continue;
            }
            this.defaultTotalEnchants += enchantData.getDefaultQuantity();
            this.totalEnchants += enchantData.getQuantity();
            values.put(key, enchantData);
        }
        // Copy values into materials
        this.enchants = values;
        this.getConsole().info("Loaded %s enchants (current/default quantities: %s / %s) from %s", values.size(), this.totalEnchants, this.defaultTotalEnchants, this.enchantFile);
    }

    /**
     * Removes the given enchants and levels from the itemStack given.
     * @param itemStack - The itemstack to remove the enchants from
     * @param enchantmentAndLevels - The enchantments and the level to remove
     */
    public void removeEnchantLevelsFromItem(ItemStack itemStack, HashMap<Enchantment, Integer> enchantmentAndLevels) {
        for (Enchantment enchantment : enchantmentAndLevels.keySet()) {
            this.removeEnchantLevelsFromItem(itemStack, enchantment, enchantmentAndLevels.get(enchantment));
        }
    }

    /**
     * Reduces an enchant level on an itemstack by levels amount
     * If the level is 5 and you remove 4, the level is set to 1.
     * If the level is 5 and you remove 5, the enchant is removed.
     * @param itemStack - The itemstack to remove the enchant from
     * @param enchantment - The enchantment to remove
     * @param levels - The levels to remove
     */
    public void removeEnchantLevelsFromItem(ItemStack itemStack, Enchantment enchantment, int levels) {
        int currentLevel = itemStack.getEnchantmentLevel(enchantment);
        itemStack.removeEnchantment(enchantment);
        int levelsLeft = currentLevel - levels;
        if (levelsLeft > 0) {
            itemStack.addUnsafeEnchantment(enchantment, levelsLeft);
        }
    }

    /**
     * Adds an enchant to an item
     * @param itemStack - The itemstack
     * @param enchantment - The enchant
     * @param levels - The levels
     * @return Response
     */
    public Response addEnchantToItem(ItemStack itemStack, Enchantment enchantment, int levels) {
        Response response;
        int newLevel = levels + itemStack.getEnchantmentLevel(enchantment);
        EnchantData enchantData = this.getEnchant(enchantment.getKey().getKey());
        if (enchantData == null) {
            response = new Response(EconomyResponse.ResponseType.FAILURE, "enchant is not supported");
        } else {
            if (enchantData.getMaxLevel() < newLevel) {
                response = new Response(EconomyResponse.ResponseType.FAILURE, String.format("level is greater than max (%d/%d)", newLevel, enchantData.getMaxLevel()));
            } else {
                itemStack.addUnsafeEnchantment(enchantment, newLevel);
                response = new Response(EconomyResponse.ResponseType.SUCCESS, "");
            }
        }

        return response;
    }

    /**
     * Returns the enchantData for the enchantID provided.
     * @param name - The name ID of the enchant
     * @return EnchantData
     */
    public EnchantData getEnchant(String name) {
        name = name.trim().toLowerCase();
        return this.enchants.get(name);
    }

    /**
     * Returns if an item is enchanted or not.
     * Supports un-enchantable items
     * @param itemStack - The itemstack to check
     * @return boolean - Is enchanted / Is not enchanted
     */
    public boolean isEnchanted(ItemStack itemStack) {
        return itemStack.getEnchantments().size() >= 1;
    }

    /**
     * Returns the enchant level of the given enchant on the given itemstack
     * Will return 0 if the enchant does not exist / is not on the itemstack
     * @param itemStack - The itemstack
     * @param enchantName - The enchant name
     */
    public int getEnchantLevel(ItemStack itemStack, String enchantName) {
        int level = 0;
        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment.getKey().getKey().equals(enchantName)) {
                level = enchantments.get(enchantment);
                break;
            }
        }
        return level;
    }

    /**
     * Returns A hashmap of the enchantdata, level
     * If there are no enchants, the hashmap will be empty.
     * Supports unenchantable items
     * @param itemStack - The itemstack to check.
     * @return HashMap<EnchantData, Integer> - The Enchants and their respective level
     */
    public HashMap<EnchantData, Integer> getEnchantLevels(ItemStack itemStack) {
        HashMap<EnchantData, Integer> enchants = new HashMap<>();
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put(getEnchant(enchantment.getKey().getKey()), itemStack.getEnchantmentLevel(enchantment));
        }
        return enchants;
    }

    /**
     * Returns the total purchase price of all the enchants on an itemstack
     * @param itemStack - The itemstack to check
     * @return MultiValueResponse
     */
    public MultiValueResponse getBuyValue(ItemStack itemStack) {
        HashMap<String, Double> values = MultiValueResponse.createValues();
        HashMap<String, Integer> quantities = MultiValueResponse.createQuantities();
        EconomyResponse.ResponseType responseType = EconomyResponse.ResponseType.SUCCESS;
        String errorMessage = "";

        Map<Enchantment, Integer> enchantmentLevels = itemStack.getEnchantments();
        for (Enchantment enchantment : enchantmentLevels.keySet()) {
            int level = enchantmentLevels.get(enchantment);
            String enchantID = enchantment.getKey().getKey();
            ValueResponse valueResponse = this.getBuyValue(itemStack, enchantID, level);
            if (valueResponse.isFailure()) {
                errorMessage = valueResponse.errorMessage;
                responseType = valueResponse.responseType;
                break;
            } else {
                values.put(enchantID, valueResponse.value);
                quantities.put(enchantID, level);
            }
        }

        return new MultiValueResponse(values, quantities, responseType, errorMessage);
    }

    /**
     * Returns the purchase value of the enchantID provided at the given level.
     * @param enchantID - The enchantment ID
     * @param levelsToBuy - The enchantment level
     * @param itemStack - The itemStack to apply to
     * @return EnchantValueResponse - The value of the enchant
     */
    public ValueResponse getBuyValue(ItemStack itemStack, String enchantID, int levelsToBuy) {
        EnchantData enchantData = this.getEnchant(enchantID);
        ValueResponse response;
        if (enchantData == null) {
            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("enchant id %s does not exist", enchantID));

        } else {
            if (enchantData.getEnchantment() == null) {
                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("enchant id %s does not exist in the store", enchantID));

            } else {
                if (!(enchantData.getAllowed())) {
                    response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "enchant is not allowed to be bought or sold");

                } else {
                    int itemStackEnchantmentLevel = itemStack.getEnchantmentLevel(enchantData.getEnchantment());
                    int newTotalLevel = itemStackEnchantmentLevel + levelsToBuy;
                    if (enchantData.getMaxLevel() < newTotalLevel) {
                        response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("level would be above max(%d/%d)", newTotalLevel, enchantData.getMaxLevel()));

                    } else {
                        int enchantAmount = EnchantData.levelsToBooks(itemStackEnchantmentLevel, newTotalLevel);
                        if (enchantAmount > enchantData.getQuantity()) {
                            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("not enough stock (%d/%d)", enchantAmount, enchantData.getQuantity()));

                        } else {
                            double price = this.calculatePrice(enchantAmount, enchantData.getQuantity(), this.buyScale, false);
                            if (price > 0) {
                                response = new ValueResponse(price, EconomyResponse.ResponseType.SUCCESS, "");
                            } else {
                                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "market is saturated.");
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    /**
     * Returns the sell value of all enchants on an itemstack
     * @param itemStack - The itemstack to check
     * @return MultiEnchantValueResponse - The value of each enchant
     */
    public MultiValueResponse getSellValue(ItemStack itemStack) {
        HashMap<String, Double> values = MultiValueResponse.createValues();
        HashMap<String, Integer> quantities = MultiValueResponse.createQuantities();
        EconomyResponse.ResponseType responseType = EconomyResponse.ResponseType.SUCCESS;
        String errorMessage = "";

        Map<Enchantment, Integer> itemStackEnchants = itemStack.getEnchantments();
        for (Enchantment enchantment : itemStackEnchants.keySet()) {
            int level = itemStackEnchants.get(enchantment);
            String enchantID = enchantment.getKey().getKey();
            ValueResponse valueResponse = this.getSellValue(itemStack, enchantID, level);
            if (valueResponse.isFailure()) {
                errorMessage = valueResponse.errorMessage;
                responseType = valueResponse.responseType;
                break;
            } else {
                values.put(enchantID, valueResponse.value);
                quantities.put(enchantID, level);
            }
        }

        return new MultiValueResponse(values, quantities, responseType, errorMessage);
    }

    /**
     * Returns the value of an enchant on an item.
     * @param itemStack - The itemstack to check
     * @param enchantID - The enchant ID to check for
     * @param levelsToSell - The level to value
     * @return ValueResponse
     */
    public ValueResponse getSellValue(ItemStack itemStack, String enchantID, int levelsToSell) {
        EnchantData enchantData = this.getEnchant(enchantID);
        ValueResponse response;
        if (enchantData == null) {
            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("unknown enchant id %s", enchantID));
        } else{

            Enchantment enchantment = enchantData.getEnchantment();
            if (enchantment == null) {
                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("unknown enchant id %s", enchantID));

            } else {

                if (!enchantData.getAllowed()) {
                    response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "this enchant is not allowed to be bought or sold.");

                } else {

                    Map<Enchantment, Integer> itemStackEnchants = itemStack.getEnchantments();
                    if (!itemStackEnchants.containsKey(enchantment)) {
                        response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("item does not have enchant %s", enchantID));

                    } else {

                        int itemStackEnchantLevel = itemStackEnchants.get(enchantment);
                        if (itemStackEnchantLevel < levelsToSell) {
                            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("item enchant does not have enough levels(%d/%d)", itemStackEnchantLevel, levelsToSell));
                        } else {
                            double value = this.calculatePrice(EnchantData.levelsToBooks(itemStackEnchantLevel, itemStackEnchantLevel-levelsToSell), enchantData.getQuantity(), this.sellScale, false);
                            if (value > 0) {
                                response = new ValueResponse(value, EconomyResponse.ResponseType.SUCCESS, "");
                            } else {
                                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "market is saturated.");
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    /**
     * Edits the quantity of an enchant & the total quantity of enchants
     * @param enchantData - The enchant to edit
     * @param quantity - The quantity to edit by. Can be negative.
     */
    public void editQuantity(EnchantData enchantData, int quantity) {
        this.editTotalEnchants(quantity);
        enchantData.editQuantity(quantity);
    }

    /**
     * Edits the quantity of an enchant & the total quantity of enchants
     * @param enchantData - The enchant to edit
     * @param levels - The quantity to edit by, in levels. Can be negative.
     */
    public void editLevelQuantity(EnchantData enchantData, int levels) {
        int books;
        if (levels > 0) {
            books = EnchantData.levelsToBooks(0, levels);
        } else {
            books = -EnchantData.levelsToBooks(0, -levels);
        }
        this.editQuantity(enchantData, books);
    }

    /**
     * Sets the price of a enchant
     * @param enchantData - The enchant to set
     * @param value - The value to set the price to
     */
    public void setPrice(EnchantData enchantData, double value) {
        enchantData.setQuantity(this.calculateStock(value, this.buyScale, this.getInflation()));
    }

    /**
     * Sets the quantity of an enchant & edits the total quantity of enchants
     * @param enchantData - The enchant
     * @param quantity - The quantity to set to
     */
    public void setQuantity(EnchantData enchantData, int quantity) {
        this.editQuantity(enchantData, quantity - enchantData.getQuantity());
    }

    /**
     * Edits the total quantity of enchants in the market
     * @param quantity - The quantity to edit by. Can be negative.
     */
    private void editTotalEnchants(int quantity) {
        this.totalEnchants += quantity;
    }

    /**
     * Returns the total number of enchants in the market
     * @return int
     */
    public int getTotalEnchants() {
        return totalEnchants;
    }

    /**
     * Returns the default total number of enchants in the market
     * @return int
     */
    public int getDefaultTotalEnchants() {
        return defaultTotalEnchants;
    }

    /**
     * Calculates the price of an enchant based on it's parameters
     * @param amount - The amount of the enchant to buy/sell
     * @param stock - The amount of the enchant in stock
     * @param scale - The scaling (tax) to apply to the purchase/sale
     * @param purchase - If the user is buy / if the user is not buying (E.g. selling)
     * @return double - the price of amount of this enchant
     */
    public double calculatePrice(double amount, double stock, double scale, boolean purchase) {
        return Math.calculatePrice(this.baseQuantity, stock, this.defaultTotalEnchants, this.totalEnchants, amount, scale, purchase, this.dynamicPricing, this.wholeMarketInflation);
    }

    /**
     * Calculates the stock of enchants based on the given parameters.
     * @param price - The price of the enchant
     * @param scale - The economy scaling
     * @param inflation - The economy inflation
     */
    public int calculateStock(double price, double scale, double inflation) {
        return (int) ((this.baseQuantity / price) * scale * inflation);
    }

    /**
     * Returns the inflation of the whole enchant market
     * @return double
     */
    public double getInflation() {
        if (this.wholeMarketInflation) {
            return Math.getInflation(this.defaultTotalEnchants, this.totalEnchants);
        } else {
            return 1.0;
        }
    }

    /**
     * Returns the price of a single enchant book
     * @param stock - The stock of the enchant
     * @param scale - The scaling to apply to the purchase/sale
     * @param inflation - The inflation of the market
     * @return double
     */
    public double getPrice(double stock, double scale, double inflation) {
        if (!this.wholeMarketInflation) {
            inflation = 1.0;
        }
        return Math.getPrice(this.baseQuantity, stock, scale, inflation);
    }

    public double getUserPrice(EnchantData enchantData) {
        return this.getUserPrice(enchantData.getQuantity());
    }

    /**
     * Returns the user price of a single enchant
     * @param stock - The stock of the enchant
     * @return double
     */
    public double getUserPrice(double stock) {
        return this.getPrice(stock, this.buyScale, this.getInflation());
    }

    public double getMarketPrice(EnchantData enchantData) {
        return this.getMarketPrice(enchantData.getQuantity());
    }

    /**
     * Returns the market price of a single enchant
     * @param stock - The stock of the enchant
     * @return double
     */
    public double getMarketPrice(double stock) {
        return this.getPrice(stock, this.sellScale, this.getInflation());
    }

    /**
     * Saves the enchant data to the loaded config
     * @param enchantData - The enchant to save
     */
    public void saveEnchant(EnchantData enchantData) {
        this.config.set(enchantData.getID(), enchantData.getConfigurationSection());
    }

    /**
     * Saves all internal enchants to the internal config
     * Then saves the internal config to the save file
     */
    public void saveEnchants() {
        for (EnchantData enchantData : this.enchants.values()) {
            this.saveEnchant(enchantData);
        }
        this.saveFile();
        this.getConsole().info("Enchants saved.");
    }

    /**
     * Saves the internal config to the save file
     */
    private void saveFile() {
        // load back all info
        FileConfiguration config = this.getConfig().loadFile(this.enchantFile);
        for (String key : config.getKeys(false)) {
            if (!this.enchants.containsKey(key)) continue;

            config.set(key, this.config.get(key));
        }

        this.getConfig().saveFile(config, this.enchantFile);
    }
}
