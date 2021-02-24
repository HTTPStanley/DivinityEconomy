package EDGRRRR.DCE.Materials;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Math.Math;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class MaterialManager {
    // Link back to Main
    private final DCEPlugin app;
    private final BukkitRunnable saveTimer;
    // Stores the default items.json file location
    private final String materialsFile = "materials.yml";
    private final String aliasesFile = "aliases.yml";
    public HashMap<String, String> aliases;
    public HashMap<String, MaterialData> materials;
    public int totalMaterials;
    public int baseTotalMaterials;
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
        int timer = this.app.getConfig().getInt(this.app.getConfigManager().strMainSaveTimer);
        this.saveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                saveAll();
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
    public MaterialValue getSellValue(ItemStack[] itemStacks) {
        double value = 0.0;

        // Loop through items and add up the sell value of each item
        for (ItemStack itemStack : itemStacks) {
            MaterialValue mv = this.getSellValue(itemStack);
            if (mv.getResponseType() == ResponseType.SUCCESS) {
                value += mv.getValue();
            } else {
                return new MaterialValue(0.0, mv.getErrorMessage(), mv.getResponseType());
            }
        }

        return new MaterialValue(value, "", ResponseType.SUCCESS);
    }

    /**
     * Returns the sell value for a single stack of items.
     *
     * @param itemStack - The itemStack to get the value of
     * @return MaterialValue - The price of the itemstack if no errors occurred.
     */
    public MaterialValue getSellValue(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        double scale = 1.0;
        MaterialValue response;

        if (meta != null) {
            if (meta.getEnchants().size() > 0) {
                response = new MaterialValue(0.0, "Cannot sell enchanted items.", ResponseType.FAILURE);
            } else {
                MaterialData materialData = this.getMaterial(itemStack.getType().name());
                if (materialData == null) {
                    response = new MaterialValue(0.0, "Item cannot be found.", ResponseType.FAILURE);
                } else {
                    response = new MaterialValue(this.calculatePrice(itemStack.getAmount(), materialData.getQuantity(), (scale * this.getDamageValue(itemStack)), false), "", ResponseType.SUCCESS);
                }
            }
        } else {
            response = new MaterialValue(0.0, "Item is not supported.", ResponseType.FAILURE);
        }
        return response;
    }

    /**
     * Returns the price of buying the given items.
     *
     * @param itemStacks - The items to get the price for
     * @return MaterialValue
     */
    public MaterialValue getBuyValue(ItemStack[] itemStacks) {
        double value = 0.0;
        for (ItemStack itemStack : itemStacks) {
            MaterialValue mv = this.getBuyValue(itemStack);
            if (mv.getResponseType() == ResponseType.SUCCESS) {
                value += mv.getValue();
            } else {
                return new MaterialValue(0.0, mv.getErrorMessage(), mv.getResponseType());
            }
        }

        return new MaterialValue(value, "", ResponseType.SUCCESS);
    }

    /**
     * Returns the value of an itemstack
     *
     * @param itemStack - The item stack to get the value of
     * @return MaterialValue
     */
    public MaterialValue getBuyValue(ItemStack itemStack) {
        double scale = this.app.getEconomyManager().tax;
        MaterialValue response;

        MaterialData materialData = this.getMaterial(itemStack.getType().name());
        if (materialData == null) {
            response = new MaterialValue(0.0, "Item cannot be found.", ResponseType.FAILURE);
        } else {
            response = new MaterialValue(this.calculatePrice(itemStack.getAmount(), materialData.getQuantity(), scale, true), "", ResponseType.SUCCESS);
        }
        return response;
    }

    /**
     * Returns the market price based on stock
     *
     * @param stock - The stock of the material
     * @return double
     */
    public double getMarketPrice(int stock) {
        return this.calculatePrice(1, stock, 1.0, false);
    }


    /**
     * Returns the user price based on stock
     *
     * @param stock - The stock of the material
     * @return double
     */
    public double getUserPrice(int stock) {
        return this.calculatePrice(1, stock, this.app.getEconomyManager().tax, true);
    }

    /**
     * Calculates the price of a material
     *
     * @param amount   - The amount to calculate the price for
     * @param stock    - The stock of the material
     * @param scale    - The scaling to apply, such as tax
     * @param purchase - Whether this is a purchase from or sale to the market
     * @return double
     */
    private double calculatePrice(int amount, int stock, double scale, boolean purchase) {
        double value = 0;
        int materials = this.totalMaterials;

        // Loop for amount
        // Get the price and add it to the value
        // if purchase = true
        // remove 1 stock to simulate decrease
        // if purchase = false
        // add 1 stock to simulate increase
        for (int i = 1; i <= amount; i++) {
            value += this.calculatePrice(stock, scale, Math.getInflation(this.baseTotalMaterials, materials));
            if (purchase) {
                stock -= 1;
                materials -= 1;
            } else {
                stock += 1;
                materials += 1;
            }
        }

        return value;
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
    private double calculatePrice(int stock, double scale, double inflation) {
        // Price breakdown
        // Prices were balanced in data.csv
        // Prices are determined by quantity
        // Price = $1 @ 1000000 (1 million) items
        // Price = $2 @ 500000 (5 hundred-thousand) items
        // Price is then scaled - such as the addition of tax (20% by default)
        // Price is then scaled for inflation
        // Inflation works by calculating the default total items and dividing it by the new total items
        // This results in an increase in price when there are less items in the market than default
        // Or a decrease in price when there are more items in the market than default
        return (this.app.getEconomyManager().baseQuantity / (double) stock) * scale * inflation;
    }

    /**
     * Gets the market-wide level of inflation
     *
     * @return double - The level of inflation
     */
    public double getInflation() {
        return Math.getInflation(this.baseTotalMaterials, this.totalMaterials);
    }

    /**
     * Adds or removes the amount from the stock
     * Used to track inflation
     *
     * @param amount - The amount to add or remove. Negative to remove.
     */
    public void editItems(int amount) {
        this.totalMaterials += amount;
    }

    /**
     * Loads aliases from the aliases file into the aliases variable
     */
    public void loadAliases() {
        FileConfiguration config = this.app.getConfigManager().loadConfig(this.aliasesFile);
        HashMap<String, String> values = new HashMap<>();
        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            values.put(key, value);
        }
        this.aliases = values;
        this.app.getConsoleManager().info("Loaded " + values.size() + " aliases from " + this.aliasesFile);
    }

    /**
     * Loads the materials from the materials file into the materials variable
     */
    public void loadMaterials() {
        // Load the config
        this.config = this.app.getConfigManager().loadConfig(this.materialsFile);
        FileConfiguration defaultConf = this.app.getConfigManager().readResource(this.materialsFile);
        // Set material counts
        this.baseTotalMaterials = 0;
        this.totalMaterials = 0;
        // Create a HashMap to store the values
        HashMap<String, MaterialData> values = new HashMap<>();
        // Loop through keys and get data
        // Add data to a MaterialData and put in HashMap under key
        for (String key : this.config.getKeys(false)) {
            ConfigurationSection data = this.config.getConfigurationSection(key);
            ConfigurationSection defaultData = defaultConf.getConfigurationSection(key);
            MaterialData mData = new MaterialData(this, data, defaultData);
            this.baseTotalMaterials += mData.getDefaultQuantity();
            this.totalMaterials += mData.getQuantity();
            values.put(key, mData);
        }
        // Copy values into materials
        this.materials = values;
        this.app.getConsoleManager().info("Loaded " + values.size() + "(" + this.totalMaterials + "/" + this.baseTotalMaterials + ") materials from " + this.materialsFile);
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
    public void saveAll() {
        for (MaterialData materialD : materials.values()) {
            this.saveMaterial(materialD);
        }
        this.saveMaterials();
        this.app.getConsoleManager().info("Materials saved.");
    }

    /**
     * Saves the config to the config file
     */
    public void saveMaterials() {
        this.app.getConfigManager().saveFile(this.config, this.materialsFile);
    }
}
