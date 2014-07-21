package com.discoverylab.ripple.android.object;

import java.util.List;
import java.util.Vector;

/**
 * Singleton to hold global list of patients.
 * Created by james on 7/21/14.
 */
public class PatientList {

    // Reference to PatientList instance
    private static PatientList instance;

    // Global list of patients
    private List<Patient> patientList = new Vector<Patient>(40);

    // object to lock patient list
    private final Object lock = new Object();

    // Private constructor
    private PatientList() {
    }

    // Use static initialization for synchronization of creating instance
    private static class Loader {
        private static PatientList instance = new PatientList();
    }

    public static PatientList getInstance() {
        return Loader.instance;
    }

    public boolean addPatient(Patient p) {
        return patientList.add(p);
    }

    public Patient getPatient(int index) {
        return patientList.get(index);
    }

    public int getPatientListSize() {
        return patientList.size();
    }

    public List<Patient> getPatientList() {
        return patientList;
    }
}
