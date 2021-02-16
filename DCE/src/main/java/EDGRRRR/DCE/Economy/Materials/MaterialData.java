package EDGRRRR.DCE.Economy.Materials;

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
    private MaterialManager manager;
    // The configuration section for this material
    private ConfigurationSection configData;
    // The configuration section for this material
    private ConfigurationSection defaultConfigData;
    // The potionData for this material
    private MaterialPotionData potionData;

    // String key names for values
    private String strAllowed = "ALLOWED";
    private String strQuantity = "QUANTITY";
    private String strMaterialID = "MATERIAL";
    private String strPotionData = "POTION_DATA";
    private String strCleanName = "CLEAN_NAME";
    private String strEntity = "ENTITY";
    private String strTypeMaterial = "MATERIAL";
    private String strTypeEntity = "ENTITY";
    private String strTypePotion = "POTION";
    private String strPotionUpgraded = "upgraded";
    private String strPotionExtended = "extended";
    private String strPotionType = "type";


    /**
     * Constructor
     * @param manager
     * @param configData
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
     * @return
     */
    public ConfigurationSection getConfigData() {
        return this.configData;
    }

    /**
     * Returns the clean name for the material.
     * Ideal for messaging or returning to the user.
     * @return
     */
    public String getCleanName() {
        return this.configData.getString(this.strCleanName);
    }

    /**
     * Returns the quantity of this material in the market
     * @return
     */
    public int getQuantity() {
        return this.configData.getInt(this.strQuantity);
    }

    public int getDefaultQuantity() {
        return this.defaultConfigData.getInt(this.strQuantity);
    }

    /**
     * Returns the market price of this item
     * Market price = price for server (user sell price)
     * @return
     */
    public double getMarketPrice() {
        return this.manager.getMarketPrice(this.getQuantity());
    }

    /**
     * Returns the user price of this item
     * User price = price for user (user buy price)
     * @return
     */
    public double getUserPrice() {
        return this.manager.getUserPrice(this.getQuantity());
    }

    /**
     * Returns the banned state of the material
     * True means the item is allowed
     * False means the item is banned
     * @return
     */
    public boolean getAllowed() {
        return this.configData.getBoolean(this.strAllowed);
    }

    /**
     * Returns the material ID
     * Examples: "AIR", "OAK_WOOD_PLANKS"
     * @return
     */
    public String getMaterialID() {
        return this.configData.getString(this.strMaterialID);
    }

    /**
     * Returns the potiondata for the material
     * Is null if the material is not a potion
     * @return
     */
    public MaterialPotionData getPotionData() {
        return this.potionData;
    }

    /**
     * Returns the internal Material from the materialID
     * @return
     */
    public Material getMaterial() {
        return Material.getMaterial(this.getMaterialID());
    }

    /**
     * Returns the internal Entity name
     * @return
     */
    public String getEntityName() {
        return this.configData.getString(this.strEntity);
    }

    /**
     * Returns the type of material
     * Examples:
     *  MATERIAL = block/item
     *  POTION = a potion that requires potionData
     *  ENTITY = an entity based item
     * @return
     */
    public String getType() {
        String type = null;
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
     * @param amount
     * @return
     */
    public ItemStack getItemStack(int amount) {
        // Create the itemStack of <material> of <amount>
        ItemStack iStack = new ItemStack(this.getMaterial(), amount);
        // If potion, set meta data
        if (this.getType() == this.strTypePotion) {
            PotionMeta meta = (PotionMeta) iStack.getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.valueOf(potionData.getType()), potionData.getExtended(), potionData.getUpgraded()));
            iStack.setItemMeta(meta);
        }
        return iStack;
    }

    /**
     * Sets the quantity to <amount>
     * @param amount
     */
    public void setQuantity(int amount) {
        int oldQuantity = this.getQuantity();
        setData(this.strQuantity, amount);
        int change = oldQuantity - amount;
        this.manager.editItems(change);
    }

    /**
     * Adds the quantity <amount>
     * @param amount
     */
    public void addQuantity(int amount) {
        setData(this.strQuantity, this.getQuantity() + amount);
        this.manager.editItems(amount);
    }

    /**
     * Removes the quantity <amount>
     * @param amount
     */
    public void remQuantity(int amount) {
        setData(this.strQuantity, this.getQuantity() - amount);
        this.manager.editItems(amount);
    }

    /**
     * Sets a data key to value
     * @param key
     * @param value
     */
    private void setData(String key, Object value) {
        this.configData.set(key, value);
    }
}