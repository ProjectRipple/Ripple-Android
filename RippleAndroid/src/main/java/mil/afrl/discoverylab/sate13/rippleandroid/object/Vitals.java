package mil.afrl.discoverylab.sate13.rippleandroid.object;

/**
 * Created by harmonbc on 6/21/13.
 */
public class Vitals {

    private int vid, value;
    private Sensors sensor;
    private Value valueType;
    private String ipaddr;

    public Vitals(){
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Sensors getSensor() {
        return sensor;
    }

    public void setSensor(Sensors sensor) {
        this.sensor = sensor;
    }

    public Value getValueType() {
        return valueType;
    }

    public void setValueType(Value valueType) {
        this.valueType = valueType;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    private enum Sensors{
        PULSE_OX, RESP, ECG;
    }

    private enum Value{
        PULSE, O2, ECG, RESP
    }
}
