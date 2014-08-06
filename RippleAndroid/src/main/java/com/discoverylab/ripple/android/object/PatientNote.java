package com.discoverylab.ripple.android.object;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private String noteId = UUID.randomUUID().toString();
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
    private LatLng latLng = Common.DEFAULT_LATLNG;
    private double altitude = Common.DEFAULT_ALT;

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
     * @return LatLng this note was taken at or {@value Common#DEFAULT_LATLNG} if location was not set.
     */
    public LatLng getLatLng() {
        return this.latLng;
    }

    /**
     * @return Altitude note was taken at or {@value Common#DEFAULT_ALT} if location was not set
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

    public String getNoteId() {
        return noteId;
    }


    /**
     * Get a Json Object representing this note.
     *
     * @return JsonObject representing this note.
     */
    public JsonObject getJsonObject() {
        return this.getJsonObject(false);
    }

    /**
     * Get a JsonObject representing this note, removing elements not needed when writing to a file
     * if requested. Example of something to ignore is the base64 string representing a note's image
     * as the image should be stored as a stand alone file.
     *
     * @param writingToFile True ignores extra elements that should not be written to the json file.
     * @return JsonObject representing this note.
     */
    private JsonObject getJsonObject(boolean writingToFile) {
        JsonObject object = new JsonObject();

        DateFormat df = Util.getISOUTCFormatter();

        object.addProperty(JSONTag.RESPONDER_ID, this.responderId);
        object.addProperty(JSONTag.PATIENT_ID, this.mPatient.getPatientId());
        object.addProperty(JSONTag.NOTE_ID, this.noteId);
        object.addProperty(JSONTag.DATE, df.format(this.mDateTime));
        object.addProperty(JSONTag.NOTE_BODY_PART, this.selectedBodyPart.toString());

        JsonObject location = new JsonObject();
        location.addProperty(JSONTag.LOCATION_LAT, this.latLng.latitude);
        location.addProperty(JSONTag.LOCATION_LNG, this.latLng.longitude);
        location.addProperty(JSONTag.LOCATION_ALT, this.altitude);

        object.add(JSONTag.LOCATION, location);

        JsonArray noteContents = new JsonArray();

        for (NoteItem i : this.noteItems) {
            if (writingToFile) {
                // TODO: Figure out a better way to handle this
                // Remove anything that should not be written to file
                if (i instanceof NoteItemImage) {
                    // Always ignore base64 image when writing to file
                    noteContents.add(((NoteItemImage) i).getJsonObjectNoImage());
                } else {
                    // default just add full json object
                    noteContents.add(i.getJsonObject());
                }
            } else {
                noteContents.add(i.getJsonObject());
            }
        }

        object.add(JSONTag.NOTE_CONTENTS, noteContents);

        return object;
    }


    /**
     * Save the this note to a file.
     *
     * @param context An application context for retrieving the storage directory
     * @return true if save was successful, false otherwise.
     */
    public boolean saveNoteToFile(Context context) {
        if (context == null) {
            Log.e(TAG, "Null context passed to saveNoteToFile.");
            return false;
        }

        // Assuming patient id is valid directory name
        File noteDir = context.getDir(Common.NOTES_DIR, Context.MODE_PRIVATE);
        File patientNoteDir = new File(noteDir.getPath() + File.separator + this.getPatient().getPatientId());
        if (!patientNoteDir.exists()) {
            if (!patientNoteDir.mkdirs()) {
                Log.e(TAG, "Failed to creates notes directory!");
                return false;
            }
        }

        File outFile = new File(patientNoteDir.getPath() + File.separator + this.getNoteId() + ".json");
        JsonObject noteJson = this.getJsonObject(true);

        // File already exists
        if (outFile.exists()) {
            // delete the old note file? it should be the same, but just in case.
            boolean deleteResult = outFile.delete();
            if (deleteResult) {
                Log.d(TAG, "Deleted existing note file: " + outFile.getName());
            } else {
                Log.d(TAG, "Failed to delete existing note file: " + outFile.getName());
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
            fos.write(noteJson.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to open output stream.");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Failed to write file.");
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close fos.");
                }
            }
        }
        return true;
    }

    /**
     * Construct a patient note from the provided JSON string.
     *
     * @param jsonString JSON string containing patient note
     * @return PatientNote represented by JSON string or null if string could not be parsed.
     */
    public static PatientNote fromJson(String jsonString) {

        try {
            Gson gson = new GsonBuilder().setDateFormat(Common.ISO_DATETIME_FORMAT).create();
            // may throw parse exception
            JsonObject object = gson.fromJson(jsonString, JsonObject.class);

            return fromJsonObject(object);
        } catch (JsonParseException jpe) {
            Log.e(TAG, "Failed to parse note json.");
        }
        return null;
    }

    /**
     * Construct a patient note from the provided JSON object.
     *
     * @param jsonObject JSON Object containing patient note
     * @return PatientNote represented by JSON Object or null if object could not be parsed.
     */
    public static PatientNote fromJsonObject(JsonObject jsonObject) {
        DateFormat df = Util.getISOUTCFormatter();
        try {
            String responderId = jsonObject.get(JSONTag.RESPONDER_ID).getAsString();
            String patientId = jsonObject.get(JSONTag.PATIENT_ID).getAsString();
            String noteId = jsonObject.get(JSONTag.NOTE_ID).getAsString();

            // TODO: what is patient has not been created yet?
            Patient p = Patients.getInstance().getPatient(patientId);

            PatientNote note = new PatientNote(p);
            note.setResponderId(responderId);
            note.noteId = noteId;

            Date noteDate = df.parse(jsonObject.get(JSONTag.DATE).getAsString());

            note.setDate(noteDate);

            PatientTagHelper.BODY_PARTS bodyPart =
                    PatientTagHelper.BODY_PARTS.valueOf(jsonObject.get(JSONTag.NOTE_BODY_PART).getAsString());

            note.setSelectedBodyPart(bodyPart);

            JsonObject location = jsonObject.get(JSONTag.LOCATION).getAsJsonObject();
            double latitude = location.get(JSONTag.LOCATION_LAT).getAsDouble();
            double longitude = location.get(JSONTag.LOCATION_LNG).getAsDouble();
            double altitude = location.get(JSONTag.LOCATION_ALT).getAsDouble();

            note.setLatLngAlt(new LatLng(latitude, longitude), altitude);

            JsonArray noteItems = jsonObject.get(JSONTag.NOTE_CONTENTS).getAsJsonArray();

            for (JsonElement je : noteItems) {
                NoteItem item = noteItemFromJson(je.getAsJsonObject());
                note.addNoteItem(item);
            }
            // finish note
            note.finish();
            // return the note
            return note;
        } catch (ParseException pe) {
            Log.e(TAG, "Failed to parse note Date.");
        }
        return null;
    }

    /**
     * Build a patient note item from the provided JSON object
     *
     * @param json JSON Object to parse
     * @return NoteItem represented by the json object.
     */
    public static NoteItem noteItemFromJson(JsonObject json) {
        NoteItem.NOTE_TYPE type = NoteItem.NOTE_TYPE.valueOf(json.get(JSONTag.NOTE_ITEM_TYPE).getAsString());
        NoteItem rtnValue = null;
        switch (type) {
            case TEXT:
                rtnValue = new NoteItemText(json.get(JSONTag.NOTE_ITEM_MESSAGE).getAsString());
                break;
            case IMAGE:
                rtnValue = new NoteItemImage(json.get(JSONTag.NOTE_ITEM_FILE).getAsString());
                if (json.has(JSONTag.NOTE_ITEM_IMG)) {
                    String base64Image = json.get(JSONTag.NOTE_ITEM_IMG).getAsString();
                    decodeBase64Image(base64Image, json.get(JSONTag.NOTE_ITEM_FILE).getAsString());
                }
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

    private static void decodeBase64Image(String imageString, String imageName){
        File outFile = getImageOutputFile(imageName);

        if (outFile != null && !outFile.exists()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outFile);
                byte[] imgBytes = Base64.decode(imageString, Base64.NO_WRAP);
                fos.write(imgBytes);
            } catch (IOException e) {
                Log.e(TAG, "Exception when writing base64 image to file.");
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close output stream.");
                    }
                }
            }
        }
    }

    private static File getImageOutputFile(String filename) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), Common.PHOTO_DIR);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Required media storage does not exist");
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator + filename);

    }

}






