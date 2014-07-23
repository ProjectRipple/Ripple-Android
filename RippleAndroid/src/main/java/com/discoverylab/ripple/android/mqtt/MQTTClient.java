package com.discoverylab.ripple.android.mqtt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * MQTTClient handles communication with a MQTT server
 */
public class MQTTClient implements MqttCallback {
    // Log tag
    private static final String TAG = "MQTTClient";
    // True to enable debug messages
    private static final boolean DEBUG = false;

    // Broker information
    private String brokerHostName;
    private String brokerPort;
    // Persistence object, null means default persistence settings are used.
    private static final MqttClientPersistence MQTT_PERSISTENCE = null;
    // Don't need to remember any state between connections, so can use clean start
    private static final boolean MQTT_CLEAN_START = true;
    // Keep alive interval for connection in seconds(default for library is 60 seconds)
    private static final short MQTT_KEEP_ALIVE = 5 * 60;
    // Set quality of services to 0 (at most once delivery).
    // However, this means that some messages might get lost (delivery is not guaranteed)
    private int[] mqttQoSs = {0};
    private int mqttQoS = 0;
    // Broker should not retain any messages
    private static final boolean MQTT_RETAINED_PUBLISH = false;
    // MQTT client id base
    private static final String MQTT_CLIENT_ID_BASE = "ripple";
    // Time to wait for any blocking action to complete
    private static final long TIME_TO_WAIT_IN_MILLIS = 30000;
    // Reference to Client object
    private MqttClient mqttClient;

    // Device ID used for MQTT connection
    private String deviceId;
    // Handler for callback to service
    private final Handler handler;

    // Message what constants
    public final static int MQTTCLIENT_CONNECTION_ESTABLISHED = 8881;
    public final static int MQTTCLIENT_CONNECTION_TERMINATED = 8882;
    public final static int MQTTCLIENT_CONNECTION_LOST = 8883;
    public final static int MQTTCLIENT_MESSAGE_ARRIVED = 8884;
    // Keys for message bundle
    public final static String MESSAGE_TOPIC = "MESSAGE_TOPIC";
    public final static String MESSSAGE_PAYLOAD = "MESSAGE_PAYLOAD";

    /**
     * @param brokerHostName Host name of broker
     * @param brokerPort     Port of MQTT
     * @param deviceId       ID of this client
     * @param handler        Handler for calling service
     * @throws MqttException Problem creating client object
     */
    public MQTTClient(String brokerHostName, String brokerPort, String deviceId, final Handler handler) throws MqttException {
        this.brokerHostName = brokerHostName;
        this.brokerPort = brokerPort;
        this.deviceId = deviceId;
        this.handler = handler;

        // connection string
        final String mqttConnSpec = "tcp://" + brokerHostName + ":" + brokerPort;
        // construct client ID
        final String clientID = MQTT_CLIENT_ID_BASE + "/" + deviceId;

        // create the MQTTClient object
        this.mqttClient = new MqttClient(mqttConnSpec, clientID, MQTT_PERSISTENCE);

        // set time to wait
        this.mqttClient.setTimeToWait(TIME_TO_WAIT_IN_MILLIS);
        // set callback to this
        this.mqttClient.setCallback(this);
    }

    /**
     * Connect to MQTT server specified in constructor. This is a blocking call and
     * will not return until the connection is completed or fails.
     *
     * @throws MqttException for non security related problems including communication errors
     */
    public void connect() throws MqttException {

        if (this.mqttClient.isConnected()) {
            // already connected
            handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_ESTABLISHED);
            // return or the call to connect with throw an exception
            return;
        }
        // setup connection options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(MQTT_CLEAN_START);
        options.setKeepAliveInterval(MQTT_KEEP_ALIVE);

        // attempt connect
        this.mqttClient.connect(options);
        // notify handler of success
        handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_ESTABLISHED);
    }

    /**
     * Disconnects from the server. Will wait for up to 30 seconds to
     * allow current work to finish disconnecting.
     */
    public void disconnect() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            // problem disconnecting (client is already disconnecting or disconnected)
            e.printStackTrace();
            if (DEBUG) {
                Log.d(TAG, "Failed to disconnect the client: " + e.getMessage());
            }
        }
        // notify handler
        handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_TERMINATED);
    }

    /**
     * Subscribe to the topic specified.
     *
     * @param topicName Topic to subscribe to
     * @throws MqttException if there was an error registering the subscription
     */
    public void subscribeToTopic(String topicName) throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {

            handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_LOST);
            if (DEBUG) {
                Log.d(TAG, "Connection error: no connection");
            }
        } else {
            String[] topic = {topicName};
            mqttClient.subscribe(topic, mqttQoSs);
        }
    }

    /**
     * Requests the server unsubscribe the client from a topic
     *
     * @param topicName Topic to unsubscribe from
     * @throws MqttException if there was an error unregistering the subscription
     */
    public void unsubscribeFromTopic(String topicName) throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_LOST);
            if (DEBUG) {
                Log.d(TAG, "Connection error: no connnection");
            }
        } else {
            String[] topic = {topicName};
            mqttClient.unsubscribe(topic);
        }
    }

    /**
     * Public message to a topic on the server and returns once the message has been delivered.
     *
     * @param topicName Topic to publish to
     * @param message   Message to send
     * @throws MqttException errors encountered while publishing the message. For example, if the client is not connected.
     */
    public void publishToTopic(String topicName, String message) throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_LOST);
            if (DEBUG) {
                Log.d(TAG, "Connection error: no connection");
            }
        } else {
            mqttClient.publish(topicName, message.getBytes(), mqttQoS, MQTT_RETAINED_PUBLISH);
        }
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    public String getBrokerHostName() {
        return brokerHostName;
    }

    public String getBrokerPort() {
        return brokerPort;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        handler.sendEmptyMessage(MQTTCLIENT_CONNECTION_LOST);
        if (DEBUG) {
            Log.d(TAG, "Detected loss of connection.");
        }
    }

    @Override
    public void messageArrived(String topicName, MqttMessage mqttMessage) throws Exception {
        String s = new String(mqttMessage.getPayload());
        if (DEBUG) {
            Log.d(TAG, "Got message: " + s);
        }
        Message msg = handler.obtainMessage(MQTTCLIENT_MESSAGE_ARRIVED);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_TOPIC, topicName);
        bundle.putString(MESSSAGE_PAYLOAD, s);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // Do nothing for now
    }
}
