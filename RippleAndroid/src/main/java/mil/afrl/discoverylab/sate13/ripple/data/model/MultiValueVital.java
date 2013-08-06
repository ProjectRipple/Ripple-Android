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
public final class MultiValueVital implements Parcelable, Serializable {


    private static final long serialVersionUID = 512L;


    public Integer vid = -1;
    public Integer pid;
    public Date server_timestamp;
    public Long sensor_timestamp;
    public Integer sensor_type;
    public Integer value_type;
    public Integer period_ms;
    public int[] values;

    private MultiValueVital() {
        // No public default constructor
    }

    public MultiValueVital(Integer pid, String server_timestamp, Long sensor_timestamp, Integer sensor_type, Integer value_type, Integer period_ms, int[] values) {
        this.pid = pid;
        try {
            this.server_timestamp = Common.SIMPLE_DATETIME_FORMAT.parse(server_timestamp);
        } catch (ParseException e) {
            this.server_timestamp = new Date();
        }
        this.sensor_timestamp = sensor_timestamp;
        this.sensor_type = sensor_type;
        this.value_type = value_type;
        this.period_ms = period_ms;
        this.values = values;
    }

    public MultiValueVital(MultiValueVital v) {
        vid = v.vid;
        pid = v.pid;
        server_timestamp = new Date(v.server_timestamp.getTime());
        sensor_timestamp = v.sensor_timestamp;
        sensor_type = v.sensor_type;
        value_type = v.value_type;
        values = v.values;
    }

    public MultiValueVital(Parcel in) {
        vid = in.readInt();
        pid = in.readInt();
        try {
            server_timestamp = Common.SIMPLE_DATETIME_FORMAT.parse(in.readString());
        } catch (ParseException e) {
            server_timestamp = new Date();
        }
        sensor_timestamp = in.readLong();
        sensor_type = in.readInt();
        value_type = in.readInt();
        period_ms = in.readInt();
        in.readIntArray(values);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(vid);
        dest.writeInt(pid);
        dest.writeString(Common.SIMPLE_DATETIME_FORMAT.format(server_timestamp));
        dest.writeLong(sensor_timestamp);
        dest.writeInt(sensor_type);
        dest.writeInt(value_type);
        dest.writeInt(period_ms);
        dest.writeIntArray(values);
    }

//    public ContentValues toContentValues() {
//        ContentValues cv = new ContentValues();
//        cv.put(RippleContent.db_vital.Columns.VID.getName(), vid);
//        cv.put(RippleContent.db_vital.Columns.PID.getName(), pid);
//        cv.put(RippleContent.db_vital.Columns.SERVER_TIMESTAMP.getName(), Common.SIMPLE_DATETIME_FORMAT.format(server_timestamp));
//        cv.put(RippleContent.db_vital.Columns.SENSOR_TIMESTAMP.getName(), sensor_timestamp);
//        cv.put(RippleContent.db_vital.Columns.SENSOR_TYPE.getName(), sensor_type);
//        cv.put(RippleContent.db_vital.Columns.VALUE_TYPE.getName(), value_type);
//        cv.put(RippleContent.db_vital.Columns.VALUE.getName(), values);
//        return cv;
//    }

    public static final Creator<MultiValueVital> CREATOR = new Creator<MultiValueVital>() {
        public MultiValueVital createFromParcel(Parcel in) {
            return new MultiValueVital(in);
        }

        public MultiValueVital[] newArray(int size) {
            return new MultiValueVital[size];
        }
    };

    public void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
