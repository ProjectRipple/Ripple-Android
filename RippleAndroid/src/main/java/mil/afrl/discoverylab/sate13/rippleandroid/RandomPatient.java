package mil.afrl.discoverylab.sate13.rippleandroid;

import android.graphics.Color;

import java.util.Random;

import mil.afrl.discoverylab.sate13.rippleandroid.object.Patient;

/**
 * Created by harmonbc on 6/24/13.
 */
public class RandomPatient {

    private static int[] colors = {Color.RED, Color.GREEN, Color.YELLOW};
    private static String[] fNames = {"Bill", "Tom", "Will", "James"};
    private static String[] lNames = {"Doe", "Nye", "Brady", "Smith", "Bacon"};
    private static String[] ssn = {"999-99-9999", "888-88-8888", "777-77-7777", "666-66-6666"};
    private static String[] src = {"0012740400040404", "0012740400040303", "0012740400065303","0012740406740303","0012723400040303","0012740400120303","0012740457876303","0012740428176303","0012740457476303"};
    private static String[] sex = {"Male", "Female"};
    private static String[] type = {"US Mil", "US Civ", "Foreign Civ", "E-POW"};
    private static String[] ipAddr = {"127.0.0.2", "127.0.0.3", "192.168.0.3", "10.0.0.2", "10.3.2.1", "10.4.3.2", "10.5.4.3"};
    private static int lastSrc = 0;
    public static final int MAX_UNIQUE_PATIENTS = src.length+1;

    public static Patient getRandomPatient() {
        Patient patient = new Patient();
        patient.setColor(colors[new Random().nextInt(colors.length)]);
        patient.setfName(fNames[new Random().nextInt(fNames.length)]);
        patient.setlName(lNames[new Random().nextInt(lNames.length)]);
        patient.setSsn(ssn[new Random().nextInt(ssn.length)]);
        patient.setSex(sex[new Random().nextInt(sex.length)]);
        patient.setType(type[new Random().nextInt(type.length)]);
        patient.setIpaddr(ipAddr[new Random().nextInt(ipAddr.length)]);
        patient.setBpm(new Random().nextInt(80) + 20);
        patient.setO2(new Random().nextInt(30) + 70);
        patient.setRpm(new Random().nextInt(24));
        patient.setSrc(src[lastSrc]);
        lastSrc++;
        if(lastSrc == src.length){
            // reset
            lastSrc = 0;
        }
        patient.setTemperature(new Random().nextInt(10) + 90);
        return patient;
    }
}
