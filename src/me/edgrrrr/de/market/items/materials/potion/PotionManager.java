package me.edgrrrr.de.market.items.materials.potion;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialManager;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

public class PotionManager extends MaterialManager {
    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and items with data from the program
     *
     * @param main - The plugin
     */
    public PotionManager(DEPlugin main) {
        super(main, "potions.yml", "potionAliases.yml", new ConcurrentHashMap<String, MarketablePotion>());
    }

    @Override
    public String getType() {
        return "POTION";
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

        MarketablePotion potionData = (MarketablePotion) this.getItem(itemStack);

        if (potionData == null) {
            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "item cannot be found.");
        } else {
            if (!potionData.getAllowed()) {
                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "item is banned.");
            } else {
                double value = this.calculatePrice(amount, potionData.getQuantity(), this.sellScale, false);
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

        MarketablePotion potionData = (MarketablePotion) this.getItem(itemStack);
        if (potionData == null) {
            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "item cannot be found.");

        } else {
            if (!potionData.getAllowed()) {
                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "item is banned.");
            } else {
                double value = this.calculatePrice(amount, potionData.getQuantity(), this.buyScale, true);
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
        return new MarketablePotion(this.getMain(), this, ID, data, defaultData);
    }
}
