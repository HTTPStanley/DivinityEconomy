package me.edgrrrr.de.market.items.materials.potion;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.market.items.materials.MaterialManager;
import me.edgrrrr.de.market.items.materials.MaterialValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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


    @Override
    public Set<String> getLocalKeys() {
        return Arrays.stream(PotionEffectType.values())
                .map(PotionEffectType::getKey)
                .map(NamespacedKey::getKey)
                .map(Object::toString)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
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
        MarketablePotion potionData = (MarketablePotion) this.getItem(itemStack);


        // If the item data is null, return 0
        if (potionData == null)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemCannotBeFound.get(getMain(), itemStack.getType().name()));


        // Get value and add token to response
        double value = this.calculatePrice(amount, potionData.getQuantity(), this.sellScale, false);
        response.addToken(potionData, amount, value, new ItemStack[]{itemStack});


        // Check item is allowed
        if (!potionData.getAllowed())
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsBanned.get(getMain(), potionData.getName()));


        // Check if the market is saturated
        if (value <= 0)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsWorthless.get(getMain(), potionData.getName()));


        // Return the response
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
        MaterialValueResponse response = new MaterialValueResponse(EconomyResponse.ResponseType.SUCCESS, null);


        // Get the item data
        MarketablePotion potionData = (MarketablePotion) this.getItem(itemStack);


        // If the item data is null, return 0
        if (potionData == null)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemCannotBeFound.get(getMain(), itemStack.getType().name()));


        // Get value and add token to response
        double value = this.calculatePrice(amount, potionData.getQuantity(), this.buyScale, true);
        response.addToken(potionData, amount, value, new ItemStack[]{itemStack});


        // Check item is allowed
        if (!potionData.getAllowed())
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsBanned.get(getMain(), potionData.getName()));


        // Check if the market is saturated
        if (value <= 0)
            return (MaterialValueResponse) response.setFailure(LangEntry.MARKET_ItemIsWorthless.get(getMain(), potionData.getName()));


        // Return the response
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
        return new MarketablePotion(getMain(), this, ID, data, defaultData);
    }
}
