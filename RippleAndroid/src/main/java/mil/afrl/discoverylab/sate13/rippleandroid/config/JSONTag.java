package mil.afrl.discoverylab.sate13.rippleandroid.config;

/**
 * Created by burt on 7/3/13.
 */
public final class JSONTag {

    private JSONTag() {
        // No public constructor
    }

    // PatientList WS tags
    public static final String PATIENTS = "Patients";
    public static final String PATIENT = "Patient";
    public static final String PATIENT_PID = "pid";
    public static final String PATIENT_FIRST_NAME = "first_name";
    public static final String PATIENT_LAST_NAME = "last_name";
    public static final String PATIENT_SSN = "ssn";
    public static final String PATIENT_BIRTHDAY = "birthday";
    public static final String PATIENT_SEX = "sex";
    public static final String PATIENT_NBC_CONTAMINATION = "nbc_contamination";
    public static final String PATIENT_TYPE = "type";

    // VitalsList WS tags
    public static final String VITALS = "Vitals";
    public static final String VITAL = "Vital";
    public static final String VITALS_VID = "vid";
    public static final String VITALS_IP_ADDR = "ip_addr";
    public static final String VITALS_TIMESTAMP = "timestamp";
    public static final String VITALS_SENSOR_TYPE = "sensor_type";
    public static final String VITALS_VALUE_TYPE = "value_type";
    public static final String VITALS_VALUE = "value";

    // InterventionList WS tags
    public static final String INTERVENTIONS = "Interventions";
    public static final String INTERVENTION = "intervention";
    public static final String INTERVENTION_IID = "iid";
    public static final String INTERVENTION_PID = "pid";
    public static final String INTERVENTION_TYPE = "type";
    public static final String INTERVENTION_DETAILS = "details";

    // TraumaList WS tags
    public static final String TRAUMAS = "Traumas";
    public static final String TRAUMA = "Trauma";
    public static final String TRAUMA_TID = "tid";
    public static final String TRAUMA_PID = "pid";
    public static final String TRAUMA_LOCATION = "location";
    public static final String TRAUMA_TYPE = "type";
    public static final String TRAUMA_STATUS = "status";

}
