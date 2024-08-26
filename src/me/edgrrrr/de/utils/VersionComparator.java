package me.edgrrrr.de.utils;

public class VersionComparator {

    public static boolean isVersionLower(String version1, String version2) {
        // Split the version strings into components using "." as the delimiter
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            // Convert the current part to an integer for comparison, default to 0 if part is missing
            int v1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int v2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            // Compare the current components
            if (v1 < v2) {
                return true; // version1 is lower than version2
            } else if (v1 > v2) {
                return false; // version1 is not lower than version2
            }
        }

        // If all components are equal, the versions are not lower than each other
        return false;
    }


    public static boolean isVersionEqual(String version1, String version2) {
        return version1.equals(version2);
    }


    public static boolean isVersionLowerOrEqual(String version1, String version2) {
        return isVersionLower(version1, version2) || isVersionEqual(version1, version2);
    }


    public static boolean isVersionHigher(String version1, String version2) {
        return !isVersionLowerOrEqual(version1, version2);
    }


    public static boolean isVersionHigherOrEqual(String version1, String version2) {
        return !isVersionLower(version1, version2);
    }
}