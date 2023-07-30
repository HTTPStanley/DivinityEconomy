package me.edgrrrr.de.response;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base class for a response
 */
public class Response {
    // The response type
    private ResponseType responseType;
    // The error message, if any.
    private String errorMessage;

    /**
     * Constructor
     *
     * @param responseType - The type of response.
     * @param errorMessage - The error message, if any.
     */
    public Response(ResponseType responseType, String errorMessage) {
        this.responseType = responseType;
        this.errorMessage = errorMessage;
    }


    /**
     * Empty Constructor
     */
    public Response() {
        this(ResponseType.NOT_IMPLEMENTED, null);
    }

    /**
     * Returns the error message, if any.
     * @return
     */
    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Returns the response type
     */
    @Nonnull
    public ResponseType getResponseType() {
        return responseType;
    }



    /**
     * Returns the response type
     * @param responseType
     */
    public Response setResponseType(@Nonnull ResponseType responseType) {
        this.responseType = responseType;
        return this;
    }

    /**
     * Returns the response type
     * @param errorMessage
     */
    public Response setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }


    /**
     * Set the response type and error message
     * @param responseType
     * @param errorMessage
     * @return
     */
    public Response setResponse(@Nonnull ResponseType responseType, @Nullable String errorMessage) {
        this.responseType = responseType;
        this.errorMessage = errorMessage;
        return this;
    }


    /**
     * Set the response type to failure, and set the error message.
     * @param errorMessage
     * @return
     */
    public Response setFailure(@Nullable String errorMessage) {
        return this.setResponse(ResponseType.FAILURE, errorMessage);
    }


    /**
     * Set the response type to success, and set the error message.
     * @param errorMessage
     * @return
     */
    public Response setSuccess(@Nullable String errorMessage) {
        return this.setResponse(ResponseType.SUCCESS, errorMessage);
    }


    /**
     * If the response was a success, or not.
     *
     * @return boolean - is success
     */
    public boolean isSuccess() {
        return this.responseType == ResponseType.SUCCESS;
    }

    /**
     * If the response was a failure, or not.
     *
     * @return boolean - is failure
     */
    public boolean isFailure() {
        return !this.isSuccess();
    }


    /**
     * If the response has an error message, or not.
     * @return
     */
    public boolean hasErrorMessage() {
        return this.errorMessage != null;
    }


    /**
     * Returns the toString of the response type
     */
    @Override
    public String toString() {
        return String.format("Response: { type: %s, message: %s }", this.responseType.toString(), this.errorMessage != null ? this.errorMessage : "null");
    }
}
