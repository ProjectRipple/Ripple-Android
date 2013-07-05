package mil.afrl.discoverylab.sate13.rippleandroid.config;

/**
 * Created by burt on 7/3/13.
 */
public final class WSConfig {

    private WSConfig() {
        // No public constructor
    }

    public static final String ROOT_URL = "http://24.123.68.123/ripple/"; // Delta
    public static final String WS_PATIENT_LIST_URL_JSON = ROOT_URL + "patientListJson.php";
    public static final String WS_VITALS_LIST_URL_JSON = ROOT_URL + "vitalsListJson.php";

}
