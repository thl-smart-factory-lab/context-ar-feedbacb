package com.example.printerstatus_glass;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that holds and manages printer and UWB data.
 * Provides thread-safe operations for data storage and retrieval.
 */
public class DataHolder {
    // Singleton instance
    private static DataHolder instance;

    // Thread-safe storage for printer status data (key-value pairs)
    private final Map<String, String> printerData = new HashMap<>();

    // Thread-safe storage for UWB position data (key-value pairs)
    private final Map<String, String> uwbData = new HashMap<>();

    /**
     * Private constructor to enforce singleton pattern
     */
    private DataHolder() {}

    /**
     * Returns the singleton instance of DataHolder (thread-safe)
     * @return The single instance of DataHolder
     */
    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    /**
     * Updates printer data with new values (thread-safe)
     * @param newData Map containing new printer status values
     */
    public void updatePrinterData(Map<String, String> newData) {
        synchronized (printerData) {
            printerData.putAll(newData);
        }
    }

    /**
     * Updates UWB data with new values (thread-safe)
     * @param newData Map containing new UWB position values
     */
    public void updateUwbData(Map<String, String> newData) {
        synchronized (uwbData) {
            uwbData.putAll(newData);
        }
    }

    /**
     * Retrieves a copy of current printer data (thread-safe)
     * @return New Map containing all printer data
     */
    public Map<String, String> getPrinterData() {
        synchronized (printerData) {
            return new HashMap<>(printerData);
        }
    }

    /**
     * Retrieves a copy of current UWB data (thread-safe)
     * @return New Map containing all UWB data
     */
    public Map<String, String> getUwbData() {
        synchronized (uwbData) {
            return new HashMap<>(uwbData);
        }
    }

    /**
     * Clears all stored data (thread-safe)
     */
    public void clearAll() {
        synchronized (printerData) {
            printerData.clear();
        }
        synchronized (uwbData) {
            uwbData.clear();
        }
    }
}