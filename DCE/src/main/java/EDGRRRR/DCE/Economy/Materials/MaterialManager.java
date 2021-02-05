package EDGRRRR.DCE.Economy.Materials;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import EDGRRRR.DCE.Main.DCEPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class MaterialManager {
    // Link back to Main
    private DCEPlugin app;

    // Stores items
    private HashMap<String, String> aliases;
    private HashMap<String, MaterialData> materials;

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
    }

    /**
     * Returns a material from the materialData HashMap
     * Will be none if no alias or direct name is found.
     * @param alias
     * @return
     */
    public MaterialData getMaterial(String alias) {
        // Check aliases
        // If alias is empty then get directly from materials list
        // get the material and return it, could be Null
        String matID = aliases.get(alias);
        if (matID == null) matID = alias;
        MaterialData material = materials.get(matID);
        return material;
    }

    /**
     * Loads the materials from the materials file into the materials variable
     */
    public void loadMaterials() {
        // Load the config
        FileConfiguration config = loadFile(materialsFile);
        // Create a HashMap to store the values
        HashMap<String, MaterialData> values = new HashMap<String, MaterialData>();
        // Loop through keys and get data
        // Add data to a MaterialData and put in HashMap under key
        for (String key : config.getKeys(false)) {
            ConfigurationSection data = config.getConfigurationSection(key);
            boolean isBanned = !(data.getBoolean("ALLOWED"));
            int quantity = data.getInt("QUANTITY");
            String entityName = data.getString("entity");
            String materialName = data.getString("material");
            ConfigurationSection potionData = data.getConfigurationSection("potionData");

            MaterialData mData = new MaterialData(isBanned, quantity, materialName, potionData, entityName);
            values.put(key, mData);
        }
        // Copy values into materials
        this.materials = values;
        app.getCon().info("Loaded " + values.size() + " materials from " + materialsFile);
    }

    public EconomyResponse addMaterial(Player player, Material material, int amount) {
        player.getInventory().addItem(new ItemStack(material, amount));
        return new EconomyResponse(1.0, 1.0, ResponseType.SUCCESS, "");
    }

    /**
     * Gets the price of a material
     * @param materialD - The materialData
     * @param amount - The amount of the material to buy
     * @param scale - The scaling to apply to the final price. For example 1.2 = 20% ontop for tax reasons
     * @param purchase - Whether this is a user purchase or sale.
     * @return 
     */
    public EconomyResponse getMaterialPrice(MaterialData materialD, int amount, double scale, boolean purchase) {
        double value = 0;

        // If name is unknown return such
        if (materialD == null) return new EconomyResponse(amount, 0.0, ResponseType.FAILURE, "Unknown item: ");

        // Get the stock
        // If amount is greater than stock then return such
        int stock = materialD.getQuantity();
        if (stock < amount) return new EconomyResponse(amount, 0.0, ResponseType.FAILURE, "Not enough stock: " + stock);

        // Loop for amount
        // Get the price and add it to the value
        // if purchase = true
        //  remove 1 stock to simulate decrease
        // if purchase = false
        //  add 1 stock to simulate increase
        for (int i=1; i<=amount; i++) {
            value += getPrice(stock, scale);
            if (purchase) stock -= 1;
            else stock += 1;            
        }

        // Rounding
        value = app.getEco().round(value);

        return new EconomyResponse(amount, value, ResponseType.SUCCESS, "");
    }

    private double getPrice(int stock, double scale) {
        return app.getEco().round((app.getEco().baseQuantity / stock) * scale);
    }

    private double getMarketPrice(int stock) {
        return getPrice(stock, 1.0);
    }

    private double getUserPrice(int stock) {
        return getPrice(stock, app.getEco().tax);
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
     * Loads the default and current config files
     * If the config file is empty or non-existent, it will be overwritten with the default config
     * And returned
     * @param file
     * @return
     */
    public FileConfiguration loadFile(String file) {
        // Instantiate default and user config
        FileConfiguration defConfig = null;
        FileConfiguration config = null;
        try {
            // Load default and user config
            defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(app.getResource(file)));
            config = YamlConfiguration.loadConfiguration(new File(app.getDataFolder(), file));

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

}
