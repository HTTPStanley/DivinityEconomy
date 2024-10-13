package org.divinitycraft.divinityeconomy.utils;

public class Converter {
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
     * A function for extracting a double from a String
     * will return null if an error occurs (such as the string not containing a double)
     *
     * @param arg - A string to convert to a double
     * @return double - A potentially converted double
     */
    public static double getDouble(String arg) {
        // Instantiate amount
        double amount = 0;

        // Try to parse the double
        // Catch the error and set to null
        try {
            amount = Double.parseDouble(arg);
        } catch (Exception e) {}

        return amount;
    }

    /**
     * A function for extracting an integer from a String
     * @param arg
     * @return
     */
    public static int getInt(String arg) {
        // Instantiate amount
        int amount = 0;

        // Try to parse the integer
        // Catch the error and set to null
        try {
            amount = Integer.parseInt(arg);
        } catch (Exception e) {}

        return amount;
    }

    /**
     * A function for extracting a long from a String
     * @param arg
     * @return
     */
    public static long getLong(String arg) {
        // Instantiate amount
        long amount = 0;

        // Try to parse the integer
        // Catch the error and set to null
        try {
            amount = Long.parseLong(arg);
        } catch (Exception e) {}

        return amount;
    }

    /**
     * A function for extracting a boolean from a String
     * This function will case-insensitive match the word "true", otherwise it will return false
     * @param arg
     * @return
     */
    public static boolean getBoolean(String arg) {
        return arg.equalsIgnoreCase("true");
    }

    /**
     * A function for constraining an integer
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static int constrainInt(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }


    /**
     * A function for constraining a long
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static long constrainLong(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }


    /**
     * A function for constraining a double
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double constrainDouble(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
