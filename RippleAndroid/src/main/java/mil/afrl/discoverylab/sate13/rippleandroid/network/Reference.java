package mil.afrl.discoverylab.sate13.rippleandroid.network;

import java.text.SimpleDateFormat;

/**
 * Reference class for static values and enumerations
 * @author james
 */
public class Reference {

    public enum ANALYTICS_RESPONSE{
        NO_ATTENTION_NEEDED, ANOMALY_DETECTED, ONGOING_ASSESSMENT, LOW_PRIORITY, MEDIUM_PRIORITY, HIGH_PRIORITY, IMMEDIATE_INTERVENTION;
    }

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat datetimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
    // database table structures
    public static final String PATIENT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS patient ("
            + " pid int(11) NOT NULL AUTO_INCREMENT,"
            + " ip_addr varchar(42) NOT NULL,"
            + " first_name varchar(20) DEFAULT NULL,"
            + " last_name varchar(20) DEFAULT NULL,"
            + " ssn varchar(11) DEFAULT NULL,"
            + " dob datetime DEFAULT NULL,"
            + " sex varchar(6) DEFAULT NULL,"
            + " nbc_contamination int(1) DEFAULT NULL,"
            + " type varchar(10) DEFAULT NULL,"
            + " PRIMARY KEY (pid),"
            + " UNIQUE KEY ip_addr (ip_addr)"
            + ");";
    public static final String VITAL_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS vital ("
            + " vid int(11) NOT NULL AUTO_INCREMENT,"
            + " pid int(11) NOT NULL,"
            + " server_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " sensor_timestamp bigint(20) unsigned DEFAULT NULL,"
            + " sensor_type varchar(10) DEFAULT NULL,"
            + " value_type varchar(15) DEFAULT NULL,"
            + " value int(11) DEFAULT NULL,"
            + " PRIMARY KEY (vid,pid),"
            + " FOREIGN KEY (pid) REFERENCES patient (pid)"
            + ");";
    public static final String VITAL_BLOB_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS vital_blob ("
            + " bid int(11) NOT NULL AUTO_INCREMENT,"
            + " pid int(11) NOT NULL,"
            + " server_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " sensor_timestamp bigint(20) unsigned DEFAULT NULL,"
            + " sensor_type int unsigned DEFAULT NULL,"
            + " value_type int unsigned DEFAULT NULL,"
            + " num_samples int unsigned,"
            + " period_ms int unsigned,"
            + " value BLOB DEFAULT NULL,"
            + " PRIMARY KEY (bid,pid),"
            + " FOREIGN KEY (pid) REFERENCES patient (pid)"
            + ") ENGINE=InnoDB;";
}