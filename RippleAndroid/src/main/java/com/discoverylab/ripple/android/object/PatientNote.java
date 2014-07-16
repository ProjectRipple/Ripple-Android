package com.discoverylab.ripple.android.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by james on 7/16/14.
 */
public class PatientNote {

    private List<NoteItem> noteItems = new ArrayList<NoteItem>();

    public boolean addNoteItem(NoteItem item) {
        return this.noteItems.add(item);
    }

    public int getNumNoteItems() {
        return this.noteItems.size();
    }

    /**
     *
     * @return An unmodifiable list referencing this objects note items
     */
    public List<NoteItem> getNoteItems(){
        return Collections.unmodifiableList(this.noteItems);
    }
}
