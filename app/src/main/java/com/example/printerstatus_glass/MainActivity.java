package com.example.printerstatus_glass;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Main activity class that handles printer status monitoring and display.
 * Implements MQTT callback for real-time data updates.
 */
public class MainActivity extends AppCompatActivity implements MqttHandler.MessageCallback {
    // MQTT connection configuration
    private static final String BROKER_URL = "tcp://control.server.de:1883";
    private static final String CLIENT_ID = "ID";
    private static final String PRINTER_TOPIC = "sf/printer/a";
    private static final String UWB_TOPIC = "sf/UWB/uwb-a";

    // UI components and data handlers
    private MqttHandler mqttHandler;
    private TextView tvStatus;
    private DataHolder dataHolder;

    // Current printer topic and last update timestamp
    private String currentPrinterTopic = PRINTER_TOPIC; // default topic
    private long lastPrinterUpdateTime = 0;

    // latency measuring variables (test)
    private long mqttReceiveTime = 0;
    private long uiUpdateStartTime = 0;
    private int messageCount = 0;
    private long totalProcessingLatency = 0;
    private long totalUiUpdateLatency = 0;

    /**
     * Called when the activity is first created.
     * Initializes UI components, MQTT connection, and data handlers.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize data holder singleton
        dataHolder = DataHolder.getInstance();

        // Set up main content view with scrolling
        FrameLayout bodyLayout = findViewById(R.id.body_layout);
        tvStatus = new TextView(this);
        tvStatus.setTextSize(20);
        tvStatus.setPadding(20, 20, 20, 20);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        scrollView.addView(tvStatus);
        bodyLayout.addView(scrollView);

        // Initialize MQTT client and connect
        mqttHandler = new MqttHandler();
        mqttHandler.setMessageCallback(this);
        mqttHandler.connect(BROKER_URL, CLIENT_ID);

        // Subscribe to default topics
        mqttHandler.subscribe(PRINTER_TOPIC);
        mqttHandler.subscribe(UWB_TOPIC);
    }

    /**
     * Callback method invoked when an MQTT message is received.
     * Processes the message based on its topic and updates the UI accordingly.
     * @param topic The MQTT topic on which the message was received
     * @param message The content of the received message
     */
    @Override
    public void onMessageReceived(String topic, String message) {
        mqttReceiveTime = System.currentTimeMillis(); // record the message arriving time (test)
        runOnUiThread(() -> {
            uiUpdateStartTime = System.currentTimeMillis(); // record the UI thread starting time (test)
            long messageProcessingLatency = uiUpdateStartTime - mqttReceiveTime;

            try {
                if (topic.equals(currentPrinterTopic)) {
                    // Handle printer status updates
                    Map<String, String> newData = DataParser.parsePrinterData(message);
                    dataHolder.updatePrinterData(newData);
                    lastPrinterUpdateTime = System.currentTimeMillis();
                    updateDisplay();
                } else if (topic.equals(UWB_TOPIC)) {
                    // Handle UWB position updates
                    Map<String, String> newData = DataParser.parseUwbData(message);
                    dataHolder.updateUwbData(newData);

                    // Update printer topic based on UWB coordinates
                    double x = safeParseDouble(newData.get("positionX"));
                    double y = safeParseDouble(newData.get("positionY"));
                    String newTopic = PrinterTopicSelector.selectPrinterTopic(x, y);

                    if (newTopic != null && !newTopic.equals(currentPrinterTopic)) {
                        currentPrinterTopic = newTopic;
                        mqttHandler.subscribe(currentPrinterTopic);
                        Toast.makeText(this, "Change Topic: " + newTopic, Toast.LENGTH_SHORT).show();
                    }

                    updateDisplay();
                }

                messageCount++;
                totalProcessingLatency += messageProcessingLatency;
                long uiUpdateLatency = System.currentTimeMillis() - uiUpdateStartTime;
                totalUiUpdateLatency += uiUpdateLatency;

                // Output statistics every 10 messages
                if (messageCount % 10 == 0) {
                    logLatencyStats();
                }


            } catch (Exception e) {
                Toast.makeText(this, "Message Processing Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the UI with current printer and position data.
     * Formats and displays all available information in a structured way.
     */
    private void updateDisplay() {
        try {
            Map<String, String> printerData = dataHolder.getPrinterData();
            Map<String, String> uwbData = dataHolder.getUwbData();

            // Parse UWB position data with default value 0
            double positionX = safeParseDouble(uwbData.get("positionX"));
            double positionY = safeParseDouble(uwbData.get("positionY"));

            // Build status display text
            StringBuilder statusText = new StringBuilder();

            // Configure text view appearance
            tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            tvStatus.setGravity(Gravity.END);

            // Create status header
            String title = "====== Printer " + printerData.get("printer_name").toUpperCase() + " Status ======\n";
            statusText.append(title);

            // Append printer status information
            appendIfExists(statusText, "Bed Temperature",printerData.get("bed_temperature_current") + "°C / " + printerData.get("bed_temperature_target") + "°C");
            appendIfExists(statusText, "Tool Temperature",printerData.get("tool_temperature_current") + "°C / " + printerData.get("tool_temperature_target") + "°C");
            appendIfExists(statusText, "Printer State", printerData.get("state"));
            appendIfExists(statusText,"Completion",String.format(Locale.US, "%.1f", safeParseDouble(printerData.get("completion"))) + "%" + "   Time Left: " + printerData.get("print_time_left")+ "s");
            appendIfExists(statusText, "Job Name", printerData.get("job_name"));

            // Update main status display
            tvStatus.setText(statusText.toString());

            // Update footer with position information
            TextView footerTextView = findViewById(R.id.footer);
            String currentPosition = "Current Position: " + positionX + ", " + positionY ;
            footerTextView.setText(currentPosition);

            // Update timestamp if data is available
            TextView timestampTextView = findViewById(R.id.timestamp);
            if (lastPrinterUpdateTime > 0) {
                String updateTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        .format(new Date(lastPrinterUpdateTime));
                timestampTextView.setText("Update Time: " + updateTime);
            } else {
                statusText.append("Printer Update Time: No Data\n");
            }

        } catch (Exception e) {
            Toast.makeText(this, "View Update Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Safely parses a string to double with default value 0
     * @param value The string to parse
     * @return Parsed double value or 0 if parsing fails
     */
    private double safeParseDouble(String value) {
        try {
            return Double.parseDouble(Objects.requireNonNullElse(value, "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Appends a labeled value to the string builder if the value exists
     * @param builder The StringBuilder to append to
     * @param label The label for the value
     * @param value The value to append
     */
    private void appendIfExists(StringBuilder builder, String label, String value) {
        if (value != null && !value.isEmpty()) {
            builder.append(label).append(": ").append(value).append("\n");
        }
    }

    /**
     * Called when the pointer capture state changes.
     * @param hasCapture True if the pointer capture is enabled
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    /**
     * Logs latency statistics for performance monitoring.
     * Calculates and displays average processing times.
     */
    private void logLatencyStats() {
        double avgProcessing = (double)totalProcessingLatency / messageCount;
        double avgUiUpdate = (double)totalUiUpdateLatency / messageCount;

        String stats = String.format(Locale.US,
                "Latency Stats (last %d msgs):\n" +
                        "MQTT->UI Thread: %.1fms\n" +
                        "UI Update: %.1fms",
                messageCount, avgProcessing, avgUiUpdate);

        Log.d("LatencyMetrics", stats);
    }

}