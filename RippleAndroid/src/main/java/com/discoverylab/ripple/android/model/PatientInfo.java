package com.discoverylab.ripple.android.model;

/**
 * Object for patient info returned by patient info request.
 * <p/>
 * Created by james on 7/24/14.
 */
public class PatientInfo {

    private String pid;
    private String rid;
    private String date;
    private String name;
    private Integer age;
    private String sex;
    private String nbc;
    private String triage;
    private String status;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNbc() {
        return nbc;
    }

    public void setNbc(String nbc) {
        this.nbc = nbc;
    }

    public String getTriage() {
        return triage;
    }

    public void setTriage(String triage) {
        this.triage = triage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
