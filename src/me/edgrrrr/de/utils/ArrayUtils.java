package me.edgrrrr.de.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArrayUtils {

    /**
     * Creates an array containing digits from start to end, including start but not end.
     */
    public static int[] range(int start, int end) {
        int[] rangeArray = new int[(end + 1) - start];
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

    public static Map<Integer, List<Object>> toPages(Object[] objects, int pageSize) {
        Map<Integer, List<Object>> pages = new ConcurrentHashMap<>();

        int pageNum = 0;
        List<Object> page = new ArrayList<>();
        for (Object value : objects) {
            if (page.size() == pageSize) {
                pages.put(pageNum, page);
                pageNum += 1;
                page = new ArrayList<>();
            }

            page.add(value);
        }

        if (!pages.containsKey(pageNum)) {
            pages.put(pageNum, page);
        }

        return pages;
    }
}
