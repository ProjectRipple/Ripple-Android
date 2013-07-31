package mil.afrl.discoverylab.sate13.ripple.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;

/**
 * Created by burt on 7/31/13.
 */
public class SubscriptionResponse implements Parcelable {

    public boolean success;
    public String exception;
    public Integer pid_echo;
    public String action_echo;
    public Integer port_echo;

    private SubscriptionResponse() {
        // No default Constructor
    }

    public SubscriptionResponse(boolean success, String exception, Integer pid_echo, String action_echo, Integer port_echo) {
        this.success = success;
        this.exception = exception;
        this.pid_echo = pid_echo;
        this.action_echo = action_echo;
        this.port_echo = port_echo;
    }

    public SubscriptionResponse(Parcel in) {
        success = (in.readInt() == 1);
        exception = in.readString();
        pid_echo = in.readInt();
        action_echo = in.readString();
        port_echo = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        if (success == true) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(exception);
        dest.writeInt(pid_echo);
        dest.writeString(action_echo);
        dest.writeInt(port_echo);
    }

    @Override
    public String toString() {
        return Common.GSON.toJson(this);
    }
}
