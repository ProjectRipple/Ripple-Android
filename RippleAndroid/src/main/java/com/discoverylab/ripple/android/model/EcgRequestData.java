package com.discoverylab.ripple.android.model;

/**
 * Object representing result of an ECG stream request
 * Created by James on 6/1/2014.
 */
public class EcgRequestData {

    private String result;
    private String msg;

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
}