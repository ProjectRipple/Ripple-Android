package mil.afrl.discoverylab.sate13.rippleandroid.data.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent.DbVitals;

/**
 * Created by burt on 7/3/13.
 */
public final class Vitals implements Parcelable {

    public Integer vid;
    public Integer ip_addr;
    public Integer timestamp;
    public Integer sensor_type;
    public Integer value_type;
    public Integer value;

    public Vitals() {
    }

    public Vitals(Parcel in) {
        vid = in.readInt();
        ip_addr = in.readInt();
        timestamp = in.readInt();
        sensor_type = in.readInt();
        value_type = in.readInt();
        value = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(vid);
        dest.writeInt(ip_addr);
        dest.writeInt(timestamp);
        dest.writeInt(sensor_type);
        dest.writeInt(value_type);
        dest.writeInt(value);
    }

    public static final Parcelable.Creator<Vitals> CREATOR = new Parcelable.Creator<Vitals>() {
        public Vitals createFromParcel(Parcel in) {
            return new Vitals(in);
        }

        public Vitals[] newArray(int size) {
            return new Vitals[size];
        }
    };

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbVitals.Columns.VID.getName(), vid);
        cv.put(DbVitals.Columns.IP_ADDR.getName(), ip_addr);
        cv.put(DbVitals.Columns.TIMESTAMP.getName(), timestamp);
        cv.put(DbVitals.Columns.SENSOR_TYPE.getName(), sensor_type);
        cv.put(DbVitals.Columns.VALUE_TYPE.getName(), value_type);
        cv.put(DbVitals.Columns.VALUE.getName(), value);
        return cv;
    }
}
