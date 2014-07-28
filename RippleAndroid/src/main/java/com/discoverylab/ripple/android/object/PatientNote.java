package com.discoverylab.ripple.android.object;

import android.util.Log;

import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.discoverylab.ripple.android.util.PatientTagHelper;
import com.discoverylab.ripple.android.util.Util;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.text.DateFormat;
import java.text.ParseException;
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

    // Log tag
    private static final String TAG = PatientNote.class.getSimpleName();
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
    // ID of responder that made the note.
    private String responderId = Common.RESPONDER_ID;
    // Location note was taken at
    private LatLng latLng = new LatLng(-123456.0, -123456.0);
    private double altitude = -123456.0;

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
     * @param responderId ID of responder taking the note.
     * @return false if the note has already been finished, otherwise true
     */
    public boolean setResponderId(String responderId) {
        if (isFinished) {
            return false;
        }
        this.responderId = responderId;
        return true;
    }

    /**
     * @return ID of responder that took this note. Default is id of device operator.
     */
    public String getResponderId() {
        return this.responderId;
    }

    /**
     * @param latLng   latitude & longitude of this note
     * @param altitude altitude of this note
     * @return false if note has already been finished, otherwise true
     */
    public boolean setLatLngAlt(LatLng latLng, double altitude) {
        if (isFinished) {
            return false;
        }
        this.latLng = latLng;
        this.altitude = altitude;
        return true;
    }

    /**
     * Get the lat/lng this note was taken at
     *
     * @return LatLng this note was taken at or (-123456.0, -123456.0) if location was not set.
     */
    public LatLng getLatLng() {
        return this.latLng;
    }

    /**
     * @return Altitude note was taken at or -123456.0 if location was not set
     */
    public double getAltitude() {
        return this.altitude;
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

        object.addProperty(JSONTag.RESPONDER_ID, this.responderId);
        object.addProperty(JSONTag.PATIENT_ID, this.mPatient.getPatientId());
        object.addProperty(JSONTag.DATE, df.format(this.mDateTime));
        object.addProperty(JSONTag.NOTE_BODY_PART, this.selectedBodyPart.toString());

        JsonObject location = new JsonObject();
        location.addProperty(JSONTag.LOCATION_LAT, this.latLng.latitude);
        location.addProperty(JSONTag.LOCATION_LNG, this.latLng.longitude);
        location.addProperty(JSONTag.LOCATION_ALT, this.altitude);

        object.add(JSONTag.LOCATION, location);

        JsonArray noteContents = new JsonArray();

        for (NoteItem i : this.noteItems) {
            noteContents.add(i.getJsonObject());
        }

        object.add(JSONTag.NOTE_CONTENTS, noteContents);

        return object;

    }

    public static PatientNote fromJson(String jsonString) {
        Gson gson = new GsonBuilder().setDateFormat(Common.ISO_DATETIME_FORMAT).create();
        DateFormat df = Util.getISOUTCFormatter();
        try {
            // may throw parse exception
            JsonObject object = gson.fromJson(jsonString, JsonObject.class);

            String responderId = object.get(JSONTag.RESPONDER_ID).getAsString();
            String patientId = object.get(JSONTag.PATIENT_ID).getAsString();

            // TODO: what is patient has not been created yet?
            Patient p = Patients.getInstance().getPatient(patientId);

            PatientNote note = new PatientNote(p);
            note.setResponderId(responderId);

            Date noteDate = df.parse(object.get(JSONTag.DATE).getAsString());

            note.setDate(noteDate);

            PatientTagHelper.BODY_PARTS bodyPart =
                    PatientTagHelper.BODY_PARTS.valueOf(object.get(JSONTag.NOTE_BODY_PART).getAsString());

            note.setSelectedBodyPart(bodyPart);

            JsonObject location = object.get(JSONTag.LOCATION).getAsJsonObject();
            double latitude = location.get(JSONTag.LOCATION_LAT).getAsDouble();
            double longitude = location.get(JSONTag.LOCATION_LNG).getAsDouble();
            double altitude = location.get(JSONTag.LOCATION_ALT).getAsDouble();

            note.setLatLngAlt(new LatLng(latitude, longitude), altitude);

            JsonArray noteItems = object.get(JSONTag.NOTE_CONTENTS).getAsJsonArray();

            for (JsonElement je : noteItems) {
                NoteItem item = noteItemFromJson(je.getAsJsonObject());
                note.addNoteItem(item);
            }
            // finish note
            note.finish();
            // return the note
            return note;
        } catch (JsonParseException jpe) {
            Log.e(TAG, "Failed to parse note json.");
        } catch (ParseException pe) {
            Log.e(TAG, "Failed to parse note Date.");
        }
        return null;
    }

    public static NoteItem noteItemFromJson(JsonObject json) {
        NoteItem.NOTE_TYPE type = NoteItem.NOTE_TYPE.valueOf(json.get(JSONTag.NOTE_ITEM_TYPE).getAsString());
        NoteItem rtnValue = null;
        switch (type) {
            case TEXT:
                rtnValue = new NoteItemText(json.get(JSONTag.NOTE_ITEM_MESSAGE).getAsString());
                break;
            case IMAGE:
                rtnValue = new NoteItemImage(json.get(JSONTag.NOTE_ITEM_FILE).getAsString());
                break;
            case VOICE:
                rtnValue = new NoteItemVoice();
                break;
            case DRUG:
                rtnValue = new NoteItemDrug();
                break;
            case ECG:
                rtnValue = new NoteItemEcg();
                break;
        }
        return rtnValue;
    }

}






