package com.discoverylab.ripple.android.object;

import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.discoverylab.ripple.android.util.PatientTagHelper;
import com.discoverylab.ripple.android.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * A collection of {@link NoteItem} to represent a note taken by a responder about a patient.
 * Created by james on 7/16/14.
 */
public class PatientNote {

    // ID of this note
    private final UUID noteId = UUID.randomUUID();
    // Reference to patient that this note is about
    private final Patient mPatient;
    // List of note items
    private List<NoteItem> noteItems = new ArrayList<NoteItem>();
    // Date that note was taken
    private Date mDateTime;
    // What body part is this note about
    private PatientTagHelper.BODY_PARTS selectedBodyPart = PatientTagHelper.BODY_PARTS.NONE;
    // Is the note finalised (unmodifiable)
    private boolean isFinished = false;

    public PatientNote(Patient p) {
        this.mPatient = p;
    }

    /**
     * Add an item to the note
     *
     * @param item NoteItem to add
     * @return false if the note has already been finished, otherwise the result of {@link List#add(Object)}
     */
    public boolean addNoteItem(NoteItem item) {
        if (isFinished) {
            return false;
        }
        return this.noteItems.add(item);
    }

    /**
     * @return Number of note items in this note
     */
    public int getNumNoteItems() {
        return this.noteItems.size();
    }

    /**
     * @return An unmodifiable list referencing this objects note items
     */
    public List<NoteItem> getNoteItems() {
        return Collections.unmodifiableList(this.noteItems);
    }

    /**
     * Set the body part that this note is about.
     *
     * @param bodyPart Body part this note is about
     * @return false if the note has already been finished, otherwise true
     */
    public boolean setSelectedBodyPart(PatientTagHelper.BODY_PARTS bodyPart) {
        if (isFinished) {
            return false;
        }
        this.selectedBodyPart = bodyPart;
        return true;
    }

    /**
     * Get the body part this note is about
     *
     * @return body part this note is about
     */
    public PatientTagHelper.BODY_PARTS getSelectedBodyPart() {
        return this.selectedBodyPart;
    }

    /**
     * Set the date this note was taken
     *
     * @param date Date of this note
     * @return false if the note has already been finished, otherwise true
     */
    public boolean setDate(Date date) {
        if (isFinished) {
            return false;
        }
        this.mDateTime = date;
        return true;
    }

    /**
     * Get the date of this note
     *
     * @return date of this note
     */
    public Date getDate() {
        return new Date(this.mDateTime.getTime());
    }

    /**
     * Finish the note, making it unmodifiable. All add/set functions on this
     * object will return false after this function is called.
     */
    public void finish() {
        this.isFinished = true;
    }

    /**
     * @return true if {@link #finish()} has been called on this Note, false otherwise
     */
    public boolean isFinished() {
        return this.isFinished;
    }

    public Patient getPatient() {
        return this.mPatient;
    }

    public UUID getNoteId() {
        return noteId;
    }


    /**
     * Get a Json Object representing this note.
     *
     * @return JsonObject representing this note.
     */
    public JsonObject getJsonObject() {
        JsonObject object = new JsonObject();

        DateFormat df = Util.getISOUTCFormatter();

        object.addProperty(JSONTag.RESPONDER_ID, Common.RESPONDER_ID);
        object.addProperty(JSONTag.PATIENT_ID, this.mPatient.getPatientId());
        object.addProperty(JSONTag.DATE, df.format(this.mDateTime));
        object.addProperty(JSONTag.NOTE_BODY_PART, this.selectedBodyPart.toString());

        JsonObject location = new JsonObject();
        location.addProperty(JSONTag.LOCATION_LAT, Common.responderLatLng.latitude);
        location.addProperty(JSONTag.LOCATION_LNG, Common.responderLatLng.longitude);
        location.addProperty(JSONTag.LOCATION_ALT, Common.responderAltitude);

        object.add(JSONTag.LOCATION, location);

        JsonArray noteContents = new JsonArray();

        for(NoteItem i : this.noteItems){
            noteContents.add(i.getJsonObject());
        }

        object.add(JSONTag.NOTE_CONTENTS, noteContents);

        return object;

    }

}
