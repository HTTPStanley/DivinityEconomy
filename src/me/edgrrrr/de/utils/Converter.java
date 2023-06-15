package me.edgrrrr.de.utils;

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

    public static int constrainInt(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }


    public static long constrainLong(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }


    public static double constrainDouble(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
