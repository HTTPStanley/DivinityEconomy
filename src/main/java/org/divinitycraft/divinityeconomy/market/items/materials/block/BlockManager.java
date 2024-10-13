package org.divinitycraft.divinityeconomy.market.items.materials.block;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.ItemManager;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.market.items.materials.MaterialManager;
import org.divinitycraft.divinityeconomy.market.items.materials.MaterialValueResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BlockManager extends MaterialManager {
    // Stores the default items.json file location
    public static final String materialsFile = "materials.yml";
    public static final String aliasesFile = "materialAliases.yml";

    // Other settings
    private boolean itemDmgScaling = false;

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and materials with data from the program
     *
     * @param main - The plugin
     */
    public BlockManager(DEPlugin main) {
        super(main, materialsFile, aliasesFile, new ConcurrentHashMap<String, MarketableBlock>());
    }


    /**
     * Returns the scaling of price for an item, based on its durability and damage.
     *
     * @param itemStack - The itemstack containing the material with the specified damage.
     * @return double - The level of price scaling to apply. For example .9 = 90% of full price. Maximum value is 1 for undamaged.
     */
    private static double getDamageValue(ItemStack itemStack) {
        // Instantiate damage value
        double damageValue = 1.0;

        // Get meta and cast to damageable, for getting the items durability
        // Get durability and max durability
        Damageable dmg = (Damageable) itemStack.getItemMeta();
        if (dmg == null) return damageValue;
        double durability = dmg.getDamage();
        double maxDurability = itemStack.getType().getMaxDurability();

        // If max durability > 0 - Meaning the item is damageable (aka a tool)
        // Adjust damage value to be the percentage of health left on the item.
        // 50% damaged = .5 scaling (50% of full price)
        // Durability is in the form of 1 = 1 damage (if item has 10 health, 1 durability = 9 health)
        // Hence maxDurability - durability / maxDurability
        if (maxDurability > 0) {
            damageValue = (maxDurability - durability) / maxDurability;
        }

        return damageValue;
    }


    /**
     * Returns the MarketableBlock for the given alias
     * @param alias
     * @return
     */
    public MarketableBlock getMaterial(String alias) {
        return (MarketableBlock) this.getItem(alias);
    }

    /**
     * Called by init
     */
    @Override
    public void init() {
        super.init();
        this.itemDmgScaling = this.getConfMan().getBoolean(Setting.MARKET_MATERIALS_ITEM_DMG_SCALING_BOOLEAN);
    }


    @Override
    public Set<String> getLocalKeys() {
        return Arrays.stream(Material.values())
                .map(Material::getKey)
                .map(NamespacedKey::getKey)
                .map(Object::toString)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    }


    @Override
    public String getType() {
        return "MATERIAL";
    }

    /**
     * Returns the damage scaling of the item given
     *
     * @param itemStack
     * @return
     */
    private double getDamageScaling(ItemStack itemStack) {
        if (this.itemDmgScaling) {
            return BlockManager.getDamageValue(itemStack);
        } else {
            return 1.0;
        }
    }

    /**
     * Returns the sell value for a single stack of items.
     *
     * @param itemStack - The itemStack to get the value of
     * @return MaterialValue - The price of the itemstack if no errors occurred.
     */
    @Override
    public MaterialValueResponse getSellValue(ItemStack itemStack, int amount) {
        // Create the value response
        MaterialValueResponse response = new MaterialValueResponse(ResponseType.SUCCESS, null);


        // Check if item is enchanted and return failure if so
        if (ItemManager.removeEnchantedItems(new ItemStack[]{itemStack}).length == 0)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsEnchanted.get(getMain(), getMarkMan().getName(itemStack)));



        // Check if the item has a name and return failure if sot
        if ((ItemManager.itemIsNamed(itemStack) || ItemManager.itemHasLore(itemStack)) && this.ignoreNamedItems)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsNamedOrLored.get(getMain(), getMarkMan().getName(itemStack)));


        // Get the material data
        MarketableBlock materialData = (MarketableBlock) this.getItem(itemStack);


        // If material data is null, return failure
        if (materialData == null)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemCannotBeFound.get(getMain(), itemStack.getType().name()));


        // Get value and add to response
        double value = this.calculatePrice(amount, materialData.getQuantity(), (this.sellScale * this.getDamageScaling(itemStack)), false);
        response.addToken(materialData, amount, value, new ItemStack[]{itemStack});


        // Check if item is banned
        if (!materialData.getAllowed())
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsBanned.get(getMain(), materialData.getName()));


        // If value is equal to 0 or less, return failure
        if (value <= 0)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsWorthless.get(getMain(), materialData.getName()));


        // Check if item is enchanted and return failure if so
        if (this.getEnchMan().isEnchanted(itemStack))
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsEnchanted.get(getMain(), materialData.getName()));


        // Check if the item has a name and return failure if so
        if ((ItemManager.itemIsNamed(itemStack) || ItemManager.itemHasLore(itemStack)) && this.ignoreNamedItems)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsNamedOrLored.get(getMain(), materialData.getName()));


        // Return
        return response;
    }

    /**
     * Returns the value of an itemstack
     *
     * @param itemStack - The item stack to get the value of
     * @return MaterialValue
     */
    @Override
    public MaterialValueResponse getBuyValue(ItemStack itemStack, int amount) {
        // Create value response
        MaterialValueResponse response = new MaterialValueResponse(ResponseType.SUCCESS, null);


        // Get the material data
        MarketableBlock materialData = (MarketableBlock) this.getItem(itemStack);


        // If material data is null, return failure
        if (materialData == null)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemCannotBeFound.get(getMain(), itemStack.getType().name()));


        // Get value
        double value = this.calculatePrice(amount, materialData.getQuantity(), (this.buyScale * this.getDamageScaling(itemStack)), false);
        response.addToken(materialData, amount, value, new ItemStack[]{itemStack});


        // If material is banned, return failure
        if (!materialData.getAllowed())
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsBanned.get(getMain(), materialData.getName()));


        // If material is worthless, return failure
        if (value <= 0)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsWorthless.get(getMain(), materialData.getName()));


        // Return
        return response;
    }

    @Override
    public MarketableMaterial loadItem(String ID, ConfigurationSection data, ConfigurationSection defaultData) {
        return new MarketableBlock(getMain(), this, ID, data, defaultData);
    }
}
