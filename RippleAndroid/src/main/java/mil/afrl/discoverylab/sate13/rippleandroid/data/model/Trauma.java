package mil.afrl.discoverylab.sate13.rippleandroid.data.model;

import android.content.ContentValues;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent.DbTrauma;

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
        cv.put(DbTrauma.Columns.TID.getName(), tid);
        cv.put(DbTrauma.Columns.PID.getName(), pid);
        cv.put(DbTrauma.Columns.LOCATION.getName(), location);
        cv.put(DbTrauma.Columns.TYPE.getName(), type);
        cv.put(DbTrauma.Columns.STATUS.getName(), status);
        return cv;
    }

}
