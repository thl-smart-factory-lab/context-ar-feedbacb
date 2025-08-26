package com.example.printerstatus_glass;

/**
 * Determines the appropriate printer MQTT topic based on physical coordinates.
 * Maps X,Y position values to specific printer zones in a facility.
 */
public class PrinterTopicSelector {
    private static final String TOPIC_A = "sf/printer/a";
    private static final String TOPIC_B = "sf/printer/b";
    private static final String TOPIC_C = "sf/printer/c";

    /**
     * Selects the appropriate printer topic based on X,Y coordinates.
     *
     * Zone Map:
     * - Zone A: X ∈ [-1,1.5], Y ∈ (2.6,4.2]
     * - Zone B: X ∈ [-1,1.5], Y ∈ [1,2.6]
     * - Zone C: X ∈ [5.8,7.3], Y ∈ (2.6,4.2]
     *
     * @param x The X-coordinate position value
     * @param y The Y-coordinate position value
     * @return Corresponding MQTT topic string, or null if position doesn't match any zone
     */
    public static String selectPrinterTopic(double x, double y) {
        if (x >= -1 && x <= 1.5) {
            if (y >= 1 && y <= 2.6) {
                return TOPIC_B;
            } else if (y > 2.6 && y <= 4.2) {
                return TOPIC_A;
            }
        } else if (x >= 5.8 && x <= 7.3 && y > 2.6 && y <= 4.2) {
            return TOPIC_C;
        }

        // Position doesn't match any defined zone
        return null;
    }
}