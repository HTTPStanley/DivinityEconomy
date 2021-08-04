package me.edgrrrr.de.response;

import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Used for storing the response to the query of the value of an item
 */
public class ValueResponse extends Response {
    // The value of the enchant
    public final double value;

    /**
     * Constructor
     *
     * @param value        - The value of the enchant
     * @param responseType - The type of response
     * @param errorMessage - The error, if any.
     */
    public ValueResponse(double value, EconomyResponse.ResponseType responseType, String errorMessage) {
        super(responseType, errorMessage);
        this.value = value;
    }
}
