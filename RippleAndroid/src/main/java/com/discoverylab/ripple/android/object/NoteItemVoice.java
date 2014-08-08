package com.discoverylab.ripple.android.object;

import com.discoverylab.ripple.android.config.JSONTag;
import com.google.gson.JsonObject;

/**
 * Voice note.
 * <p/>
 * Created by james on 7/16/14.
 */
public class NoteItemVoice implements NoteItem {
    @Override
    public NOTE_TYPE getNoteType() {
        return NOTE_TYPE.VOICE;
    }

    @Override
    public JsonObject getJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty(JSONTag.NOTE_ITEM_TYPE, this.getNoteType().toString());


        return object;
    }
}
