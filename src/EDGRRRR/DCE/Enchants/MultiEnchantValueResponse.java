package EDGRRRR.DCE.Enchants;

import com.sun.istack.internal.NotNull;
import net.milkbowl.vault.economy.EconomyResponse;

import java.util.HashMap;

/**
 * A multi-enchant value response
 */
public class MultiEnchantValueResponse {
    // The type of response
    public final EconomyResponse.ResponseType responseType;
    // The error message
    public final String errorMessage;
    // The EnchantID to EnchantData map
    public final HashMap<String, EnchantData> enchantIDMap;
    // The enchantID to level map
    public final HashMap<String, Integer> enchantLevelMap;
    // The enchantID to value map
    public final HashMap<String, Double> enchantValueMap;
    // The total value of the enchants
    public final double totalValue;

    /**
     * Constructor
     * @param enchantIDMap - The enchantID to EnchantData map.
     * @param enchantLevelMap - The enchantID to enchantLevel map.
     * @param enchantValueMap - The enchantID to enchantValue map.
     * @param totalValue - The total value of all enchants
     * @param responseType - The type of response
     * @param errorMessage - The error message, if any.
     */
    public MultiEnchantValueResponse(HashMap<String, EnchantData> enchantIDMap, HashMap<String, Integer> enchantLevelMap, HashMap<String, Double> enchantValueMap, double totalValue, EconomyResponse.ResponseType responseType, String errorMessage) {
        this.responseType = responseType;
        this.errorMessage = errorMessage;
        this.enchantIDMap = enchantIDMap;
        this.enchantLevelMap = enchantLevelMap;
        this.enchantValueMap = enchantValueMap;
        this.totalValue = totalValue;
    }

    /**
     * Returns if the value was successful or not
     * @return boolean
     */
    @NotNull
    public boolean isSuccess() {
        return this.responseType == EconomyResponse.ResponseType.SUCCESS;
    }

    /**
     * Returns if the value was a failure or not
     * @return boolean
     */
    @NotNull
    public boolean isFailure() {
        return !this.isSuccess();
    }
}
