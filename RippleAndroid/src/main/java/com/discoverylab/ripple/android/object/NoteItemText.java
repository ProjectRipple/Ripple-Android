package com.discoverylab.ripple.android.object;

import com.discoverylab.ripple.android.config.JSONTag;
import com.google.gson.JsonObject;

/**
 * Note item for a text note.
 * <p/>
 * Created by james on 7/16/14.
 */
public class NoteItemText implements NoteItem {

    private final String noteText;

    public NoteItemText(String text) {
        this.noteText = text;
    }

    public String getNoteText() {
        return this.noteText;
    }


    @Override
    public NOTE_TYPE getNoteType() {
        return NOTE_TYPE.TEXT;
    }

    @Override
    public JsonObject getJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty(JSONTag.NOTE_ITEM_TYPE, this.getNoteType().toString());

        object.addProperty(JSONTag.NOTE_ITEM_MESSAGE, this.noteText);

        return object;
    }
}
