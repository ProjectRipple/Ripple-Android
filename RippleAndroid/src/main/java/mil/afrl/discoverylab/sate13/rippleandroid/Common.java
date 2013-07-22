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

    public static final String LOG_TAG       = "RIPPLE";
    public static final String CSV_DELIMITER = ";";
    public static final String PACKAGE_NAMESPACE = "mil.afrl.discoverylab.sate13.rippleandroid";
    public static final int    ECG_CSV       = R.raw.model;
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat datetimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

    public enum NW_MSG_TYPES {
        UDP_BANNER(0), TCP_STREAM(1);

        private final int val;

        NW_MSG_TYPES(int val) {
            this.val = val;
        }

        public int getVal() {
            return this.val;
        }
    };

}