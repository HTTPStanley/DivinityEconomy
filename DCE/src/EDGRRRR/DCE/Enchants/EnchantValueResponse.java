package EDGRRRR.DCE.Enchants;

import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Used for storing the value of an enchant, type of response and the error, if any.
 */
public class EnchantValueResponse {
    // The value of the enchant
    public final double value;
    // The type of response, success, failure?
    public final EconomyResponse.ResponseType responseType;
    // The error, if any.
    public final String errorMessage;

    /**
     * Constructor
     * @param value - The value of the enchant
     * @param responseType - The type of response
     * @param errorMessage - The error, if any.
     */
    public EnchantValueResponse(double value, EconomyResponse.ResponseType responseType, String errorMessage) {
        this.value = value;
        this.responseType = responseType;
        this.errorMessage = errorMessage;
    }
}
