package edgrrrr.dce.enchants;

import edgrrrr.dce.main.DCEPlugin;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.response.MultiValueResponse;
import edgrrrr.dce.response.ValueResponse;
import com.sun.istack.internal.NotNull;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;


/**
 * Enchant manager
 * Used for managing prices and quantity of enchants
 */
public class EnchantmentManager {
    // Link bank to main
    private final DCEPlugin app;
    // Scheduler for saving enchant data
    private final BukkitRunnable saveTimer;
    // The file config
    private FileConfiguration config;
    // Stores the enchants
    public HashMap<String, EnchantData> enchants;
    // File and resource filename
    private final String enchantFile = "enchantments.yml";
    // total and default total enchants in market
    private int totalEnchants;
    private int defaultTotalEnchants;
    // Settings
    private final double enchantBuyTax;
    private final double enchantSellTax;
    private final int enchantBaseQuantity;


    /**
     * Constructor
     * Note that this does not automatically load the enchants.
     * Use #loadEnchants() to read the enchants into memory.
     * @param app The main class
     */
    public EnchantmentManager(DCEPlugin app) {
        this.app = app;
        this.enchantBuyTax = this.app.getConfig().getDouble(this.app.getConfigManager().strEnchantBuyTax);
        this.enchantSellTax = this.app.getConfig().getDouble(this.app.getConfigManager().strMaterialSellTax);
        this.enchantBaseQuantity = this.app.getConfig().getInt(this.app.getConfigManager().strMaterialBaseQuantity);
        this.saveTimer = new BukkitRunnable() {
            @Override
            public void run() {

            }
        };
    }

