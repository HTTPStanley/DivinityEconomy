package edgrrrr.de.math;

import java.util.logging.Logger;

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
        // Price breakdown
        // Prices were balanced in data.csv
        // Prices are determined by quantity
        // Price = $1 @ 1000000 (1 million) items
        // Price = $2 @ 500000 (5 hundred-thousand) items
        // Price is then scaled - such as the addition of tax (20% by default)
        // Price is then scaled for inflation
        // Inflation works by calculating the default total items and dividing it by the new total items
        // This results in an increase in price when there are less items in the market than default
        // Or a decrease in price when there are more items in the market than default

        //Don't accept negatives!
        // I guess this counts as a fix?
        boolean error = false;
        String et = "n/a";
        if (baseQuantity < 0) {
            baseQuantity = 1;
            error = true;
            et = "BQ";
        }
        if (currentQuantity < 0) {
            currentQuantity = 1;
            error = true;
            et = "CQ";
        }
        if (scale < 0) {
            scale = 1d;
            error = true;
            et = "S";
        }
        if (inflation < 0) {
            inflation = 1d;
            error = true;
            et = "I";
        }

        if (currentQuantity == 0) currentQuantity+=1;
        double value = (getScale(baseQuantity, currentQuantity)) * scale * inflation;

        // TODO: fix this properly? cunt.
        // YEAH WELL I DON'T FUCKING KNOW WHAT IS WRONG OK??
        // LOOK, I'VE ADDED THE "DON'T ACCEPT NEGATIVES" CODE, WHICH SHOULD TECHNICALLY FIX THIS????
        // BUT TECHNICALLY IF THE CASE ARISES WHEREBY ONE OF THE GIVEN VARIABLES ARE NEGATIVE, THERE IS AN ISSUE ELSEWHERE.
        if (value < 0) {
            value = -value;
            error = true;
        }
        // TODO: Or don't. lazy dick face.

        if (error) {
            Logger.getLogger("Minecraft").warning("A math error has occurred in getPrice. (%s) Show this to a developer.");
        }
        return value;
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
