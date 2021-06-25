package me.edgrrrr.de.response;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * Base class for a response
 */
public class Response {
    // The response type
    public final ResponseType responseType;
    // The error message, if any.
    public final String errorMessage;

    /**
     * Constructor
     * @param responseType - The type of response.
     * @param errorMessage - The error message, if any.
     */
    public Response (ResponseType responseType, String errorMessage) {
        this.responseType = responseType;
        this.errorMessage = errorMessage;
    }

    /**
     * If the response was a success, or not.
     * @return boolean - is success
     */
    public boolean isSuccess() {
        return this.responseType == ResponseType.SUCCESS;
    }

    /**
     * If the response was a failure, or not.
     * @return boolean - is failure
     */
    public boolean isFailure() {
        return !this.isSuccess();
    }
}
