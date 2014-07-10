package com.discoverylab.ripple.android.mqtt;

/**
 * Constants used by the MQTT service
 */
public final class MQTTServiceConstants {
    // Message constants sent to service
    // Register ServiceManager messenger with service
    public final static int MSG_REGISTER_CLIENT = 9991;
    // Unregister ServiceManager messenger from service
    public final static int MSG_UNREGISTER_CLIENT = 9992;
    // Stop the service
    public final static int MSG_STOP_SERVICE = 9993;
    // Subscribe to specified topic (in message data bundle)
    public final static int MSG_SUBSCRIBE = 9994;
    // Unsubscribe from the specified topic (in message data bundle)
    public final static int MSG_UNSUBSCRIBE = 9995;
    // Publish message to the specified topic (both topic and message in data bundle)
    public final static int MSG_PUBLISH_TO_TOPIC = 9996;
    // Request the MQTT connection status from the service
    public static final int MSG_GET_CONNECTION_STATUS = 9997;

    // Message fields to/from service
    public final static String MQTT_BROKER_IP = "BROKER_IP_ADDRESS";
    public final static String MQTT_BROKER_PORT = "BROKER_PORT_NUMBER";
    public final static String MQTT_TOPIC = "MQTT_TOPIC";
    public final static String MQTT_MESSAGE = "MQTT_MESSAGE";
    public static final String MQTT_CONNECTION_STATUS = "CONNECTION_STATUS";

    // Service action strings
    public final static String ACTION_START = "MQTT_START";
    public final static String ACTION_STOP = "MQTT_STOP";
    public final static String ACTION_RECONNECT = "MQTT_RECONNECT";

    // Message constants sent from service
    // a message has arrived from the MQTT client
    public final static int MSG_PUBLISHED_MESSAGE = 1001;
    // the MQTT client was successfully connected
    public final static int MSG_CONNECTED = 1002;
    // the MQTT client cannot connect to the server at this time
    public final static int MSG_CANT_CONNECT = 1003;
    // the MQTT client has disconnected from the server
    public final static int MSG_DISCONNECTED = 1004;
    // the MQTT client is attempting to reconnect to the server
    public final static int MSG_RECONNECTING = 1005;
    // the MQTT client is waiting for a network to connect
    public static final int MSG_NO_NETWORK = 1006;
    // Service reporting connection status of the MQTT client
    public static final int MSG_CONNECTION_STATUS = 1007;
}
