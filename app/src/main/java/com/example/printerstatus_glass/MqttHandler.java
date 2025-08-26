package com.example.printerstatus_glass;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import android.util.Log;

/**
 * Handles MQTT client operations including connection, subscription, and message handling.
 * Provides a callback interface for received messages.
 */
public class MqttHandler {
    private MqttClient client;
    private MessageCallback messageCallback;

    /**
     * Callback interface for handling incoming MQTT messages.
     */
    public interface MessageCallback {
        /**
         * Called when a new message is received from subscribed topic.
         * @param topic The topic on which the message was received
         * @param message The message payload as a String
         */
        void onMessageReceived(String topic, String message);
    }

    /**
     * Sets the callback for incoming messages.
     * @param callback The implementation of MessageCallback interface
     */
    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
    }

    /**
     * Establishes connection to MQTT broker.
     * @param brokerUrl The URL of the MQTT broker
     * @param clientId Unique identifier for this client
     */
    public void connect(String brokerUrl, String clientId) {
        try {
            // Use memory persistence for temporary storage of messages
            MemoryPersistence persistence = new MemoryPersistence();
            // Create new MQTT client instance
            client = new MqttClient(brokerUrl, clientId, persistence);

            // Configure connection options
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true); // Start with clean session

            // Set callback handlers for MQTT events
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("MQTT", "Connection lost", cause);
                    // Try reconnection
                    try {
                        client.reconnect();
                    } catch (MqttException e) {
                        Log.e("MQTT", "Reconnect failed", e);
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // Forward message to registered callback
                    if (messageCallback != null) {
                        messageCallback.onMessageReceived(topic, new String(message.getPayload()));
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Message delivered");
                }
            });

            client.connect(connectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribes to specified MQTT topic.
     * @param topic The topic to subscribe to
     */
    public void subscribe(String topic) {
        try {
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}