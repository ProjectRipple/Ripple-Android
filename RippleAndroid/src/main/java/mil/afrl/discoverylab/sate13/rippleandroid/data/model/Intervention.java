package mil.afrl.discoverylab.sate13.rippleandroid.data.model;

import android.content.ContentValues;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent.DbIntervention;

/**
 * Created by burt on 7/3/13.
 */
public final class Intervention {

    public Integer iid;
    public Integer pid;
    public String type;
    public String details;

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbIntervention.Columns.IID.getName(), iid);
        cv.put(DbIntervention.Columns.PID.getName(), pid);
        cv.put(DbIntervention.Columns.TYPE.getName(), type);
        cv.put(DbIntervention.Columns.DETAILS.getName(), details);
        return cv;
    }

}
