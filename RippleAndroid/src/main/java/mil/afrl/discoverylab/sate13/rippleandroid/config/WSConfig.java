package mil.afrl.discoverylab.sate13.rippleandroid.config;

public final class WSConfig {



    private WSConfig() {
        // No public constructor
    }

    //public static final String ROOT_URL = "http://24.123.68.123/ripple/www/"; // Delta
    //public static final String ROOT_URL = "http://10.0.3.181:8080/RippleBroker/"; // Ripple
    //public static final String ROOT_URL = "http://192.168.0.200:8080/RippleBroker/"; // Ripple
    public static final int BROKER_PORT = 8080;
    public static final String BROKER_ROOT = "RippleBroker";
    public static final String DEFAULT_IP = "abcd::222:19FF:FEF8:8FA7";
    public static String ROOT_URL = "http://["+DEFAULT_IP+"]:"+BROKER_PORT+"/"+BROKER_ROOT+"/"; // James
    public static String WS_QUERY_URL = ROOT_URL + "Query";
    public static final String WS_VITAL_LIST_URL_JSON = ROOT_URL + "Query?QueryType=VITAL&pid=1&vidi=0&limit=50";
    //public static final String WS_PATIENT_LIST_URL_JSON = ROOT_URL + "patientListJson.php";
    public static final String WS_PATIENT_LIST_URL_JSON = ROOT_URL + "Query?QueryType=PATIENT";
    //public static final String WS_VITAL_LIST_URL_JSON = ROOT_URL + "vitalsListJson.php";
    public static final String UDP_VITALS_STREAM_HOST = "192.168.0.101";

    public static final String WS_PROPERTY_QUERYTYPE = "QueryType";

    public static final String WS_VITAL_PROPERTY_PID = "pid";
    public static final String WS_VITAL_PROPERTY_VIDI = "vidi";
    public static final String WS_VITAL_PROPERTY_ROWLIMIT = "rowlimit";
    public static final String WS_VITAL_PROPERTY_TIMELIMIT = "timelimit";

    public static final String WS_SUBSCRIPTION_PROPERTY_PID = "pid";
    public static final String WS_SUBSCRIPTION_PROPERTY_ACTION = "action";
    public static final String WS_SUBSCRIPTION_PROPERTY_PORT = "port";

    public static final int WS_VITAL_PARAM_DEFAULT_ROWLIMIT = 100;
    public static final int WS_VITAL_PARAM_DEFAULT_TIMELIMIT = 5000;
    public static final int SERVER_TCP_PORT = 0;
    public static final int UDP_VITALS_STREAM_PORT = 4444;

}
