package org.divinitycraft.divinityeconomy.market.pricing;

import org.divinitycraft.divinityeconomy.Constants;
import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.divinitycraft.divinityeconomy.utils.Converter;

/**
 * V2 Pricing Model - Elasticity-Based Supply/Demand System
 * This model uses logarithmic curves to simulate realistic price elasticity.
 * Price changes follow a power curve (price = basePrice * (baseQuantity/currentQuantity)^elasticity).
 *
 * Characteristics:
 * - Uses elasticity coefficient (0.7 for items, 0.3 for market inflation)
 * - Scarcity premium: prices rise exponentially when supply is low
 * - Diminishing returns: prices fall logarithmically when supply is abundant
 * - More realistic supply-demand dynamics with smoother price changes
 */
public class V2PricingModel implements PricingModel {

    private double minItemValue;
    private double maxItemValue;

    // Elasticity coefficients
    private static final double ITEM_ELASTICITY = 0.7;
    private static final double MARKET_ELASTICITY = 0.3;
    private static final double BASE_PRICE = 1.0;

    public V2PricingModel(double minItemValue, double maxItemValue) {
        this.minItemValue = minItemValue;
        this.maxItemValue = maxItemValue;
    }

    @Override
    public double calculatePrice(MarketableToken token, double baseQuantity, double defaultMarketSize,
                                 double marketSize, double amount, double scale, boolean purchase,
                                 boolean wholeMarketInflation) {
        return calculatePrice(token, baseQuantity, defaultMarketSize, marketSize, amount, scale, purchase, wholeMarketInflation, token.getElasticity());
    }

    /**
     * Calculates the price of an amount of items with custom elasticity, factoring in dynamic supply changes.
     *
     * @param token                - The marketable token being priced
     * @param baseQuantity         - The base quantity of the item
     * @param defaultMarketSize    - The default market size
     * @param marketSize           - The current market size
     * @param amount               - The amount of the item to buy/sell
     * @param scale                - The price scaling (e.g. tax)
     * @param purchase             - Whether this is a purchase (true) or sale (false)
     * @param wholeMarketInflation - Whether to apply market-wide inflation
     * @param elasticity           - The elasticity coefficient for this specific item
     * @return double - Total price for the transaction
     */
    public double calculatePrice(MarketableToken token, double baseQuantity, double defaultMarketSize,
                                 double marketSize, double amount, double scale, boolean purchase,
                                 boolean wholeMarketInflation, double elasticity) {
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
                value += getPrice(baseQuantity, currentQuantity, scale, inflation, elasticity);
                currentQuantity -= 1;
                if (wholeMarketInflation) marketSize -= 1;
            } else {
                value += getPrice(baseQuantity, currentQuantity + 1, scale, inflation, elasticity);
                currentQuantity += 1;
                if (wholeMarketInflation) marketSize += 1;
            }
        }

        return value;
    }

    @Override
    public double getPrice(double baseQuantity, double currentQuantity, double scale, double inflation) {
        // Default elasticity if not using the overload
        return getPrice(baseQuantity, currentQuantity, scale, inflation, ITEM_ELASTICITY);
    }

    /**
     * Returns the price of a single unit at the current stock level with custom elasticity.
     *
     * @param baseQuantity    - Base quantity of the product in the market
     * @param currentQuantity - Current quantity of the product in the market
     * @param scale           - Scaling factor to apply to the price
     * @param inflation       - Inflation factor in the market
     * @param elasticity      - The elasticity coefficient for this specific item
     * @return double - The price of one unit
     */
    public double getPrice(double baseQuantity, double currentQuantity, double scale, double inflation, double elasticity) {
        // if currentQuantity is zero, increment it by one to avoid division by zero
        currentQuantity = Math.max(currentQuantity, 1);

        // get the raw price using elasticity formula with custom elasticity
        double rawPrice = getRawPrice(baseQuantity, currentQuantity, elasticity);

        // apply scaling and inflation factors, then fit the price to the required constraints
        return fitPriceToConstraints(rawPrice * scale * inflation);
    }

    @Override
    public int calculateStock(double baseQuantity, double price, double scale, double inflation) {
        // Reverse the scaling and inflation factors to get the raw price
        double rawPrice = price / (scale * inflation);

        // Reverse the logarithmic pricing formula
        // rawPrice = basePrice * (1 / supplyRatio)^elasticity
        // Solving for supplyRatio:
        // (1 / supplyRatio)^elasticity = rawPrice / basePrice
        // 1 / supplyRatio = (rawPrice / basePrice)^(1/elasticity)
        // supplyRatio = 1 / ((rawPrice / basePrice)^(1/elasticity))
        // currentQuantity = baseQuantity * supplyRatio

        double priceRatio = rawPrice / BASE_PRICE;
        double inverseSupplyRatio = Math.pow(priceRatio, 1.0 / ITEM_ELASTICITY);
        double supplyRatio = 1.0 / inverseSupplyRatio;
        double newQuantity = baseQuantity * supplyRatio;

        return (int) newQuantity;
    }

    @Override
    public double getInflation(double defaultMarketSize, double actualMarketSize) {
        // Avoid division by zero
        if (actualMarketSize <= 0) actualMarketSize = 1;

        // Market-wide inflation uses similar elasticity curve
        // But with lower elasticity (0.3) for more stable economy-wide changes
        double marketSupplyRatio = actualMarketSize / defaultMarketSize;

        // Calculate inflation multiplier using power curve
        return Math.pow(1.0 / marketSupplyRatio, MARKET_ELASTICITY);
    }

    @Override
    public String getModelName() {
        return "V2 - Elasticity";
    }

    @Override
    public String getDescription() {
        return "Improved elasticity-based pricing model using logarithmic supply-demand curves. " +
               "Price = 15 * (baseQuantity/currentQuantity)^0.7. Provides realistic price movements " +
               "with scarcity premiums and diminishing returns.";
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    /**
     * Calculates the raw price of the product based on base and current quantities.
     * Uses a logarithmic supply-demand curve for more realistic price elasticity.
     * When supply is low, prices increase exponentially (scarcity premium).
     * When supply is high, prices decrease logarithmically (diminishing returns).
     *
     * @param baseQuantity    - Base quantity of the product.
     * @param currentQuantity - Current quantity of the product.
     * @param elasticity      - The elasticity coefficient for this item.
     * @return double - The raw price of the product.
     */
    private double getRawPrice(double baseQuantity, double currentQuantity, double elasticity) {
        // Calculate the supply ratio (how much supply vs baseline)
        double supplyRatio = currentQuantity / baseQuantity;

        // Use logarithmic curve for more realistic supply-demand dynamics
        // When supply is scarce (ratio < 1): prices rise exponentially
        // When supply is abundant (ratio > 1): prices fall with diminishing returns
        // Formula: basePrice * (baseQuantity/currentQuantity)^elasticity
        double priceMultiplier = Math.pow(1.0 / supplyRatio, elasticity);

        double rawPrice = BASE_PRICE * priceMultiplier;

        return fitPriceToConstraints(rawPrice);
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
