package mil.afrl.discoverylab.sate13.rippleandroid.data.model;

import android.content.ContentValues;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent.DbPatient;

/**
 * Created by burt on 7/3/13.
 */
public final class Patient {

    public Integer pid;
    public String first_name;
    public String last_name;
    public Integer ssn;
    public String birthday;
    public boolean sex;
    public boolean nbc_contamination;
    public String type;

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbPatient.Columns.PID.getName(), pid);
        cv.put(DbPatient.Columns.FIRST_NAME.getName(), first_name);
        cv.put(DbPatient.Columns.LAST_NAME.getName(), last_name);
        cv.put(DbPatient.Columns.SSN.getName(), ssn);
        cv.put(DbPatient.Columns.BIRTHDAY.getName(), birthday);
        cv.put(DbPatient.Columns.NBC_CONTAMINATION.getName(), nbc_contamination);
        cv.put(DbPatient.Columns.TYPE.getName(), type);
        return cv;
    }

}
