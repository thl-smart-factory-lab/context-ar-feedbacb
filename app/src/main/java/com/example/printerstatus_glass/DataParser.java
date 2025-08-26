package com.example.printerstatus_glass;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for parsing MQTT message strings into key-value pairs.
 * Handles both printer status and UWB position data formats.
 */
public class DataParser {

    /**
     * Parses printer status data from MQTT message string.
     * @param input The raw message string (e.g., "printer_status,temp=60")
     * @return Map of parsed key-value pairs
     */
    public static Map<String, String> parsePrinterData(String input) {
        return parseInputString(input, "printer_status,");
    }

    /**
     * Parses UWB position data from MQTT message string.
     * @param input The raw message string (e.g., "position,x=1.5,y=2.0")
     * @return Map of parsed key-value pairs
     */
    public static Map<String, String> parseUwbData(String input) {
        return parseInputString(input, "position,");
    }

    /**
     * Core parsing method that handles string normalization and key-value extraction.
     * @param input The raw input string to parse
     * @param prefix The expected message prefix to remove
     * @return Map containing all valid key-value pairs
     */
    private static Map<String, String> parseInputString(String input, String prefix) {
        Map<String, String> dataMap = new HashMap<>();

        // Return empty map for null or empty input
        if (input == null || input.isEmpty()) {
            return dataMap;
        }

        // Normalize the input string:
        // 1. Remove message prefix if present
        // 2. Replace first space with comma for consistent splitting
        String standardized = input.startsWith(prefix) ?
                input.substring(prefix.length()) : input;
        standardized = standardized.replaceFirst(" ", ","); // 

        // Process each comma-separated key-value pair
        String[] parts = standardized.split(",");
        for (String part : parts) {
            // Split on first equals sign only
            String[] keyValue = part.split("=", 2);

            // Only process if we have both key and value
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();

                // Clean value by removing surrounding quotes if present
                String value = keyValue[1].trim()
                        .replaceAll("^\"|\"$", "")
                        .replaceAll("^'|'$", "");

                // Store non-empty key-value pairs
                if (!key.isEmpty() && !value.isEmpty()) {
                    dataMap.put(key, value);
                }
            }
        }
        return dataMap;
    }
}