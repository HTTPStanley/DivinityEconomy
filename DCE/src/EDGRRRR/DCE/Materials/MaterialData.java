package EDGRRRR.DCE.Materials;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * A class for storing material data
 */
public class MaterialData {
    // The material manager
    private final MaterialManager manager;
    // The configuration section for this material
    private final ConfigurationSection configData;
    // The configuration section for this material
    private final ConfigurationSection defaultConfigData;
    // The potionData for this material
    private final MaterialPotionData potionData;

    // String key names for values
    private final String strAllowed = "ALLOWED";
    private final String strQuantity = "QUANTITY";
    private final String strMaterialID = "MATERIAL";
    private final String strPotionData = "POTION_DATA";
    private final String strCleanName = "CLEAN_NAME";
    private final String strEntity = "ENTITY";
    private final String strTypeMaterial = "MATERIAL";
    private final String strTypeEntity = "ENTITY";
    private final String strTypePotion = "POTION";
    private final String strPotionUpgraded = "upgraded";
    private final String strPotionExtended = "extended";
    private final String strPotionType = "type";


    /**
     * Constructor
     *
     * @param manager    - The material manager
     * @param configData - The config section containing the data for this material
     */
    public MaterialData(MaterialManager manager, ConfigurationSection configData, ConfigurationSection defaultConfigData) {
        this.manager = manager;
        this.configData = configData;
        this.defaultConfigData = defaultConfigData;
        ConfigurationSection pData = configData.getConfigurationSection(this.strPotionData);
        if (pData == null) {
            this.potionData = null;
        } else {
            this.potionData = new MaterialPotionData(pData);
        }
    }

    /**
     * Returns the config section
     *
     * @return ConfigurationSection - Returns the config data section for this material
     */
    public ConfigurationSection getConfigData() {
        return this.configData;
    }

    /**
     * Returns the clean name for the material.
     * Ideal for messaging or returning to the user.
     *
     * @return String - Returns the clean name for this item
     */
    public String getCleanName() {
        return this.configData.getString(this.strCleanName);
    }

    /**
     * Returns the quantity of this material in the market
     *
     * @return int - The quantity of this item in stock
     */
    public int getQuantity() {
        return this.configData.getInt(this.strQuantity);
    }

    /**
     * Sets the quantity to <amount>
     *
     * @param amount - The amount to set the internal stock of this item to
     */
    public void setQuantity(int amount) {
        int oldQuantity = this.getQuantity();
        this.setData(this.strQuantity, amount);
        int change = oldQuantity - amount;
        this.manager.editItems(change);
    }

    /**
     * Sets the stock level so that the price of the material is that given.
     * @param price - The new price for this material.
     */
    public void setPrice(double price) {
        this.setQuantity(this.manager.calculateStock(price, 1.0, this.manager.getInflation()));
    }

    /**
     * Returns the default quantity of this material from the config
     *
     * @return int - The default quantity of this item in stock
     */
    public int getDefaultQuantity() {
        return this.defaultConfigData.getInt(this.strQuantity);
    }

    /**
     * Returns the market price of this item
     * Market price = price for server (user sell price)
     *
     * @return double - The market (sell) price of this item
     */
    public double getMarketPrice() {
        return this.manager.getMarketPrice(this.getQuantity());
    }

    /**
     * Returns the user price of this item
     * User price = price for user (user buy price)
     *
     * @return double - The user (buy) price of this item
     */
    public double getUserPrice() {
        return this.manager.getUserPrice(this.getQuantity());
    }

    /**
     * Returns the banned state of the material
     * True means the item is allowed
     * False means the item is banned
     *
     * @return boolean - Whether the item is allowed to be bought/sold or not
     */
    public boolean getAllowed() {
        return this.configData.getBoolean(this.strAllowed);
    }

    /**
     * Returns the material ID
     * Examples: "AIR", "OAK_WOOD_PLANKS"
     *
     * @return String - The internal material name
     */
    public String getMaterialID() {
        return this.configData.getString(this.strMaterialID);
    }

    /**
     * Returns the potiondata for the material
     * Is null if the material is not a potion
     *
     * @return MaterialPotionData - The potion data for this material, if not potion may be null.
     */
    public MaterialPotionData getPotionData() {
        return this.potionData;
    }

    /**
     * Returns the internal Material from the materialID
     *
     * @return Material - Returns the material object for this material.
     */
    public Material getMaterial() {
        return Material.getMaterial(this.getMaterialID());
    }

    /**
     * Returns the internal Entity name
     *
     * @return String - The entity name of this material if an entity.
     */
    public String getEntityName() {
        return this.configData.getString(this.strEntity);
    }

    /**
     * Returns the type of material
     * Examples:
     * MATERIAL = block/item
     * POTION = a potion that requires potionData
     * ENTITY = an entity based item
     *
     * @return String - The type of object this is.
     */
    public String getType() {
        String type;
        // If potion
        if (!(potionData == null)) {
            type = this.strTypePotion;
        }
        // If entity
        else if (!(this.getEntityName() == null)) {
            type = this.strTypeEntity;
        }
        // Else is material
        else {
            type = this.strTypeMaterial;
        }
        return type;
    }

    /**
     * Returns an itemStack containing <amount> of this material
     *
     * @param amount - The amount to set the stack to
     * @return ItemStack - The item stack
     */
    public ItemStack getItemStack(int amount) {
        // Create the itemStack of <material> of <amount>
        ItemStack iStack = new ItemStack(this.getMaterial(), amount);
        // If potion, set meta data
        if (this.getType().equals(this.strTypePotion)) {
            PotionMeta meta = (PotionMeta) iStack.getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.valueOf(potionData.getType()), potionData.getExtended(), potionData.getUpgraded()));
            iStack.setItemMeta(meta);
        }
        return iStack;
    }

    /**
     * Adds the quantity <amount>
     *
     * @param amount - The amount of stock to add to the pile
     */
    public void addQuantity(int amount) {
        this.setData(this.strQuantity, this.getQuantity() + amount);
        this.manager.editItems(amount);
    }

    /**
     * Removes the quantity <amount>
     *
     * @param amount - The amount of stock to remove from the pile
     */
    public void remQuantity(int amount) {
        this.setData(this.strQuantity, this.getQuantity() - amount);
        this.manager.editItems(amount);
    }

    /**
     * Sets a data key to value
     *
     * @param key   - The key
     * @param value - The value
     */
    private void setData(String key, Object value) {
        this.configData.set(key, value);
    }
}
