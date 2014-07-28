package com.discoverylab.ripple.android.adapter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.object.NoteItem;
import com.discoverylab.ripple.android.object.PatientNote;
import com.discoverylab.ripple.android.util.Util;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by james on 7/28/14.
 */
public class NoteListAdapter extends BaseExpandableListAdapter {

    private final List<PatientNote> notes;
    private final Context context;
    private final DateFormat dateFormat;

    public NoteListAdapter(Context ctx, List<PatientNote> notes) {
        super();
        this.context = ctx;
        this.notes = notes;
        this.dateFormat = Util.getISOUTCFormatter();
    }

    @Override
    public int getGroupCount() {
        return this.notes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // for now, each notes just have one child with all the note items
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.notes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // no real children at the moment
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // no group ids
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // no child ids
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // no ids
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.note_list_group, parent, false);
        }


        PatientNote note = this.notes.get(groupPosition);

        TextView date = (TextView) v.findViewById(R.id.note_list_group_title);

        date.setText(this.dateFormat.format(note.getDate()));

        ImageView text = (ImageView) v.findViewById(R.id.note_list_group_text);
        ImageView image = (ImageView) v.findViewById(R.id.note_list_group_image);
        ImageView voice = (ImageView) v.findViewById(R.id.note_list_group_voice);
        ImageView rx = (ImageView) v.findViewById(R.id.note_list_group_rx);
        ImageView ecg = (ImageView) v.findViewById(R.id.note_list_group_ecg);

        text.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);
        voice.setVisibility(View.INVISIBLE);
        rx.setVisibility(View.INVISIBLE);
        ecg.setVisibility(View.INVISIBLE);

        // set visibility of icons if note has those types
        List<NoteItem> noteItems = note.getNoteItems();
        for(NoteItem item : noteItems){
            switch (item.getNoteType()){
                case TEXT:
                    text.setVisibility(View.VISIBLE);
                    break;
                case IMAGE:
                    image.setVisibility(View.VISIBLE);
                    break;
                case VOICE:
                    voice.setVisibility(View.VISIBLE);
                    break;
                case DRUG:
                    rx.setVisibility(View.VISIBLE);
                    break;
                case ECG:
                    ecg.setVisibility(View.VISIBLE);
                    break;
            }
        }

        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView view = new TextView(this.context);
        view.setText("Dummy note item.");
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // no child is selectable at the moment
        return false;
    }
}
