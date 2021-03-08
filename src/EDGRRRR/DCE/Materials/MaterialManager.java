package edgrrrr.dce.materials;

import edgrrrr.dce.config.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class MaterialManager {
    // Link back to Main
    private final DCEPlugin app;
    // Save time scheduler
    private final BukkitRunnable saveTimer;
    // Stores the default items.json file location
    private final String materialsFile = "materials.yml";
    private final String aliasesFile = "aliases.yml";
    // Stores the materials and the aliases
    public HashMap<String, String> aliases;
    public HashMap<String, MaterialData> materials;
    // Used for calculating inflation/deflation
    private int totalMaterials;
    private int defaultTotalMaterials;
    // Other settings
    private final double materialBuyTax;
    private final double materialSellTax;
    private final double materialBaseQuantity;
    // Stores items
    private FileConfiguration config;

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and materials with data from the program
     *
     * @param app - The plugin
     */
    public MaterialManager(DCEPlugin app) {
        this.app = app;
        this.materialBuyTax = this.app.getConfig().getDouble(Setting.MARKET_MATERIALS_BUY_TAX_FLOAT.path());
        this.materialSellTax = this.app.getConfig().getDouble(Setting.MARKET_MATERIALS_SELL_TAX_FLOAT.path());
        this.materialBaseQuantity = this.app.getConfig().getInt(Setting.MARKET_MATERIALS_BASE_QUANTITY_INTEGER.path());
        int timer = Math.getTicks(this.app.getConfig().getInt(Setting.MARKET_SAVE_TIMER_INTEGER.path()));
        this.saveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                saveMaterials();
            }
        };
        this.saveTimer.runTaskTimer(this.app, timer, timer);
    }

    /**
     * Returns a material from the materialData HashMap Will be none if no alias or
     * direct name is found.
     *
     * @param alias - The alias or name of the material to get.
     * @return MaterialData - Returns the material data corresponding to the string supplied.
     */
    public MaterialData getMaterial(String alias) {
        // Check aliases
        // If alias is empty then get directly from materials list
        // get the material and return it, could be Null
        String matID = aliases.get(alias);
        if (matID == null)
            matID = alias;
        return this.materials.get(matID);
    }

    /**
     * Returns the scaling of price for an item, based on its durability and damage.
     *
     * @param itemStack - The itemstack containing the material with the specified damage.
     * @return double - The level of price scaling to apply. For example .9 = 90% of full price. Maximum value is 1 for undamaged.
     */
    private double getDamageValue(ItemStack itemStack) {
        // Instantiate damage value
        double damageValue = 1.0;

        // Get meta and cast to damageable, for getting the items durability
        // Get durability and max durability
        Damageable dmg = (Damageable) itemStack.getItemMeta();
        double durability = dmg.getDamage();
        double maxDurability = itemStack.getType().getMaxDurability();

        // If max durability > 0 - Meaning the item is damageable (aka a tool)
        // Adjust damage value to be the percentage of health left on the item.
        // 50% damaged = .5 scaling (50% of full price)
        // Durability is in the form of 1 = 1 damage (if item has 10 health, 1 durability = 9 health)
        // Hence maxDura - dura / maxDura
        if (maxDurability > 0) {
            damageValue = (maxDurability - durability) / maxDurability;
        }
        return damageValue;
    }

    /**
     * Returns the combined sell value of all the items given
     *
     * @param itemStacks - The items to calculate the price for
     * @return MaterialValue - The value of the items, or not if an error occurred.
     */
    public ValueResponse getSellValue(ItemStack[] itemStacks) {
        double value = 0.0;

        // Loop through items and add up the sell value of each item
        for (ItemStack itemStack : itemStacks) {
            ValueResponse mv = this.getSellValue(itemStack);
            if (mv.isSuccess()) {
                value += mv.value;
            } else {
                return new ValueResponse(0.0, mv.responseType, mv.errorMessage);
            }
        }

        return new ValueResponse(value, ResponseType.SUCCESS, "");
    }

    /**
     * Returns the sell value for a single stack of items.
     *
     * @param itemStack - The itemStack to get the value of
     * @return MaterialValue - The price of the itemstack if no errors occurred.
     */
    public ValueResponse getSellValue(ItemStack itemStack) {
        double scale = 1.0;
        ValueResponse response;

        if (this.app.getEnchantmentManager().isEnchanted(itemStack)) {
            response = new ValueResponse(0.0, ResponseType.FAILURE, "item is enchanted.");

        } else {
            MaterialData materialData = this.getMaterial(itemStack.getType().name());

            if (materialData == null) {
                response = new ValueResponse(0.0, ResponseType.FAILURE, "item cannot be found.");
            } else {
                if (!materialData.getAllowed()) {
                    response = new ValueResponse(0.0, ResponseType.FAILURE, "item is banned.");
                } else {
                    response = new ValueResponse(this.calculatePrice(itemStack.getAmount(), materialData.getQuantity(), (scale * this.getDamageValue(itemStack)), false), ResponseType.SUCCESS, "");
                }
            }
        }

        return response;
    }

    /**
     * Returns the price of buying the given items.
     *
     * @param itemStacks - The items to get the price for
     * @return MaterialValue
     */
    public ValueResponse getBuyValue(ItemStack[] itemStacks) {
        double value = 0.0;
        for (ItemStack itemStack : itemStacks) {
            ValueResponse mv = this.getBuyValue(itemStack);
            if (mv.isSuccess()) {
                value += mv.value;
            } else {
                return new ValueResponse(0.0, mv.responseType, mv.errorMessage);
            }
        }

        return new ValueResponse(value, ResponseType.SUCCESS, "");
    }

    /**
     * Returns the value of an itemstack
     *
     * @param itemStack - The item stack to get the value of
     * @return MaterialValue
     */
    public ValueResponse getBuyValue(ItemStack itemStack) {
        ValueResponse response;

        MaterialData materialData = this.getMaterial(itemStack.getType().name());
        if (materialData == null) {
            response = new ValueResponse(0.0, ResponseType.FAILURE, "item cannot be found.");
        } else {
            if (!materialData.getAllowed()) {
                response = new ValueResponse(0.0, ResponseType.FAILURE, "item cannot be bought or sold.");
            } else {
                response = new ValueResponse(this.calculatePrice(itemStack.getAmount(), materialData.getQuantity(), materialBuyTax, true), ResponseType.SUCCESS, "");
            }
        }

        return response;
    }

    /**
     * Returns the market price based on stock
     *
     * @param stock - The stock of the material
     * @return double
     */
    public double getMarketPrice(double stock) {
        return this.getPrice(stock, this.materialSellTax, this.getInflation());
    }


    /**
     * Returns the user price based on stock
     *
     * @param stock - The stock of the material
     * @return double
     */
    public double getUserPrice(double stock) {
        return this.getPrice(stock, this.materialBuyTax, this.getInflation());
    }

    /**
     * Calculates the price of a material * amount
     * This is not the same as price * amount -- Factors in price change and inflation change during purchase
     *
     * @param amount   - The amount to calculate the price for
     * @param stock    - The stock of the material
     * @param scale    - The scaling to apply, such as tax
     * @param purchase - Whether this is a purchase from or sale to the market
     * @return double
     */
    public double calculatePrice(double amount, double stock, double scale, boolean purchase) {
        return Math.calculatePrice(this.materialBaseQuantity, stock, this.defaultTotalMaterials, this.totalMaterials, amount, scale, purchase);
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
        return Math.getPrice(this.materialBaseQuantity, stock, scale, inflation);
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
        return (int) ((int) (this.materialBaseQuantity / price) * scale * inflation);
    }

    /**
     * Gets the market-wide level of inflation
     *
     * @return double - The level of inflation
     */
    public double getInflation() {
        return Math.getInflation(this.defaultTotalMaterials, this.totalMaterials);
    }

    /**
     * Adds or removes the amount from the stock
     * Used to track inflation
     *
     * @param amount - The amount to add or remove. Negative to remove.
     */
    public void editTotalMaterials(int amount) {
        this.totalMaterials += amount;
    }

    public int getTotalMaterials() {
        return totalMaterials;
    }

    public int getDefaultTotalMaterials() {
        return defaultTotalMaterials;
    }

    /**
     * Loads aliases from the aliases file into the aliases variable
     */
    public void loadAliases() {
        FileConfiguration config = DCEPlugin.CONFIG.loadConfig(this.aliasesFile);
        HashMap<String, String> values = new HashMap<>();
        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            values.put(key, value);
        }
        this.aliases = values;
        DCEPlugin.CONSOLE.info("Loaded " + values.size() + " aliases from " + this.aliasesFile);
    }

    /**
     * Loads the materials from the materials file into the materials variable
     */
    public void loadMaterials() {
        // Load the config
        this.config = DCEPlugin.CONFIG.loadConfig(this.materialsFile);
        FileConfiguration defaultConf = DCEPlugin.CONFIG.readResource(this.materialsFile);
        // Set material counts
        this.defaultTotalMaterials = 0;
        this.totalMaterials = 0;
        // Create a HashMap to store the values
        HashMap<String, MaterialData> values = new HashMap<>();
        // Loop through keys and get data
        // Add data to a MaterialData and put in HashMap under key
        for (String key : this.config.getKeys(false)) {
            ConfigurationSection data = this.config.getConfigurationSection(key);
            ConfigurationSection defaultData = defaultConf.getConfigurationSection(key);
            MaterialData mData = new MaterialData(this, data, defaultData);
            this.defaultTotalMaterials += mData.getDefaultQuantity();
            this.totalMaterials += mData.getQuantity();
            values.put(key, mData);
        }
        // Copy values into materials
        this.materials = values;
        DCEPlugin.CONSOLE.info("Loaded " + values.size() + "(" + this.totalMaterials + "/" + this.defaultTotalMaterials + ") materials from " + this.materialsFile);
    }

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
     * Saves the material data to the config
     *
     * @param material - The material to save
     */
    public void saveMaterial(MaterialData material) {
        this.setData(material.getMaterialID(), material.getConfigData());
    }

    /**
     * Loops through the materials and saves their data to the config
     * Then saves the config to the config file
     */
    public void saveMaterials() {
        for (MaterialData materialD : materials.values()) {
            this.saveMaterial(materialD);
        }
        this.saveFile();
        DCEPlugin.CONSOLE.info("Materials saved.");
    }

    /**
     * Saves the config to the config file
     */
    public void saveFile() {
        DCEPlugin.CONFIG.saveFile(this.config, this.materialsFile);
    }
}
