package mil.afrl.discoverylab.sate13.rippleandroid.config;

public final class WSConfig {

    private WSConfig() {
        // No public constructor
    }

    //public static final String ROOT_URL = "http://24.123.68.123/ripple/www/"; // Delta
    //public static final String ROOT_URL = "http://10.0.3.181:8080/RippleBroker/"; // Ripple
    public static final String ROOT_URL = "http://192.168.0.200:8080/RippleBroker/"; // Ripple
    public static final String WS_QUERY_URL = ROOT_URL + "Query";
    public static final String WS_VITAL_LIST_URL_JSON = ROOT_URL + "Query?QueryType=VITAL&pid=1&vidi=0&limit=50";
    //public static final String WS_PATIENT_LIST_URL_JSON = ROOT_URL + "patientListJson.php";
    public static final String WS_PATIENT_LIST_URL_JSON = ROOT_URL + "Query?QueryType=PATIENT";
    //public static final String WS_VITAL_LIST_URL_JSON = ROOT_URL + "vitalsListJson.php";

    public static final String WS_PROPERTY_QUERYTYPE = "QueryType";
    public static final String WS_VITAL_PROPERTY_PID = "pid";
    public static final String WS_VITAL_PROPERTY_VIDI = "vidi";
    public static final String WS_VITAL_PROPERTY_ROWLIMIT = "rowlimit";
    public static final String WS_VITAL_PROPERTY_TIMELIMIT = "timelimit";

    public static final int WS_VITAL_PARAM_DEFAULT_ROWLIMIT = 10;
    public static final int WS_VITAL_PARAM_DEFAULT_TIMELIMIT = 5000;
    public static final int SERVER_TCP_PORT = 0;

}
