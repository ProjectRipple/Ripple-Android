package com.discoverylab.ripple.android.config;

/**
 * Class to hold all keys for JSON objects used by this application
 * Created by burt on 7/3/13.
 */
public final class JSONTag {

    private JSONTag() {
        // No public constructor
    }

    // Record topic payload values
    public static final String RECORD_SOURCE = "src";
    public static final String RECORD_IP_ADDRESS = "ip";
    public static final String RECORD_SEQUENCE = "seq";
    public static final String RECORD_AGE = "age";
    public static final String RECORD_HOPS = "hops";
    public static final String RECORD_HEART_RATE = "hr";
    public static final String RECORD_BLOOD_OX = "sp02";
    public static final String RECORD_RESP_PER_MIN = "resp";
    public static final String RECORD_TEMPERATURE = "temp";
    public static final String RECORD_STATUS = "stat";

    // Broker ping keys

    public static final String BROKER_PING_PATIENTS = "patients";
    public static final String BROKER_PING_PATIENTS_ID = "id";
    public static final String BROKER_PING_PATIENTS_IP = "ip";
    public static final String BROKER_PING_PATIENTS_LAST_SEEN = "last_seen";

    // Patient information tags
    public static final String PATIENT_INFO_NAME = "name";
    public static final String PATIENT_INFO_AGE = "age";
    public static final String PATIENT_INFO_SEX = "sex";
    public static final String PATIENT_INFO_NBC = "nbc";
    public static final String PATIENT_INFO_TRIAGE = "triage";
    public static final String PATIENT_INFO_STATUS = "status";
    public static final String PATIENT_INFO_REQUEST_LAST_UPDATED = "last_updated";

    // Patient Note tags
    public static final String NOTE_BODY_PART = "body_part";
    public static final String NOTE_CONTENTS = "contents";
    public static final String NOTE_ITEM_TYPE = "type";
    public static final String NOTE_ITEM_MESSAGE = "msg";
    public static final String NOTE_ITEM_FILE = "file";



    // More general tags
    public static final String RESPONDER_ID = "rid";
    public static final String PATIENT_ID = "pid";
    public static final String BROKER_ID = "cid";
    public static final String NOTE_ID = "nid";
    public static final String DATE = "date";
    public static final String LOCATION = "location";
    public static final String LOCATION_LAT = "lat";
    public static final String LOCATION_LNG = "lng";
    public static final String LOCATION_ALT = "alt";

    // PatientList WS tags
    public static final String PATIENTS = "patients";
    public static final String PATIENT = "patient";
    public static final String PATIENT_PID = "pid";
    public static final String PATIENT_IP_ADDR = "ip_addr";
    public static final String PATIENT_FIRST_NAME = "first_name";
    public static final String PATIENT_LAST_NAME = "last_name";
    public static final String PATIENT_SSN = "ssn";
    public static final String PATIENT_DOB = "dob";
    public static final String PATIENT_SEX = "sex";
    public static final String PATIENT_NBC_CONTAMINATION = "nbc_contamination";
    public static final String PATIENT_TYPE = "type";

    // VitalsList WS tags
    public static final String VITALS = "vitals";
    public static final String VITAL = "vital";
    public static final String VITALS_VID = "vid";
    public static final String VITALS_PID = "pid";
    public static final String VITALS_SERVER_TIMESTAMP = "server_timestamp";
    public static final String VITALS_SENSOR_TIMESTAMP = "sensor_timestamp";
    public static final String VITALS_SENSOR_TYPE = "sensor_type";
    public static final String VITALS_VALUE_TYPE = "value_type";
    public static final String VITALS_VALUE = "value";

    // InterventionList WS tags
    public static final String INTERVENTIONS = "interventions";
    public static final String INTERVENTION = "intervention";
    public static final String INTERVENTION_IID = "iid";
    public static final String INTERVENTION_PID = "pid";
    public static final String INTERVENTION_TYPE = "type";
    public static final String INTERVENTION_DETAILS = "details";

    // TraumaList WS tags
    public static final String TRAUMAS = "traumas";
    public static final String TRAUMA = "trauma";
    public static final String TRAUMA_TID = "tid";
    public static final String TRAUMA_PID = "pid";
    public static final String TRAUMA_LOCATION = "location";
    public static final String TRAUMA_TYPE = "type";
    public static final String TRAUMA_STATUS = "status";

    // Subscription Result Tags
    public static final String SUCCESS = "success";
    public static final String EXCEPTION = "exception";
    public static final String PID_ECHO = "pid_echo";
    public static final String ACTION_ECHO = "action_echo";
    public static final String PORT_ECHO = "port_echo";
}
