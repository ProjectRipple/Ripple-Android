package com.discoverylab.ripple.android.object;

/**
 * Created by james on 7/16/14.
 */
public class NoteItemDrug implements NoteItem {
    @Override
    public NOTE_TYPE getNoteType() {
        return NOTE_TYPE.DRUG;
    }
}
