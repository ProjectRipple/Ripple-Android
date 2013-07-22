package mil.afrl.discoverylab.sate13.rippleandroid.data.model;

import android.content.ContentValues;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent.db_trauma;

/**
 * Created by burt on 7/3/13.
 */
public final class Trauma {

    public Integer tid;
    public Integer pid;
    public String location;
    public String type;
    public String status;

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(db_trauma.Columns.TID.getName(), tid);
        cv.put(db_trauma.Columns.PID.getName(), pid);
        cv.put(db_trauma.Columns.LOCATION.getName(), location);
        cv.put(db_trauma.Columns.TYPE.getName(), type);
        cv.put(db_trauma.Columns.STATUS.getName(), status);
        return cv;
    }

}
