package com.discoverylab.ripple.android.config;


/**
 * Class to hold all values used for accessing the REST web service of the broker.
 */
public final class WSConfig {


    private WSConfig() {
        // No public constructor
    }

    // Default IP and port values
    public static final String DEFAULT_BROKER_IP = "192.168.0.220";//"abcd::222:19FF:FEF8:8FA7";
    public static final String DEFAULT_MQTT_PORT = "1883";
    public static final String DEFAULT_REST_PORT = "9113";
    // URL of Broker's REST API
    public static String ROOT_URL = "http://[" + DEFAULT_BROKER_IP + "]:" + DEFAULT_REST_PORT;

}
