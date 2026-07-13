package org.divinitycraft.divinityeconomy.market.pricing;

import org.divinitycraft.divinityeconomy.Constants;
import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.divinitycraft.divinityeconomy.utils.Converter;

/**
 * V1 Pricing Model - Linear Supply/Demand System
 * This is the original pricing model that uses a simple linear inverse relationship.
 * Price is directly proportional to the inverse of supply (price = baseQuantity/currentQuantity * 15).
 *
 * Characteristics:
 * - Simple linear relationship between supply and price
 * - Price doubles when supply is halved
 * - Can lead to extreme price swings with large supply changes
 */
public class V1PricingModel implements PricingModel {

    private double minItemValue;
    private double maxItemValue;

    public V1PricingModel(double minItemValue, double maxItemValue) {
        this.minItemValue = minItemValue;
        this.maxItemValue = maxItemValue;
    }

    @Override
    public double calculatePrice(MarketableToken token, double baseQuantity, double defaultMarketSize,
                                 double marketSize, double amount, double scale, boolean purchase,
                                 boolean wholeMarketInflation) {
        double currentQuantity = token.getQuantity();
        double value = 0;
        double inflation = 1.0;

        // Loop for amount
        // Get the price and add it to the value
        // if purchase = true, remove 1 stock to simulate decrease
        // if purchase = false, add 1 stock to simulate increase
        for (int i = 0; i < Converter.constrainInt((int) amount, Constants.MIN_VALUE_AMOUNT, Constants.MAX_VALUE_AMOUNT); i++) {
            if (wholeMarketInflation) {
                inflation = getInflation(defaultMarketSize, marketSize);
            }

            if (purchase) {
                value += getPrice(baseQuantity, currentQuantity, scale, inflation);
                currentQuantity -= 1;
                if (wholeMarketInflation) marketSize -= 1;
            } else {
                value += getPrice(baseQuantity, currentQuantity + 1, scale, inflation);
                currentQuantity += 1;
                if (wholeMarketInflation) marketSize += 1;
            }
        }

        return value;
    }

    @Override
    public double getPrice(double baseQuantity, double currentQuantity, double scale, double inflation) {
        // if currentQuantity is zero, increment it by one to avoid division by zero
        currentQuantity = Math.max(currentQuantity, 1);

        // Get the raw price using linear formula
        double rawPrice = getRawPrice(baseQuantity, currentQuantity);

        // apply scaling and inflation factors, then fit the price to the required constraints
        return fitPriceToConstraints(rawPrice * scale * inflation);
    }

    @Override
    public int calculateStock(double baseQuantity, double price, double scale, double inflation) {
        // Reverse the scaling and inflation factors to get the raw price
        double rawPrice = price / (scale * inflation);

        // Since the raw price is calculated as (scale * 15)
        // We need to isolate the scale, which is baseQuantity / currentQuantity
        double finalScale = rawPrice / 15;

        // Now, use the scale to find the current quantity
        double newQuantity = baseQuantity / finalScale;

        return (int) newQuantity;
    }

    @Override
    public double getInflation(double defaultMarketSize, double actualMarketSize) {
        // Simple linear scale for inflation
        return getScale(defaultMarketSize, actualMarketSize);
    }

    @Override
    public String getModelName() {
        return "V1 - Linear";
    }

    @Override
    public String getDescription() {
        return "Original linear pricing model with simple inverse supply-demand relationship. " +
               "Price = (baseQuantity/currentQuantity) * 15. Can produce extreme price swings.";
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    /**
     * Calculates the raw price of the product based on base and current quantities.
     * Uses simple linear inverse relationship.
     *
     * @param baseQuantity    - Base quantity of the product.
     * @param currentQuantity - Current quantity of the product.
     * @return double - The raw price of the product.
     */
    private double getRawPrice(double baseQuantity, double currentQuantity) {
        // calculate scale and apply it to the price
        double scale = getScale(baseQuantity, currentQuantity);

        // calculate raw price and fit it to the required constraints
        return fitPriceToConstraints(scale * 15);
    }

    /**
     * Calculates the scale of a number based on its base value.
     * It's essentially the ratio of base quantity to the current quantity.
     *
     * @param baseQuantity    - Base quantity of the product.
     * @param currentQuantity - Current quantity of the product.
     * @return double - The scale factor.
     */
    private double getScale(double baseQuantity, double currentQuantity) {
        return baseQuantity / currentQuantity;
    }

    /**
     * Returns the price of an item fit to the max and min constraints
     * @param price - The price to constrain
     * @return double - The constrained price
     */
    private double fitPriceToConstraints(double price) {
        return Converter.constrainDouble(price, this.minItemValue, this.maxItemValue);
    }

    /**
     * Updates the min and max item value constraints
     * @param minItemValue - The minimum item value
     * @param maxItemValue - The maximum item value
     */
    public void updateConstraints(double minItemValue, double maxItemValue) {
        this.minItemValue = minItemValue;
        this.maxItemValue = maxItemValue;
    }
}
