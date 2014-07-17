package com.discoverylab.ripple.android.object;

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
}
