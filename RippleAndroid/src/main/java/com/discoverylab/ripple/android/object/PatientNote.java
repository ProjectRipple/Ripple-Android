package com.discoverylab.ripple.android.object;

import com.discoverylab.ripple.android.util.PatientTagHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by james on 7/16/14.
 */
public class PatientNote {

    private List<NoteItem> noteItems = new ArrayList<NoteItem>();
    private Date mDateTime;
    private PatientTagHelper.BODY_PARTS selectedBodyPart = PatientTagHelper.BODY_PARTS.NONE;
    private boolean isFinished = false;

    public boolean addNoteItem(NoteItem item) {
        if (isFinished) {
            return false;
        }
        return this.noteItems.add(item);
    }

    public int getNumNoteItems() {
        return this.noteItems.size();
    }

    /**
     * @return An unmodifiable list referencing this objects note items
     */
    public List<NoteItem> getNoteItems() {
        return Collections.unmodifiableList(this.noteItems);
    }

    public boolean setSelectedBodyPart(PatientTagHelper.BODY_PARTS bodyPart) {
        if (isFinished) {
            return false;
        }
        this.selectedBodyPart = bodyPart;
        return true;
    }

    public PatientTagHelper.BODY_PARTS getSelectedBodyPart(){
        return this.selectedBodyPart;
    }

    public boolean setDate(Date date){
        if(isFinished){
            return false;
        }
        this.mDateTime = date;
        return true;
    }

    public Date getDate(){
        return new Date(this.mDateTime.getTime());
    }

    /**
     * Finish the note, making it unmodifiable. All add/set functions on this
     * object will return false after this function is called.
     */
    public void finish() {
        this.isFinished = true;
    }

    public boolean isFinished() {
        return this.isFinished();
    }


}
