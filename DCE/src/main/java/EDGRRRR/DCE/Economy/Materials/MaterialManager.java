package EDGRRRR.DCE.Economy.Materials;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import EDGRRRR.DCE.Main.DCEPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class MaterialManager {
    // Link back to Main
    private DCEPlugin app;

    // Stores items
    private FileConfiguration config;
    public HashMap<String, String> aliases;
    public HashMap<String, MaterialData> materials;
    public int totalMaterials;
    public int baseTotalMaterials;

    // Stores the default items.json file location
    private String materialsFile = "materials.yml";
    private String aliasesFile = "aliases.yml";

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and materials with data from the program.
     *
     * @param app
     */
    public MaterialManager(DCEPlugin app) {
        this.app = app;
        int timer = app.getConfig().getInt(app.getConf().strMainSaveTimer);
        new BukkitRunnable() {
            public void run() {
                saveAll();
            }
          }.runTaskTimer(this.app, timer, timer);
    }

    /**
     * Returns a material from the materialData HashMap Will be none if no alias or
     * direct name is found.
     * @param alias
     * @return
     */
    public MaterialData getMaterial(String alias) {
        // Check aliases
        // If alias is empty then get directly from materials list
        // get the material and return it, could be Null
        String matID = aliases.get(alias);
        if (matID == null)
            matID = alias;
        MaterialData material = materials.get(matID);
        return material;
    }

    public EconomyResponse addMaterial(Player player, Material material, int amount) {
        player.getInventory().addItem(new ItemStack(material, amount));
        return new EconomyResponse(1.0, 1.0, ResponseType.SUCCESS, "");
    }

    /**
     * Gets the price of a material
     * @param materialD - The materialData
     * @param amount    - The amount of the material to buy
     * @param scale     - The scaling to apply to the final price. For example 1.2 =
     *                  20% ontop for tax reasons
     * @param purchase  - Whether this is a user purchase or sale.
     * @return
     */
    public EconomyResponse getMaterialPrice(MaterialData materialD, int amount, double scale, boolean purchase) {
        double value = 0;

        // If name is unknown return such
        if (materialD == null)
            return new EconomyResponse(amount, 0.0, ResponseType.FAILURE, "Unknown item: ");

        // Get the stock
        // If amount is greater than stock then return such
        int stock = materialD.getQuantity();
        int materials = totalMaterials;
        if (stock < amount)
            return new EconomyResponse(amount, 0.0, ResponseType.FAILURE, "Not enough stock: " + stock);

        // Loop for amount
        // Get the price and add it to the value
        // if purchase = true
        // remove 1 stock to simulate decrease
        // if purchase = false
        // add 1 stock to simulate increase
        for (int i = 1; i <= amount; i++) {
            value += getPrice(stock, scale, getInflation(baseTotalMaterials, materials));
            if (purchase) {
                stock -= 1;
                materials -= 1;
            }
            else {
                stock += 1;
                materials += 1;
            }
        }

        return new EconomyResponse(amount, value, ResponseType.SUCCESS, "");
    }

    /**
     * Returns the price for an item based on it's stock and the scale to apply
     * Scale of 1.2 = 20% additive Scale of .8 = 20% reduction
     *
     * @param stock
     * @param scale
     * @return
     */
    private double getPrice(int stock, double scale, double inflation) {
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
        return (app.getEco().baseQuantity / stock) * scale * inflation;
    }

    public double getInflation() {
        return getInflation(baseTotalMaterials, totalMaterials);
    }

    public double getInflation(int baseQuantity, int actualQuantity) {
        return baseQuantity / actualQuantity;
    }

    /**
     * Returns the market price of a product Market price = exact price (scale of
     * 1.0)
     *
     * @param stock
     * @return
     */
    public double getMarketPrice(int stock) {
        return getPrice(stock, 1.0, getInflation());
    }

    /**
     * Returns the user price of a product User price = price * tax (scale of 1.2
     * default)
     *
     * @param stock
     * @return
     */
    public double getUserPrice(int stock) {
        return getPrice(stock, app.getEco().tax, getInflation());
    }

    /**
     * Loads aliases from the aliases file into the aliases variable
     */
    public void loadAliases() {
        FileConfiguration config = loadFile(aliasesFile);
        HashMap<String, String> values = new HashMap<String, String>();
        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            values.put(key, value);
        }
        this.aliases = values;
        app.getCon().info("Loaded " + values.size() + " aliases from " + aliasesFile);
    }

    /**
     * Loads the materials from the materials file into the materials variable
     */
    public void loadMaterials() {
        // Load the config
        this.config = loadFile(materialsFile);
        FileConfiguration defaultConf = loadDefaultConfig(materialsFile);
        // Set material counts
        baseTotalMaterials = 0;
        totalMaterials = 0;
        // Create a HashMap to store the values
        HashMap<String, MaterialData> values = new HashMap<String, MaterialData>();
        // Loop through keys and get data
        // Add data to a MaterialData and put in HashMap under key
        for (String key : this.config.getKeys(false)) {
            ConfigurationSection data = this.config.getConfigurationSection(key);
            ConfigurationSection defaultData = defaultConf.getConfigurationSection(key);
            MaterialData mData = new MaterialData(this, data, defaultData);
            baseTotalMaterials += mData.getDefaultQuantity();
            totalMaterials += mData.getQuantity();
            values.put(key, mData);
        }
        // Copy values into materials
        this.materials = values;
        app.getCon().info("Loaded " + values.size() + "(" + totalMaterials + "/" + baseTotalMaterials + ") materials from " + materialsFile);
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    private FileConfiguration loadDefaultConfig(String file) {return YamlConfiguration.loadConfiguration(new InputStreamReader(app.getResource(file)));}

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    private FileConfiguration loadConfig(String file) {return YamlConfiguration.loadConfiguration(new File(app.getDataFolder(), file));}

    /**
     * Loads the default and current config files If the config file is empty or
     * non-existent, it will be overwritten with the default config And returned
     *
     * @param file
     * @return
     */
    public FileConfiguration loadFile(String file) {
        // Instantiate default and user config
        FileConfiguration defConfig = null;
        FileConfiguration config = null;
        try {
            // Load default and user config
            defConfig = loadDefaultConfig(file);
            config = loadConfig(file);

            // If config is empty, overwrite with defaults
            // Empty can either mean non-existent or empty file.
            if (config.getValues(false).size() == 0) {
                config.setDefaults(defConfig);
                config.options().copyDefaults(true);
                config.save(new File(app.getDataFolder(), file));
            }
        } catch (Exception e) {
            // I don't know why this would happen but ¯\_(ツ)_/¯
            app.getCon().severe("Couldn't handle " + file + " :" + e.getMessage());
            app.getServer().getPluginManager().disablePlugin(app);
        }

        return config;
    }

    /**
     * Saves a value to a key
     * @param key
     * @param value
     */
    private void setData(String key, Object value) {
        this.config.set(key, value);
    }

    /**
     * Saves the material data to the config
     * @param material
     */
    public void saveMaterial(MaterialData material) {
        setData(material.getMaterialID(), material.getConfigData());
    }

    /**
     * Loops through the materials and saves their data to the config
     * Then saves the config to the config file
     */
    public void saveAll() {
        app.getCon().info("Saving materials.");
        for (MaterialData materialD : materials.values()) {
            saveMaterial(materialD);
        }
        saveMaterials();
        app.getCon().info("Materials saved.");
    }

    /**
     * Saves the config to the config file
     */
    public void saveMaterials() {
        try {
            this.config.save(new File(app.getDataFolder(), materialsFile));
        }
        catch (Exception e) {
            app.getCon().severe("Couldn't handle " + materialsFile + " :" + e.getMessage());
        }
    }

    public void editItems(int amount) {
        totalMaterials += amount;
    }
}