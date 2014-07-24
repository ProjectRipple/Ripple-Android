package com.discoverylab.ripple.android.object;

import com.discoverylab.ripple.android.config.JSONTag;
import com.google.gson.JsonObject;

/**
 * Note Item for an image note.
 * <p/>
 * Created by james on 7/16/14.
 */
public class NoteItemImage implements NoteItem {

    // System path to image
    private final String imagePath;

    public NoteItemImage(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public NOTE_TYPE getNoteType() {
        return NOTE_TYPE.IMAGE;
    }

    @Override
    public JsonObject getJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty(JSONTag.NOTE_ITEM_TYPE, this.getNoteType().toString());

        object.addProperty(JSONTag.NOTE_ITEM_FILE, this.imagePath);

        return object;
    }
}
