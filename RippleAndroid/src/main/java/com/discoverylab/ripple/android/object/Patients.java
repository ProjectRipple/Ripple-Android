package com.discoverylab.ripple.android.object;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Singleton to hold global map of patients.
 * <p/>
 * Created by james on 7/21/14.
 */
public class Patients {

    // Global list of patients
    private ConcurrentMap<String, Patient> patientMap = new ConcurrentHashMap<String, Patient>(80);

    // object to lock patient list
    private final Object lock = new Object();

    // Private constructor
    private Patients() {
    }

    // Use static initialization for synchronization of creating instance
    private static class Loader {
        private static Patients instance = new Patients();
    }

    public static Patients getInstance() {
        return Loader.instance;
    }

    public boolean patientExists(String id) {
        return patientMap.containsKey(id);
    }

    public void addPatient(String id, Patient p) {
        patientMap.put(id, p);
    }

    public Patient getPatient(String id) {
        return patientMap.get(id);
    }

    public int getNumPatients() {
        return patientMap.size();
    }

    public Set<Map.Entry<String, Patient>> getPatientEntries() {
        return patientMap.entrySet();
    }
}
