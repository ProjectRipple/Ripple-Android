package com.discoverylab.ripple.android.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data from a patient info request to the broker.
 * <p/>
 * Created by james on 7/24/14.
 */
public class PatientInfoRequestData {

    private String result;
    private String msg;
    private List<PatientInfo> patients = new ArrayList<PatientInfo>();

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<PatientInfo> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientInfo> patients) {
        this.patients = patients;
    }

}