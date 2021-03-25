package edgrrrr.dce.utils;

import edgrrrr.dce.mail.Mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArrayUtils {

    public static int[] range(int start, int end) {
        int[] rangeArray = new int[(end+1) - start];
        int count = start;
        for (int idx = 0; idx < end; idx++) {
            rangeArray[idx] = count;
            count += 1;
        }

        return rangeArray;
    }

    public static String[] strRange(int start, int end) {
        int[] rangeArray = range(start, end);
        return Arrays.stream(rangeArray).mapToObj(String::valueOf).toArray(String[]::new);
    }
}
