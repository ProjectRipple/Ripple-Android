package mil.afrl.discoverylab.sate13.rippleandroid.data.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent.db_vital;

/**
 * Created by burt on 7/3/13.
 */
public final class Vital implements Parcelable {

    public Integer vid;
    public Integer pid;
    public String server_timestamp;
    public Integer sensor_timestamp;
    public Integer sensor_type;
    public Integer value_type;
    public Integer value;

    public Vital() {
    }

    public Vital(Parcel in) {
        vid = in.readInt();
        pid = in.readInt();
        server_timestamp = in.readString();
        sensor_timestamp = in.readInt();
        sensor_type = in.readInt();
        value_type = in.readInt();
        value = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(vid);
        dest.writeInt(pid);
        dest.writeString(server_timestamp);
        dest.writeInt(sensor_timestamp);
        dest.writeInt(sensor_type);
        dest.writeInt(value_type);
        dest.writeInt(value);
    }

    public static final Parcelable.Creator<Vital> CREATOR = new Parcelable.Creator<Vital>() {
        public Vital createFromParcel(Parcel in) {
            return new Vital(in);
        }

        public Vital[] newArray(int size) {
            return new Vital[size];
        }
    };

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(db_vital.Columns.VID.getName(), vid);
        cv.put(db_vital.Columns.PID.getName(), pid);
        cv.put(db_vital.Columns.SERVER_TIMESTAMP.getName(), server_timestamp);
        cv.put(db_vital.Columns.SENSOR_TIMESTAMP.getName(), sensor_timestamp);
        cv.put(db_vital.Columns.SENSOR_TYPE.getName(), sensor_type);
        cv.put(db_vital.Columns.VALUE_TYPE.getName(), value_type);
        cv.put(db_vital.Columns.VALUE.getName(), value);
        return cv;
    }
}
