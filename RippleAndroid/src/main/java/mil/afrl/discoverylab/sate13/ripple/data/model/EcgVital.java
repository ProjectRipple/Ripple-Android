package mil.afrl.discoverylab.sate13.ripple.data.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
public final class EcgVital implements Parcelable, Serializable {


    private static final long serialVersionUID = 512L;

    public transient Integer sensor_type_int;

    public Integer vid = -1;
    public Integer pid;
    public Date server_timestamp;
    public Long sensor_timestamp;
    public String sensor_type;
    public String value_type;
    public Integer value;

    private EcgVital() {
        // No public default constructor
    }

    private void parseSensorType() {
        if (sensor_type != null) {
            try {
                sensor_type_int = Integer.parseInt(sensor_type);
            } catch (NumberFormatException nfe) {
                Log.e(Common.LOG_TAG, "Unable to parse sensorType: " + sensor_type);
            }
        }
    }

    public EcgVital(Integer pid, String server_timestamp, Long sensor_timestamp, String sensor_type, String value_type, Integer value) {
        this.pid = pid;
        try {
            this.server_timestamp = Common.SIMPLE_DATETIME_FORMAT.parse(server_timestamp);
        } catch (ParseException e) {
            this.server_timestamp = new Date();
        }
        this.sensor_timestamp = sensor_timestamp;
        this.sensor_type = sensor_type;
        parseSensorType();
        this.value_type = value_type;
        this.value = value;
    }

    public EcgVital(EcgVital v) {
        vid = v.vid;
        pid = v.pid;
        server_timestamp = new Date(v.server_timestamp.getTime());
        sensor_timestamp = v.sensor_timestamp;
        sensor_type = v.sensor_type;
        parseSensorType();
        value_type = v.value_type;
        value = v.value;
    }

    public EcgVital(Parcel in) {
        vid = in.readInt();
        pid = in.readInt();
        try {
            server_timestamp = Common.SIMPLE_DATETIME_FORMAT.parse(in.readString());
        } catch (ParseException e) {
            server_timestamp = new Date();
        }
        sensor_timestamp = in.readLong();
        sensor_type = in.readString();
        parseSensorType();
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
        parseSensorType();
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

    public static final Creator<EcgVital> CREATOR = new Creator<EcgVital>() {
        public EcgVital createFromParcel(Parcel in) {
            return new EcgVital(in);
        }

        public EcgVital[] newArray(int size) {
            return new EcgVital[size];
        }
    };

    public void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
