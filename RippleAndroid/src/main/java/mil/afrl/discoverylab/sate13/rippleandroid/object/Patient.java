package mil.afrl.discoverylab.sate13.rippleandroid.object;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Defines the patient.
 *
 * Created by harmonbc on 6/19/13.
 */
public class Patient implements Parcelable{

    private int age;
    private int color;
    private int bpm;
    private int rpm;
    private int o2;
    private int pid;

    private int temperature;

    private Boolean nbcContam;
    private String fName, lName, ssn, sex, type, ipaddr;

    public Patient(){
        this.nbcContam = false;
    }

    public Patient(Parcel in){
        // Make sure this ordering matches the order of writes for writeToParcel

        this.pid = in.readInt();
        this.age = in.readInt();
        this.color = in.readInt();
        this.bpm = in.readInt();
        this.rpm = in.readInt();
        this.o2 = in.readInt();
        this.temperature = in.readInt();

        this.nbcContam = in.readByte() == 1;

        this.fName = in.readString();
        this.lName = in.readString();
        this.ssn = in.readString();
        this.sex = in.readString();
        this.type = in.readString();
        this.ipaddr = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        // Make sure this ordering matches the order of reads for Patient(Parcel)
        parcel.writeInt(this.pid);
        parcel.writeInt(this.age);
        parcel.writeInt(this.color);
        parcel.writeInt(this.bpm);
        parcel.writeInt(this.rpm);
        parcel.writeInt(this.o2);
        parcel.writeInt(this.temperature);

        parcel.writeByte((byte) (this.nbcContam ? 1 : 0));

        parcel.writeString(this.fName);
        parcel.writeString(this.lName);
        parcel.writeString(this.ssn);
        parcel.writeString(this.sex);
        parcel.writeString(this.type);
        parcel.writeString(this.ipaddr);
    }

    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public Boolean getNbcContam() {
        return nbcContam;
    }

    public void setNbcContam(Boolean nbcContam) {
        this.nbcContam = nbcContam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public int getO2() {
        return o2;
    }

    public void setO2(int o2) {
        this.o2 = o2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>()
    {
        @Override
        public Patient createFromParcel(Parcel parcel) {
            return new Patient(parcel);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

}
