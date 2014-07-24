package com.discoverylab.ripple.android.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.discoverylab.ripple.android.config.Common;

import java.util.Date;

/**
 * Defines the patient.
 * <p/>
 * Created by harmonbc on 6/19/13.
 */
public class Patient implements Parcelable {

    // integer values of patient
    private int age;
    private int heartRate;
    private int breathsPerMin;
    private int o2;
    private int temperature;

    private Common.TRIAGE_COLORS triageState;
    private Common.NBC_CONTAMINATION_OPTIONS nbcContam;

    // string values of patient
    private String name;
    private String sex;
    private String type;
    private String ipaddr;
    private String status;
    private String patientId;

    // date patient was last seen by system
    private Date lastSeenDate = new Date();

    // is the patient selected by the user
    private boolean isSelected = false;

    public Patient(String patientId) {
        this.patientId = patientId;

        // set default values
        this.age = -1;
        this.triageState = Common.TRIAGE_COLORS.UNKNOWN;
        this.heartRate = -1;
        this.breathsPerMin = -1;
        this.o2 = -1;
        this.temperature = -1;

        this.name = "John Doe";
        this.sex = "Unknown";
        this.type = "Unknown";
        this.status = "Not Attended";
        this.ipaddr = "";

        this.isSelected = false;
        this.nbcContam = Common.NBC_CONTAMINATION_OPTIONS.NONE;
    }


    public Patient(Parcel in) {
        // Make sure this ordering matches the order of writes for writeToParcel
        this.age = in.readInt();
        this.triageState = (Common.TRIAGE_COLORS) in.readSerializable();
        this.heartRate = in.readInt();
        this.breathsPerMin = in.readInt();
        this.o2 = in.readInt();
        this.temperature = in.readInt();

        this.name = in.readString();
        this.sex = in.readString();
        this.type = in.readString();
        this.status = in.readString();
        this.ipaddr = in.readString();
        this.patientId = in.readString();

        this.lastSeenDate = new Date(in.readLong());

        this.isSelected = (in.readByte() == 1);
        this.nbcContam = (Common.NBC_CONTAMINATION_OPTIONS) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        // Make sure this ordering matches the order of reads for Patient(Parcel)
        parcel.writeInt(this.age);
        parcel.writeSerializable(this.triageState);
        parcel.writeInt(this.heartRate);
        parcel.writeInt(this.breathsPerMin);
        parcel.writeInt(this.o2);
        parcel.writeInt(this.temperature);

        parcel.writeString(this.name);
        parcel.writeString(this.sex);
        parcel.writeString(this.type);
        parcel.writeString(this.status);
        parcel.writeString(this.ipaddr);
        parcel.writeString(this.patientId);

        parcel.writeLong(this.lastSeenDate.getTime());

        parcel.writeByte((byte) (this.isSelected ? 1 : 0));
        parcel.writeSerializable(this.nbcContam);
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public Common.NBC_CONTAMINATION_OPTIONS getNbcContam() {
        return nbcContam;
    }

    public void setNbcContam(Common.NBC_CONTAMINATION_OPTIONS nbcContam) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getTriageColor() {
        return triageState.getColor();
    }

    public Common.TRIAGE_COLORS getTriageState() {
        return this.triageState;
    }

    public void setTriageState(Common.TRIAGE_COLORS triageState) {
        this.triageState = triageState;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getBreathsPerMin() {
        return breathsPerMin;
    }

    public void setBreathsPerMin(int breathsPerMin) {
        this.breathsPerMin = breathsPerMin;
    }

    public int getO2() {
        return o2;
    }

    public void setO2(int o2) {
        this.o2 = o2;
    }

    public String getPatientId() {
        return patientId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Date getLastSeenDate() {
        return lastSeenDate;
    }

    public void setLastSeenDate(Date lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
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
