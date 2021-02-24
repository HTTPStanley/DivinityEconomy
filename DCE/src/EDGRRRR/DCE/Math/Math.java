package EDGRRRR.DCE.Math;

public class Math {

    /**
     * A rounding function for rounding double
     *
     * @param amount         - The amount to round
     * @param roundingDigits - The number of digits to round to
     * @return double - The rounded number
     */
    public static double round(double amount, int roundingDigits) {
        // Rounds the amount to the number of digits specified
        // Does this by 10**digits (100 or 10**2 = 2 digits)
        try {
            double roundAmount = java.lang.Math.pow(10, roundingDigits);
            return java.lang.Math.round(amount * roundAmount) / roundAmount;
        } catch (Exception e) {
            return amount;
        }
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
     *
     * @param baseQuantity   - The base quantity of materials in the market
     * @param actualQuantity - The actual current quantity of materials in the market
     * @return double - The level of inflation
     */
    public static double getInflation(double baseQuantity, double actualQuantity) {
        return baseQuantity / actualQuantity;
    }
}
