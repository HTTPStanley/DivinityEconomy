package me.edgrrrr.de.market.items.materials.entity;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.items.materials.MaterialValueResponse;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

public class EntityManager extends MaterialManager {
    // Stores the default items.json file location
    private static final String entitiesFile = "entities.yml";
    private static final String aliasesFile = "entityAliases.yml";

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and items with data from the program
     *
     * @param main - The plugin
     */
    public EntityManager(DEPlugin main) {
        super(main, entitiesFile, aliasesFile, new ConcurrentHashMap<String, MarketableEntity>());
    }

    @Override
    public String getType() {
        return "ENTITY";
    }

    /**
     * Returns the sell value for a single stack of items.
     *
     * @param itemStack - The itemStack to get the value of
     * @return MaterialValue - The price of the itemstack if no errors occurred.
     */
    @Override
    public MaterialValueResponse getSellValue(ItemStack itemStack, int amount) {
        // Create value response
        MaterialValueResponse response = new MaterialValueResponse(EconomyResponse.ResponseType.SUCCESS, null);


        // Get the item data
        MarketableEntity entityData = (MarketableEntity) this.getItem(itemStack);


        // If the item data is null, return 0
        if (entityData == null)
            return (MaterialValueResponse) response.setFailure(String.format("%s cannot be found.", itemStack.getType().name()));


        // Get value and add token to response
        double value = this.calculatePrice(amount, entityData.getQuantity(), this.sellScale, false);
        response.addToken(entityData, amount, value, new ItemStack[]{itemStack});


        // Check item is allowed
        if (!entityData.getAllowed())
            return (MaterialValueResponse) response.setFailure(String.format("%s is banned.", entityData.getCleanName()));


        // If value is less than 0, return 0
        if (value <= 0)
            return (MaterialValueResponse) response.setFailure(String.format("%s is worthless.", entityData.getCleanName()));


        // Return value
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
        // Create response
        MaterialValueResponse response = new MaterialValueResponse(EconomyResponse.ResponseType.SUCCESS, null);

        // Get the item data
        MarketableEntity entityData = (MarketableEntity) this.getItem(itemStack);


        // If the item data is null, return 0
        if (entityData == null)
            return (MaterialValueResponse) response.setFailure(String.format("%s cannot be found.", itemStack.getType().name()));


        // Get value and add token to response
        double value = this.calculatePrice(amount, entityData.getQuantity(), this.buyScale, true);
        response.addToken(entityData, amount, value, new ItemStack[]{itemStack});


        // Check if item is banned
        if (!entityData.getAllowed())
            return (MaterialValueResponse) response.setFailure(String.format("%s is banned.", entityData.getCleanName()));


        // If value is less than 0, return 0
        if (value <= 0)
            return (MaterialValueResponse) response.setFailure(String.format("%s is unavailable.", entityData.getCleanName()));


        // Return value
        return response;
    }

    /**
     * Returns the item given as base class DivinityItem is abstract and cannot be instantiated
     *
     * @param data
     * @param defaultData
     * @return
     */
    @Override
    public MarketableMaterial loadItem(String ID, ConfigurationSection data, ConfigurationSection defaultData) {
        return new MarketableEntity(this.getMain(), this, ID, data, defaultData);
    }
}
