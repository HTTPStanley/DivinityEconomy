package org.divinitycraft.divinityeconomy.market.pricing;

import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.divinitycraft.divinityeconomy.utils.Converter;

/**
 * Static Bottomless Pricing Model - Fixed Price with Infinite Supply
 * This model provides fixed pricing like STATIC, but does not update quantities during transactions.
 * Think of it as a bottomless market with stable prices.
 *
 * Characteristics:
 * - Fixed prices that don't change based on supply
 * - Quantities are NOT updated when buying or selling
 * - No inflation calculations
 * - Useful for servers that want stable pricing with unlimited supply/demand
 * - Each item maintains its configured PRICE from the config files
 *
 * Difference from STATIC:
 * - STATIC: Updates quantities during buy/sell transactions
 * - STATIC_BOTTOMLESS: Does NOT update quantities (infinite supply/demand)
 */
public class StaticBottomlessPricingModel implements PricingModel {

    private double minItemValue;
    private double maxItemValue;

    public StaticBottomlessPricingModel(double minItemValue, double maxItemValue) {
        this.minItemValue = minItemValue;
        this.maxItemValue = maxItemValue;
    }

    @Override
    public double calculatePrice(MarketableToken token, double baseQuantity, double defaultMarketSize,
                                 double marketSize, double amount, double scale, boolean purchase,
                                 boolean wholeMarketInflation) {
        // Static bottomless pricing: multiply the fixed price by the amount and scale
        // No dynamic price changes during the transaction
        // No quantity updates occur (handled by the caller checking if model is bottomless)
        double price = token.getPrice() * amount * scale;
        return fitPriceToConstraints(price);
    }

    @Override
    public double getPrice(double baseQuantity, double currentQuantity, double scale, double inflation) {
        // Return static price with only scale applied (no supply/demand or inflation)
        // baseQuantity is repurposed to pass the item's base price from config
        double staticPrice = baseQuantity * scale;
        return fitPriceToConstraints(staticPrice);
    }

    @Override
    public int calculateStock(double baseQuantity, double price, double scale, double inflation) {
        // Since price doesn't change with stock in static bottomless mode,
        // we can't meaningfully calculate a stock level from price
        // Return 0 to indicate stock calculation is not applicable
        return 0;
    }

    @Override
    public double getInflation(double defaultMarketSize, double actualMarketSize) {
        // No inflation in static bottomless pricing
        return 1.0;
    }

    @Override
    public String getModelName() {
        return "Static Bottomless";
    }

    @Override
    public String getDescription() {
        return "Static bottomless pricing model with fixed prices and no quantity updates. " +
               "Each item maintains its configured PRICE from the config files regardless of " +
               "supply and demand. Quantities are NOT updated during transactions, providing " +
               "an infinite supply/demand market. Useful for servers that want predictable, " +
               "stable pricing with unlimited availability.";
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean updatesQuantityOnTransaction() {
        return false; // STATIC_BOTTOMLESS does not update quantities
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
