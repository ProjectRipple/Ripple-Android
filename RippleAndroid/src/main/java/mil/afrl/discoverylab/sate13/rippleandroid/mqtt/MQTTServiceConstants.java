package mil.afrl.discoverylab.sate13.rippleandroid.mqtt;

/**
 * Constants used by the MQTT service
 */
public final class MQTTServiceConstants {
    public final static int MSG_REGISTER_CLIENT = 9991;
    public final static int MSG_UNREGISTER_CLIENT = 9992;
    public final static int MSG_STOP_SERVICE = 9993;
    public final static int MSG_SUBSCRIBE = 9994;
    public final static int MSG_UNSUBSCRIBE = 9995;

    public final static String MQTT_BROKER_IP = "BROKER_IP_ADDRESS";
    public final static String MQTT_BROKER_PORT= "BROKER_PORT_NUMBER";
    public final static String MQTT_TOPIC = "TOPIC";

    public final static int MSG_CONNECTED = 1002;
    public final static int MSG_CANT_CONNECT = 1003;
    public final static int MSG_PUBLISHED_MESSAGE = 1001;
}
