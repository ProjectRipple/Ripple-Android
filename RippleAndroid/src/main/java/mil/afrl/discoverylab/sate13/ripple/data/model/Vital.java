package mil.afrl.discoverylab.sate13.ripple.data.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent;

/**
 * Created by burt on 7/3/13.
 */
public final class Vital implements Parcelable, Serializable {

    private static final long serialVersionUID = 512L;

    public Integer vid;
    public Integer pid;
    public Date server_timestamp;
    public Long sensor_timestamp;
    public String sensor_type;
    public String value_type;
    public Integer value;

    private Vital() {
        // No public default constructor
    }

    public Vital(Integer pid, String server_timestamp, Long sensor_timestamp, String sensor_type, String value_type, Integer value) {
        this.vid = -1;
        this.pid = pid;
        try {
            this.server_timestamp = Common.SIMPLE_DATETIME_FORMAT.parse(server_timestamp);
        } catch (ParseException e) {
            this.server_timestamp = new Date();
        }
        this.sensor_timestamp = sensor_timestamp;
        this.sensor_type = sensor_type;
        this.value_type = value_type;
        this.value = value;
    }

    public Vital(Vital v) {
        vid = v.vid;
        pid = v.pid;
        server_timestamp = new Date(v.server_timestamp.getTime());
        sensor_timestamp = v.sensor_timestamp;
        sensor_type = v.sensor_type;
        value_type = v.value_type;
        value = v.value;
    }

    public Vital(Parcel in) {
        vid = in.readInt();
        pid = in.readInt();
        try {
            server_timestamp = Common.SIMPLE_DATETIME_FORMAT.parse(in.readString());
        } catch (ParseException e) {
            server_timestamp = new Date();
        }
        sensor_timestamp = in.readLong();
        sensor_type = in.readString();
        value_type = in.readString();
        value = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(vid);
        dest.writeInt(pid);
        dest.writeString(Common.SIMPLE_DATETIME_FORMAT.format(server_timestamp));
        dest.writeLong(sensor_timestamp);
        dest.writeString(sensor_type);
        dest.writeString(value_type);
        dest.writeInt(value);
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(RippleContent.db_vital.Columns.VID.getName(), vid);
        cv.put(RippleContent.db_vital.Columns.PID.getName(), pid);
        cv.put(RippleContent.db_vital.Columns.SERVER_TIMESTAMP.getName(), Common.SIMPLE_DATETIME_FORMAT.format(server_timestamp));
        cv.put(RippleContent.db_vital.Columns.SENSOR_TIMESTAMP.getName(), sensor_timestamp);
        cv.put(RippleContent.db_vital.Columns.SENSOR_TYPE.getName(), sensor_type);
        cv.put(RippleContent.db_vital.Columns.VALUE_TYPE.getName(), value_type);
        cv.put(RippleContent.db_vital.Columns.VALUE.getName(), value);
        return cv;
    }

    public static final Parcelable.Creator<Vital> CREATOR = new Parcelable.Creator<Vital>() {
        public Vital createFromParcel(Parcel in) {
            return new Vital(in);
        }

        public Vital[] newArray(int size) {
            return new Vital[size];
        }
    };

    public void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
