package me.edgrrrr.de.market.items.materials.entity;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialManager;
import me.edgrrrr.de.response.ValueResponse;
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
    public ValueResponse getSellValue(ItemStack itemStack, int amount) {
        ValueResponse response;

        MarketableEntity entityData = (MarketableEntity) this.getItem(itemStack);

        if (entityData == null) {
            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "item cannot be found.");
        } else {
            if (!entityData.getAllowed()) {
                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "item is banned.");
            } else {
                double value = this.calculatePrice(amount, entityData.getQuantity(), this.sellScale, false);
                if (value > 0) {
                    response = new ValueResponse(value, EconomyResponse.ResponseType.SUCCESS, "");
                } else {
                    response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "market is saturated.");
                }
            }
        }

        return response;
    }

    /**
     * Returns the value of an itemstack
     *
     * @param itemStack - The item stack to get the value of
     * @return MaterialValue
     */
    @Override
    public ValueResponse getBuyValue(ItemStack itemStack, int amount) {
        ValueResponse response;

        MarketableEntity entityData = (MarketableEntity) this.getItem(itemStack);
        if (entityData == null) {
            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "item cannot be found.");

        } else {
            if (!entityData.getAllowed()) {
                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "item is banned.");

            } else {
                double value = this.calculatePrice(amount, entityData.getQuantity(), this.buyScale, true);
                if (value > 0) {
                    response = new ValueResponse(value, EconomyResponse.ResponseType.SUCCESS, "");
                } else {
                    response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "market is saturated.");
                }
            }
        }

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
