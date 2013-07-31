package mil.afrl.discoverylab.sate13.ripple.data.model;

import android.content.ContentValues;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent.db_intervention;

/**
 * Created by burt on 7/3/13.
 */
public final class Intervention {

    public Integer pid;
    public String type;
    public String details;

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(db_intervention.Columns.PID.getName(), pid);
        cv.put(db_intervention.Columns.TYPE.getName(), type);
        cv.put(db_intervention.Columns.DETAILS.getName(), details);
        return cv;
    }

}
