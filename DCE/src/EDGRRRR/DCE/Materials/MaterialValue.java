package EDGRRRR.DCE.Materials;

import net.milkbowl.vault.economy.EconomyResponse;

public class MaterialValue {
    private final double value;
    private final String errorMessage;
    private final EconomyResponse.ResponseType responseType;

    public MaterialValue(double value, String errorMessage, EconomyResponse.ResponseType responseType) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.responseType = responseType;
    }


    public double getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public EconomyResponse.ResponseType getResponseType() {
        return responseType;
    }
}
