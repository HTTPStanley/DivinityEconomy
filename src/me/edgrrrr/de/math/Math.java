package me.edgrrrr.de.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Math {
    /**
     * Calculates the number of ticks from the seconds provided
     *
     * @param seconds - The number of seconds
     * @return int - Ticks
     */
    public static int getTicks(int seconds) {
        return seconds * 20;
    }

    /**
     * Calculates the number of ticks from the milliseconds provided
     *
     * @param milliseconds - The number of milliseconds
     * @return int - Ticks
     */
    public static int getTicks(long milliseconds) {
        return getTicks((int) milliseconds / 1000);
    }

    /**
     * A function for extracting a double from a String
     * will return null if an error occurs (such as the string not containing a double)
     *
     * @param arg - A string to convert to a double
     * @return double - A potentially converted double
     */
    public static double getDouble(String arg) {
        // Instantiate amount
        double amount;

        // Try to parse the double
        // Catch the error and set to null
        try {
            amount = Double.parseDouble(arg);
        } catch (Exception e) {
            amount = 0.0;
        }

        return amount;
    }

    public static int getInt(String arg) {
        return (int) Math.getDouble(arg);
    }


    /**
     * Gets the level of inflation based on the parameters supplied
     * Just returns getScale(default, actual)
     * @param defaultMarketSize   - The base quantity of materials in the market
     * @param actualMarketSize - The actual current quantity of materials in the market
     * @return double - The level of inflation
     */
    public static double getInflation(double defaultMarketSize, double actualMarketSize) {
        return getScale(defaultMarketSize, actualMarketSize);
    }

    /**
     * Calculates the price of an amount of items
     * @param baseQuantity - The base quantity of the item
     * @param currentQuantity - The current quantity of the item
     * @param defaultMarketSize - The default market size
     * @param marketSize - The current market size
     * @param amount - The amount of the item to buy
     * @param scale - The price scaling (e.g. tax)
     * @param purchase - Whether this is a purchase or a sale.
     * @return double
     */
    public static double calculatePrice(double baseQuantity, double currentQuantity, double defaultMarketSize, double marketSize, double amount, double scale, boolean purchase, boolean dynamic, boolean marketInflation) {
        double value = 0;
        double inflation = 1.0;

        // Loop for amount
        // Get the price and add it to the value
        // if purchase = true
        // remove 1 stock to simulate decrease
        // if purchase = false
        // add 1 stock to simulate increase
        for (int i = 1; i <= amount; i++) {
            if (marketInflation) {
                inflation = getInflation(defaultMarketSize, marketSize);
            }
            if (purchase) {
                value += getPrice(baseQuantity, currentQuantity, scale, inflation);
                if (dynamic) currentQuantity -= 1;
                if (marketInflation) marketSize -= 1;
            } else {
                value += getPrice(baseQuantity, currentQuantity+1, scale, inflation);
                if (dynamic) currentQuantity += 1;
                if (marketInflation) marketSize += 1;
            }
        }

        return value;
    }

    /**
     * Gets the price of a product based on the parameters supplied
     * @param baseQuantity - The base quantity of items in the market
     * @param currentQuantity - The current quantity of items in the market
     * @param scale - The scaling to apply to the price
     * @param inflation - The inflation of the market
     * @return double
     */
    public static double getPrice(double baseQuantity, double currentQuantity, double scale, double inflation) {
        if (currentQuantity == 0) currentQuantity+=1;

        return Math.getRawPrice(baseQuantity, currentQuantity).multiply(
                BigDecimal.valueOf(scale).setScale(8, RoundingMode.HALF_DOWN)
        ).multiply(
                BigDecimal.valueOf(inflation).setScale(8, RoundingMode.HALF_DOWN)
        ).doubleValue();
    }

    private static BigDecimal getRawPrice(double baseQuantity, double currentQuantity) {
        return BigDecimal.valueOf(
                getScale(baseQuantity, currentQuantity)
        ).setScale(8, RoundingMode.HALF_DOWN).multiply(
                BigDecimal.valueOf(10).setScale(8, RoundingMode.HALF_DOWN)
        ).add(
                BigDecimal.valueOf(
                        getScale(baseQuantity, currentQuantity)
                ).multiply(
                        BigDecimal.valueOf(5)
                ).setScale(8, RoundingMode.HALF_DOWN)
        );
    }

    /**
     * Returns the scale of a number compared to it's base value
     * base / current
     * @param baseQuantity - The base quantity of items
     * @param currentQuantity - The current quantity of items
     * @return double
     */
    public static double getScale(double baseQuantity, double currentQuantity) {
        return baseQuantity / currentQuantity;
    }
}
