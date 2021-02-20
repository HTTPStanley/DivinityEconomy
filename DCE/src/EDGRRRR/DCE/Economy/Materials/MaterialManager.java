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
import org.bukkit.scheduler.BukkitRunnable;

import EDGRRRR.DCE.Main.DCEPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class MaterialManager {
    // Link back to Main
    private final DCEPlugin app;

    // Stores items
    private FileConfiguration config;
    public HashMap<String, String> aliases;
    public HashMap<String, MaterialData> materials;
    public int totalMaterials;
    public int baseTotalMaterials;

    // Stores the default items.json file location
    private final String materialsFile = "materials.yml";
    private final String aliasesFile = "aliases.yml";

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and materials with data from the program
     * @param app - The plugin
     */
    public MaterialManager(DCEPlugin app) {
        this.app = app;
        int timer = this.app.getConfig().getInt(this.app.getConf().strMainSaveTimer);
        new BukkitRunnable() {
            public void run() {
                saveAll();
            }
          }.runTaskTimer(this.app, timer, timer);
    }

    /**
     * Returns a material from the materialData HashMap Will be none if no alias or
     * direct name is found.
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
     * Removes the specified number of materials from the players inventory
     * @param player - The player to remove from
     * @param material - The material to remove
     * @param amount - The amount to remove
     */
    public void removeMaterialsFromPlayer(Player player, Material material, int amount) {
        ItemStack[] itemStacks = this.getMaterialSlots(player, material);
        this.removeMaterialsFromPlayer(itemStacks, amount);
    }

    /**
     * Removes the specified number of materials from the players inventory
     * The players inventory is parsed via itemStacks
     * @param itemStacks - The players inventory
     * @param amount - The amount to remove
     */
    public void removeMaterialsFromPlayer(ItemStack[] itemStacks, int amount) {
        int amountLeft = amount;
        for (ItemStack itemStack : itemStacks) {
            int stackAmount = itemStack.getAmount();
            int amountRemoved;
            if (amountLeft > stackAmount) {
                amountRemoved = stackAmount;
                itemStack.setAmount(0);
            } else {
                amountRemoved = stackAmount - amountLeft;
                itemStack.setAmount(amountRemoved);
            }

            amountLeft -= amountRemoved;
            if (amountLeft == 0) {
                break;
            }
        }
    }

    public void addMaterialToPlayer(Player player, Material material, int amount) {
        for (int i=0; i < amount;) {
            ItemStack newStack = new ItemStack(material);
            int amountLeft = amount - i;
            if (amountLeft > material.getMaxStackSize()) {
                newStack.setAmount(material.getMaxStackSize());
                i += material.getMaxStackSize();
            } else {
                newStack.setAmount(amountLeft);
                i += amountLeft;
            }
            player.getInventory().addItem(newStack);
        }
    }

    /**
     * Returns the number of empty slots in a players inventory.
     * @param player - The player to check
     * @return int - The number of empty slots
     */
    public int getEmptySlots(Player player) {
        int count = 0;
        ItemStack[] inventory = player.getInventory().getContents();
        for (ItemStack iStack : inventory) {
            if (iStack == null) {
                count += 1;
            }
        }

        return count;
    }

    /**
     * Returns the ItemStacks of a specific material in a player
     * @param player - The player to check
     * @param material - The material to check
     * @return ItemStack[] - An array of the ItemStack's in the player of material
     */
    public ItemStack[] getMaterialSlots(Player player, Material material) {
        HashMap<Integer, ? extends ItemStack> inventory = player.getInventory().all(material);
        ItemStack[] iStacks = new ItemStack[inventory.size()];
        int idx = 0;
        for (ItemStack iStack : inventory.values()) {
            iStacks[idx] = iStack;
            idx += 1;
        }

        return iStacks;
    }

    /**
     * Returns the total count of a material in a player
     * @param player - The player to check
     * @param material - The material to check
     * @return int - Total count of materials
     */
    public int getMaterialCount(Player player, Material material) {
        return this.getMaterialCount(this.getMaterialSlots(player, material));
    }

    /**
     * Calculates the total count of all materials in the ItemStacks
     * @param iStacks - The array of item stacks
     * @return int - Total count of materials
     */
    public int getMaterialCount(ItemStack[] iStacks) {
        int count = 0;
        for (ItemStack iStack : iStacks) {
            count += iStack.getAmount();
        }

        return count;
    }

    /**
     * Calculates the total available space for a material in the players inventory
     * @param player - The player to check
     * @param material - The material to check
     * @return int - The total space that can be further occupied by a material
     */
    public int getAvailableSpace(Player player, Material material) {
        //Get empty slots
        //Get total slots used by material
        //Get total count of materials in those slots
        int emptySlots = this.getEmptySlots(player);
        ItemStack[] iStacks = this.getMaterialSlots(player, material);
        int materialCount = this.getMaterialCount(iStacks);

        // Instantiate space
        // Add the total space occupied by the number of slots filled less the actual space filled
        // Add the empty slot space
        int availableSpace = 0;
        availableSpace += (iStacks.length * material.getMaxStackSize()) - materialCount;
        availableSpace += emptySlots * material.getMaxStackSize();

        return availableSpace;
    }

    /**
     * Adds <amount> of <material> to <player>
     * @param player - The player to add the material to
     * @param material - The material to add
     * @param amount - The amount to add
     * @return EconomyResponse - ADD DESCRIPTION
     */
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
     * @return EconomyResponse - ADD DESCRIPTION
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
            value += this.getPrice(stock, scale, this.getInflation(this.baseTotalMaterials, materials));
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
     * @param stock - The stock of the material
     * @param scale - The scaling to apply to the price
     * @param inflation - The level of inflation
     * @return double - The price of the material
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
        return (this.app.getEco().baseQuantity / (double) stock) * scale * inflation;
    }

    /**
     * Gets the market-wide level of inflation
     * @return double - The level of inflation
     */
    public double getInflation() {
        return this.getInflation(this.baseTotalMaterials, this.totalMaterials);
    }

    /**
     * Gets the level of inflation based on the parameters supplied
     * @param baseQuantity - The base quantity of materials in the market
     * @param actualQuantity - The actual current quantity of materials in the market
     * @return double - The level of inflation
     */
    public double getInflation(int baseQuantity, int actualQuantity) {
        return (double) baseQuantity / (double) actualQuantity;
    }

    /**
     * Returns the market price of a product Market price = exact price (scale of
     * 1.0)
     *
     * @param stock - The stock of the product
     * @return double - The market (sell) price of the product
     */
    public double getMarketPrice(int stock) {
        return this.getPrice(stock, 1.0, this.getInflation());
    }

    /**
     * Returns the user price of a product User price = price * tax (scale of 1.2
     * default)
     *
     * @param stock - The stock of the product
     * @return double - The user (buy) price of the product
     */
    public double getUserPrice(int stock) {
        return this.getPrice(stock, this.app.getEco().tax, this.getInflation());
    }

    /**
     * Loads aliases from the aliases file into the aliases variable
     */
    public void loadAliases() {
        FileConfiguration config = this.loadFile(this.aliasesFile);
        HashMap<String, String> values = new HashMap<>();
        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            values.put(key, value);
        }
        this.aliases = values;
        this.app.getCon().info("Loaded " + values.size() + " aliases from " + this.aliasesFile);
    }

    /**
     * Loads the materials from the materials file into the materials variable
     */
    public void loadMaterials() {
        // Load the config
        this.config = this.loadFile(this.materialsFile);
        FileConfiguration defaultConf = this.loadDefaultConfig(this.materialsFile);
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
        this.app.getCon().info("Loaded " + values.size() + "(" + this.totalMaterials + "/" + this.baseTotalMaterials + ") materials from " + this.materialsFile);
    }

    /**
     * Reads and loads the default config
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
    private FileConfiguration loadDefaultConfig(String file){return YamlConfiguration.loadConfiguration(new InputStreamReader(this.app.getResource(file)));}

    /**
     * Reads and loads the config
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
    private FileConfiguration loadConfig(String file) {return YamlConfiguration.loadConfiguration(new File(this.app.getDataFolder(), file));}

    /**
     * Loads the default and current config files If the config file is empty or
     * non-existent, it will be overwritten with the default config And returned
     * @param file - The file to load
     * @return FileConfiguration - The file config
     */
    public FileConfiguration loadFile(String file) {
        // Instantiate default and user config
        FileConfiguration defConfig;
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
                config.save(new File(this.app.getDataFolder(), file));
            }
        } catch (Exception e) {
            // I don't know why this would happen but ¯\_(ツ)_/¯
            this.app.getCon().severe("Couldn't handle " + file + " :" + e.getMessage());
            this.app.getServer().getPluginManager().disablePlugin(this.app);
        }

        return config;
    }

    /**
     * Saves a value to a key
     * @param key - The map key
     * @param value - The map value
     */
    private void setData(String key, Object value) {
        this.config.set(key, value);
    }

    /**
     * Saves the material data to the config
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
        this.app.getCon().info("Saving materials.");
        for (MaterialData materialD : materials.values()) {
            this.saveMaterial(materialD);
        }
        this.saveMaterials();
        this.app.getCon().info("Materials saved.");
    }

    /**
     * Saves the config to the config file
     */
    public void saveMaterials() {
        try {
            this.config.save(new File(this.app.getDataFolder(), this.materialsFile));
        }
        catch (Exception e) {
            this.app.getCon().severe("Couldn't handle " + this.materialsFile + " :" + e.getMessage());
        }
    }

    public void editItems(int amount) {
        this.totalMaterials += amount;
    }
}