    /**
     * Loads all enchants in the config file into the internal cache.
     */
    public void loadEnchants() {
        // Load the config
        this.config = this.app.getConfigManager().loadConfig(this.enchantFile);
        FileConfiguration defaultConf = this.app.getConfigManager().readResource(this.enchantFile);
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
            EnchantData enchantData = new EnchantData(this, data, defaultData);
            this.defaultTotalEnchants += enchantData.getDefaultQuantity();
            this.totalEnchants += enchantData.getQuantity();
            values.put(key, enchantData);
        }
        // Copy values into materials
        this.enchants = values;
        this.app.getConsoleManager().info("Loaded " + values.size() + "(" + this.totalEnchants + "/" + this.defaultTotalEnchants + ") enchantments from " + this.enchantFile);
    }

    /**
     * Calculates the number of enchant books required for the level provided.
     * @param enchantLevel - The enchant level
     * @return int - The number of enchants required for this level
     */
    @NotNull
    public int getEnchantAmount(int enchantLevel) {
        int enchantAmount = 0;
        if (enchantLevel > 0) {
            enchantAmount = (int) java.lang.Math.pow(2, enchantLevel);
        }
        return enchantAmount;
    }

    /**
     * Returns the enchantData for the enchantID provided.
     * @param name - The name ID of the enchant
     * @return EnchantData
     */
    @Nullable
    public EnchantData getEnchant(String name) {
        return this.enchants.get(name);
    }

    /**
     * Returns if an item is enchanted or not.
     * Supports unenchantable items
     * @param itemStack - The itemstack to check
     * @return boolean - Is enchanted / Is not enchanted
     */
    @NotNull
    public boolean isEnchanted(ItemStack itemStack) {
        return itemStack.getEnchantments().size() >= 1;
    }

    /**
     * Returns the enchant level of the given enchant on the given itemstack
     * Will return 0 if the enchant does not exist / is not on the itemstack
     * @param itemStack
     * @param enchantName
     * @return
     */
    @NotNull
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
    @NotNull
    public HashMap<EnchantData, Integer> getEnchantLevels(ItemStack itemStack) {
        HashMap<EnchantData, Integer> enchants = new HashMap<>();
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put(getEnchant(enchantment.getKey().getKey()), itemStack.getEnchantmentLevel(enchantment));
        }
        return enchants;
    }

    /**
     * Returns the purchase value of the enchantID provided at the given level.
     * @param enchantID - The enchantment ID
     * @param level - The enchantment level
     * @return EnchantValueResponse - The value of the enchant
     */
    @NotNull
    public ValueResponse getBuyValue(String enchantID, Integer level) {
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
                    if (enchantData.getMaxLevel() < level) {
                        response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("level is above max(%d)", enchantData.getMaxLevel()));

                    } else {
                        int enchantAmount = this.getEnchantAmount(level);
                        if (enchantAmount > enchantData.getQuantity()) {
                            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("not enough stock (%d/%d)", enchantAmount, enchantData.getQuantity()));

                        } else {
                            double price = this.calculatePrice(enchantAmount, enchantData.getQuantity(), this.enchantBuyTax, false);
                            response = new ValueResponse(price, EconomyResponse.ResponseType.SUCCESS, "");
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
    @NotNull
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
     * @param level - The level to value
     * @return ValueResponse
     */
    @NotNull
    public ValueResponse getSellValue(ItemStack itemStack, String enchantID, int level) {
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
                    response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("this enchant is not allowed to be bought or sold."));

                } else {

                    Map<Enchantment, Integer> itemStackEnchants = itemStack.getEnchantments();
                    if (!itemStackEnchants.containsKey(enchantment)) {
                        response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("item does not have enchant %s", enchantID));
                    } else {

                        int itemStackEnchantLevel = itemStackEnchants.get(enchantment);
                        if (itemStackEnchantLevel < level) {
                            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("item enchant does not have enough levels(%d/%d)", itemStackEnchantLevel, level));
                        } else {

                            response = new ValueResponse(this.calculatePrice(this.getEnchantAmount(level), enchantData.getQuantity(), this.enchantSellTax, false), EconomyResponse.ResponseType.SUCCESS, "");
                        }
                    }
                }
            }
        }
        return response;
    }

    /**
     * Edits the total number of enchants in the market
     * Negative to remove, Positive to add
     * @param amount - The amount to edit by
     */
    public void editTotalEnchants(int amount) {
        this.totalEnchants += amount;
    }

    /**
     * Returns the total number of enchants in the market
     * @return int
     */
    @NotNull
    public int getTotalEnchants() {
        return totalEnchants;
    }

    /**
     * Returns the default total number of enchants in the market
     * @return int
     */
    @NotNull
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
    @NotNull
    public double calculatePrice(double amount, double stock, double scale, boolean purchase) {
        return Math.calculatePrice(this.enchantBaseQuantity, stock, this.defaultTotalEnchants, this.totalEnchants, amount, scale, purchase);
    }

    /**
     * Returns the inflation of the whole enchant market
     * @return double
     */
    @NotNull
    public double getInflation() {
        return Math.getInflation(this.defaultTotalEnchants, this.totalEnchants);
    }

    /**
     * Returns the price of a single enchant book
     * @param stock - The stock of the enchant
     * @param scale - The scaling to apply to the purchase/sale
     * @param inflation - The inflation of the market
     * @return double
     */
    @NotNull
    public double getPrice(double stock, double scale, double inflation) {
        return Math.getPrice(this.enchantBaseQuantity, stock, scale, inflation);
    }

    /**
     * Returns the user price of a single enchant
     * @param stock - The stock of the enchant
     * @return double
     */
    @NotNull
    public double getUserPrice(double stock) {
        return this.getPrice(stock, this.enchantBuyTax, this.getInflation());
    }

    /**
     * Returns the market price of a single enchant
     * @param stock - The stock of the enchant
     * @return double
     */
    @NotNull
    public double getMarketPrice(double stock) {
        return this.getPrice(stock, this.enchantSellTax, this.getInflation());
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
        this.app.getConsoleManager().info("Materials saved.");
    }

    /**
     * Saves the internal config to the save file
     */
    public void saveFile() {
        this.app.getConfigManager().saveFile(this.config, this.enchantFile);
    }
}
