package mil.afrl.discoverylab.sate13.rippleandroid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;

/**
 * Common stores all application-global constants
 * <p/>
 * Created by Matt on 6/19/13.
 */
public final class Common {


    private Common() {
        // Empty
    }

    // Json Formatting
    public static final Gson GSON = new GsonBuilder().setDateFormat(Common.DATE_TIME_FORMAT).create();

    // Logging
    public static final String LOG_TAG = "RIPPLE_LOG_TAG";

    // Parsing
    public static final String CSV_DELIMITER = ";";
    public static final int ECG_CSV = R.raw.model;

    // Package Name
    public static final String PACKAGE_NAMESPACE = "mil.afrl.discoverylab.sate13.rippleandroid";

    // Date Formatting
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat SIMPLE_DATETIME_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT);

    // multicast constants
    public static final String MCAST_GROUP = "ff02::1";
    public static final int MCAST_PORT = 1222;
    public static final String MCAST_INTERFACE = "wlan0";

    // Messaging What Constants
    public static final int RIPPLE_MSG_MCAST = 22;
    public static final int RIPPLE_MSG_VITALS_STREAM = 44;
    public static final int RIPPLE_MSG_VITALS_TEMPERATURE = 33;
    public static final int RIPPLE_MSG_VITALS_PULSE = 55;
    public static final int RIPPLE_MSG_VITALS_BLOOD_OX = 66;
    public static final int RIPPLE_MSG_BITMAP = 99;
    public static final int RIPPLE_MSG_RECORD = 77;
    public static final int RIPPLE_MSG_ECG_STREAM = 88;

    // Fudge Values
    public static final double SIM_BASELINE_GUESS = 1858.077626;

    // MQTT topics
    public static final String MQTT_TOPIC_VITALPROP = "P_Stats/vitalprop";
    public static final String MQTT_TOPIC_ID_STRING = "[PID]";
    public static final String MQTT_TOPIC_ECG_STREAM = "P_Stream/"+MQTT_TOPIC_ID_STRING+"/ecg";
    public static final String MQTT_TOPIC_MATCH_ECG_STREAM = "P_Stream/.*/ecg";

    // Record topic payload values
    public static final String RECORD_SOURCE = "src";
    public static final String RECORD_SEQUENCE = "seq";
    public static final String RECORD_AGE = "age";
    public static final String RECORD_HOPS = "hops";
    public static final String RECORD_HEART_RATE = "hr";
    public static final String RECORD_BLOOD_OX = "sp02";
    public static final String RECORD_RESP_PER_MIN = "resp";
    public static final String RECORD_TEMPERATURE = "temp";
    public static final String RECORD_STATUS = "stat";

    //ECG Vitals Streaming
    //public static final int

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