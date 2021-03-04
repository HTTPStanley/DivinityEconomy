package EDGRRRR.DCE.Materials;

import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Used for storing the value response of a material
 */
public class MaterialValueResponse {
    // The value of a material
    public final double value;
    // The error, if any.
    public final String errorMessage;
    // The response type
    public final EconomyResponse.ResponseType responseType;

    /**
     * Constructor
     * @param value - The value of the material
     * @param errorMessage - The error message, if any
     * @param responseType - The type of response
     */
    public MaterialValueResponse(double value, String errorMessage, EconomyResponse.ResponseType responseType) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.responseType = responseType;
    }

    public boolean isSuccess() {
        return this.responseType == EconomyResponse.ResponseType.SUCCESS;
    }

    public boolean isFailure() {
        return !this.isSuccess();
    }
}
