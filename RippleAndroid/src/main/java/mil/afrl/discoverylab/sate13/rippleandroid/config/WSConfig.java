package mil.afrl.discoverylab.sate13.rippleandroid.config;

/**
 * Created by burt on 7/3/13.
 */
public final class WSConfig {

    private WSConfig() {
        // No public constructor
    }

    //public static final String ROOT_URL = "http://24.123.68.123/ripple/www/"; // Delta
    //public static final String ROOT_URL = "http://10.0.3.181:8080/RippleBroker/"; // Ripple
    public static final String ROOT_URL = "http://localhost:8080/RippleBroker/"; // Ripple
    public static final String WS_VITAL_LIST_URL_JSON = ROOT_URL + "Query?QueryType=VITAL&pid=1&vidi=0&limit=50";
    //public static final String WS_PATIENT_LIST_URL_JSON = ROOT_URL + "patientListJson.php";
    public static final String WS_PATIENT_LIST_URL_JSON = ROOT_URL + "Query?QueryType=PATIENT";
    //public static final String WS_VITAL_LIST_URL_JSON = ROOT_URL + "vitalsListJson.php";

    public static final int SERVER_TCP_PORT = 0;

}
