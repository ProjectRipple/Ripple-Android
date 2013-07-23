package mil.afrl.discoverylab.sate13.rippleandroid;

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

    public static final String LOG_TAG = "RIPPLE";
    public static final String CSV_DELIMITER = ";";
    public static final String PACKAGE_NAMESPACE = "mil.afrl.discoverylab.sate13.rippleandroid";
    public static final int ECG_CSV = R.raw.model;
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // multicast constants
    public static final String MCAST_GROUP = "ff02::1";
    public static final int MCAST_PORT = 1222;
    public static final String MCAST_INTERFACE = "wlan0";
    // message constants
    public static final int RIPPLE_MSG_MCAST = 22;

    public static final SimpleDateFormat SIMPLE_DATETIME_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT);

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

    ;

}