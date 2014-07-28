package com.discoverylab.ripple.android.object;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Singleton to hold all notes about patients.
 * <p/>
 * Created by james on 7/28/14.
 */
public class PatientNotes {

    private ConcurrentMap<String, List<PatientNote>> patientNotes = new ConcurrentHashMap<String, List<PatientNote>>(20);

    private PatientNotes() {

    }

    // Use static initialization for synchronization of creating instance
    private static class Loader {
        private static PatientNotes instance = new PatientNotes();
    }

    public static PatientNotes getInstance() {
        return Loader.instance;
    }

    /**
     * Add a note for a patient.
     *
     * @param note Note to add
     * @return false if note is null or note's patient is null, otherwise result of {@link List#add}
     */
    public synchronized boolean addNote(PatientNote note) {
        boolean rtn = false;
        if (note == null || note.getPatient() == null) {
            rtn = false;
        } else {
            Patient p = note.getPatient();
            List<PatientNote> notes = patientNotes.get(p.getPatientId());
            if (notes == null) {
                notes = new ArrayList<PatientNote>(5);
                patientNotes.put(p.getPatientId(), notes);
            }
            rtn = notes.add(note);
        }
        return rtn;
    }

    /**
     * @param patientId ID of patient to retrieve notes for
     * @return List of notes for this patient or null if no notes have been added.
     */
    public List<PatientNote> getNotesForPatient(String patientId) {
        if (!patientNotes.containsKey(patientId)) {
            patientNotes.put(patientId, new ArrayList<PatientNote>(5));
        }
        return patientNotes.get(patientId);
    }
}
