package com.discoverylab.ripple.android.object;

import com.google.gson.JsonObject;

/**
 * Basic component of a patient note.
 * <p/>
 * Created by james on 7/16/14.
 */
public interface NoteItem {

    public enum NOTE_TYPE {TEXT, IMAGE, VOICE, DRUG, ECG}

    public NOTE_TYPE getNoteType();

    public JsonObject getJsonObject();
}
