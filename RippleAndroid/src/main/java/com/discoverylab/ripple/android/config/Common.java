package com.discoverylab.ripple.android.config;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Common stores all application-global constants
 * <p/>
 * Created by Matt on 6/19/13.
 */
public final class Common {


    private Common() {
        // Empty
    }

    // responder id for this device
    public static String RESPONDER_ID = "";
    // key for responder id preference
    public static final String RESPONDER_ID_PREF = "ResponderIdPref";

    // Saved broker location
    public static LatLng brokerLatLng = new LatLng(0.0, 0.0);
    public static double brokerAltitude = 0.0;

    // Save responder location
    public static LatLng responderLatLng = new LatLng(0.0, 0.0);
    public static double responderAltitude = 0.0;

    // Json Formatting
    public static final Gson GSON = new GsonBuilder().setDateFormat(Common.DATE_TIME_FORMAT).create();

    // Logging
    public static final String LOG_TAG = "RIPPLE_LOG_TAG";

    // Parsing
    public static final String CSV_DELIMITER = ";";

    // Package Name
    public static final String PACKAGE_NAMESPACE = "com.discoverylab.ripple.android";

    // Date Formatting
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // Messaging What Constants
    public static final int RIPPLE_MSG_RECORD = 77;
    public static final int RIPPLE_MSG_ECG_STREAM = 88;
    public static final int RIPPLE_MSG_SELECT_PATIENT = 11;

    // MQTT topics
    public static final String MQTT_TOPIC_ID_STRING = "[PID]";
    public static final String MQTT_TOPIC_WILDCARD_SINGLE_LEVEL = "+";
    public static final String MQTT_TOPIC_WILDCARD_SUBTREE = "#";

    public static final String MQTT_TOPIC_VITALPROP = "P_Stats/vitalprop";

    public static final String MQTT_TOPIC_VITALCAST = "P_Stats/" + MQTT_TOPIC_ID_STRING + "/vitalcast";
    public static final String MQTT_TOPIC_MATCH_VITALCAST = "P_Stats/.*/vitalcast";

    public static final String MQTT_TOPIC_ECG_STREAM = "P_Stream/" + MQTT_TOPIC_ID_STRING + "/ecg";
    public static final String MQTT_TOPIC_MATCH_ECG_STREAM = "P_Stream/.*/ecg";

    public static final String MQTT_TOPIC_BROKER_ID_STRING = "[CID]";
    public static final String MQTT_TOPIC_BROKER_PING = "C_Status/" + MQTT_TOPIC_BROKER_ID_STRING + "/ping";
    public static final String MQTT_TOPIC_MATCH_BROKER_PING = "C_Status/.*/ping";

    public static final String MQTT_TOPIC_RESPONDER_ID_STRING = "[RID]";
    public static final String MQTT_TOPIC_RESPONDER_PING = "R_Status/" + MQTT_TOPIC_RESPONDER_ID_STRING + "/ping";


    public enum TRIAGE_COLORS {
        UNKNOWN, GREEN, YELLOW, RED, BLACK;

        private int color;

        public void setColor(int color){
            this.color = color;
        }

        public int getColor(){
            return this.color;
        }
    }


    // Vital constants
    public enum VITAL_TYPES {
        VITAL_PULSE(0), VITAL_ECG(1), VITAL_TEMPERATURE(2), VITAL_BLOOD_OX(3);
        private final int id;

        VITAL_TYPES(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    public enum NW_MSG_TYPES {
        UDP_BANNER_VITAL(true, "vitals"), TCP_STREAM_VITAL(false, "vitals"), UNKNOWN(false, "");

        private boolean udp;
        private String tag;

        NW_MSG_TYPES(boolean udp, String tag) {
            this.udp = udp;
            this.tag = tag;
        }

        public String getTag() {
            return this.tag;
        }

        public static NW_MSG_TYPES getForTag(boolean udp, String tag) {
            NW_MSG_TYPES type = NW_MSG_TYPES.UNKNOWN;
            if (udp) {
                if (tag.equals("vitals")) {
                    type = NW_MSG_TYPES.UDP_BANNER_VITAL;
                }
            } else {
                if (tag.equals("vitals")) {
                    type = NW_MSG_TYPES.TCP_STREAM_VITAL;
                }
            }
            return type;
        }
    }
}