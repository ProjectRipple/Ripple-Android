package com.discoverylab.ripple.android.object;

import android.os.Environment;

import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.google.gson.JsonObject;

import java.io.File;

/**
 * Note Item for an image note.
 * <p/>
 * Created by james on 7/16/14.
 */
public class NoteItemImage implements NoteItem {

    // System path to image
    private final String imageName;

    public NoteItemImage(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Get the full image path to the image file.
     *
     * @return Full image path to the file.
     */
    public String getImagePath() {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), Common.PHOTO_DIR).getPath()
                + File.separator + imageName;
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public NOTE_TYPE getNoteType() {
        return NOTE_TYPE.IMAGE;
    }

    @Override
    public JsonObject getJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty(JSONTag.NOTE_ITEM_TYPE, this.getNoteType().toString());

        object.addProperty(JSONTag.NOTE_ITEM_FILE, this.imageName);

        return object;
    }
}
