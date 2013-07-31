package mil.afrl.discoverylab.sate13.ripple.data.model;

import android.content.ContentValues;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent.db_patient;

/**
 * Created by burt on 7/3/13.
 */
public final class Patient {

    public Integer pid;
    public String ip_addr;
    public String first_name;
    public String last_name;
    public String ssn;
    public String dob;
    public String sex;
    public String nbc_contamination;
    public String type;

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(db_patient.Columns.PID.getName(), pid);
        cv.put(db_patient.Columns.IP_ADDR.getName(), ip_addr);
        cv.put(db_patient.Columns.FIRST_NAME.getName(), first_name);
        cv.put(db_patient.Columns.LAST_NAME.getName(), last_name);
        cv.put(db_patient.Columns.SSN.getName(), ssn);
        cv.put(db_patient.Columns.DOB.getName(), dob);
        cv.put(db_patient.Columns.NBC_CONTAMINATION.getName(), nbc_contamination);
        cv.put(db_patient.Columns.TYPE.getName(), type);
        return cv;
    }
}
