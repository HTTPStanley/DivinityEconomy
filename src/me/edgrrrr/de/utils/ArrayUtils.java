package me.edgrrrr.de.utils;

import java.util.Arrays;

public class ArrayUtils {

    /**
     * Creates an array containing digits from start to end, including start but not end.
     */
    public static int[] range(int start, int end) {
        int[] rangeArray = new int[(end+1) - start];
        int count = start;
        for (int idx = 0; idx < end; idx++) {
            rangeArray[idx] = count;
            count += 1;
        }

        return rangeArray;
    }

    /**
     * Uses the range() function to create a range array, however as strings instead.
     */
    public static String[] strRange(int start, int end) {
        int[] rangeArray = range(start, end);
        return Arrays.stream(rangeArray).mapToObj(String::valueOf).toArray(String[]::new);
    }
}
