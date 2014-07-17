package com.discoverylab.ripple.android.object;

/**
 * Created by james on 7/16/14.
 */
public class NoteItemImage implements NoteItem {

    // System path to image
    private final String imagePath;

    public NoteItemImage(String imagePath){
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public NOTE_TYPE getNoteType() {
        return NOTE_TYPE.IMAGE;
    }
}
