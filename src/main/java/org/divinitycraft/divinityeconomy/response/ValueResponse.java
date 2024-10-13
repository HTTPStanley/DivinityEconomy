package org.divinitycraft.divinityeconomy.response;

import net.milkbowl.vault.economy.EconomyResponse;

import javax.annotation.Nonnull;

/**
 * Used for storing the response to the query of the value of an item
 */
public class ValueResponse extends Response {
    // The value of the item
    private double value = 0D;


    /**
     * Constructor
     * @param value
     * @param responseType
     * @param errorMessage
     */
    public ValueResponse(double value, @Nonnull EconomyResponse.ResponseType responseType, String errorMessage) {
        super(responseType, errorMessage);
        this.value = value;
    }


    public ValueResponse(@Nonnull EconomyResponse.ResponseType responseType, String errorMessage) {
        super(responseType, errorMessage);
    }


    /**
     * Constructor
     */
    public ValueResponse() {
        super();
    }



    /**
     * Constructor
     * @param value
     */
    public ValueResponse(double value) {
        super();
        this.setValue(value);
    }


    /**
     * Adds the value of the response to this response
     * If the response is a failure, the error message is set and the response type is set to FAILURE
     * @param response
     * @return
     */
    public ValueResponse addResponse(ValueResponse response) {
        this.addValue(response.getValue());
        if (response.isFailure()){
            this.setFailure(response.getErrorMessage());
        }

        return this;
    }


    /**
     * Returns the value of the response
     * @return double
     */
    public double getValue() {
        return this.value;
    }


    /**
     * Sets the value of the response
     * @param value
     */
    protected ValueResponse setValue(double value) {
        this.value = value;
        return this;
    }


    /**
     * Adds a value to the response
     * @param value
     * @return
     */
    protected ValueResponse addValue(double value) {
        this.value = this.value + value;
        return this;
    }


    /**
     * Removes a value from the response
     * @param value
     * @return
     */
    protected ValueResponse remValue(double value) {
        this.value = this.value - value;
        return this;
    }


    /**
     * Returns the to string of the response
     */
    @Override
    public String toString() {
        return String.format("ValueResponse{ type=%s, errorMessage=%s, value=%s }",
                this.getResponseType(),
                this.getErrorMessage(),
                this.getValue());
    }
}
