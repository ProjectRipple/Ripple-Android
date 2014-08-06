package com.discoverylab.ripple.android.adapter.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.object.NoteItem;
import com.discoverylab.ripple.android.object.NoteItemImage;
import com.discoverylab.ripple.android.object.NoteItemText;
import com.discoverylab.ripple.android.object.PatientNote;
import com.discoverylab.ripple.android.util.Util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adapter for Expandable List View of notes for the selected patient.
 * <p/>
 * Created by james on 7/28/14.
 */
public class NoteListAdapter extends BaseExpandableListAdapter {

    // List of patient notes to show
    private final List<PatientNote> notes;
    // Reference to context for inflater
    private final Context context;
    // Date formatter for printing date
    private final DateFormat dateFormat;
    // Comparator to sort patient notes by date
    private final Comparator<PatientNote> noteDateComparator;

    /**
     * @param ctx   Context for this adapter
     * @param notes List of notes for adapter to show. Adapter just copies
     *              references from this list to its own internal List object.
     */
    public NoteListAdapter(Context ctx, List<PatientNote> notes) {
        super();
        this.context = ctx;
        this.notes = new ArrayList<PatientNote>(notes.size());
        this.notes.addAll(notes);
        this.dateFormat = Util.getBasicUTCFormatter();
        this.noteDateComparator = new NoteDateComparator();
        Collections.sort(this.notes, this.noteDateComparator);
    }

    /**
     * Clear adapter list and notify data set changed.
     */
    public void clearList() {
        this.notes.clear();
        this.notifyDataSetChanged();
    }

    /**
     * Clear existing list, add all items from given list, and notify data set changed.
     *
     * @param notes List of notes to now show.
     */
    public void setNotes(List<PatientNote> notes) {
        this.notes.clear();
        this.notes.addAll(notes);
        Collections.sort(this.notes, this.noteDateComparator);
        this.notifyDataSetChanged();
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
        // Do not allow focus from keyboard
        v.setFocusable(false);

        // Get note
        PatientNote note = this.notes.get(groupPosition);

        TextView date = (TextView) v.findViewById(R.id.note_list_group_title);

        date.setText(this.dateFormat.format(note.getDate()));

        TextView bodyPart = (TextView) v.findViewById(R.id.note_list_group_body_part);
        bodyPart.setText(note.getSelectedBodyPart().getPrintableString());

        ImageView bodyHighlight = (ImageView) v.findViewById(R.id.note_list_group_body_part_highlight);
        bodyHighlight.setAlpha((float) 1.0);
        switch (note.getSelectedBodyPart()) {
            case FRONT_HEAD:
                bodyHighlight.setImageResource(R.drawable.tag_small_highlight_front_head);
                break;
            case FRONT_TORSO:
                bodyHighlight.setImageResource(R.drawable.tag_small_highlight_front_torso);
                break;
            case FRONT_LEFT_ARM:
                bodyHighlight.setImageResource(R.drawable.tag_small_highlight_front_left_arm);
                break;
            case FRONT_RIGHT_ARM:
                bodyHighlight.setImageResource(R.drawable.tag_small_highlight_front_right_arm);
                break;
            case FRONT_LEFT_LEG:
                bodyHighlight.setImageResource(R.drawable.tag_small_highlight_front_left_leg);
                break;
            case FRONT_RIGHT_LEG:
                bodyHighlight.setImageResource(R.drawable.tag_small_highlight_front_right_leg);
                break;
            case NONE:
                bodyHighlight.setAlpha((float) 0.0);
                break;
        }

        ImageView text = (ImageView) v.findViewById(R.id.note_list_group_text);
        ImageView image = (ImageView) v.findViewById(R.id.note_list_group_image);
        ImageView voice = (ImageView) v.findViewById(R.id.note_list_group_voice);
        TextView rx = (TextView) v.findViewById(R.id.note_list_group_rx);
        ImageView ecg = (ImageView) v.findViewById(R.id.note_list_group_ecg);

        text.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);
        voice.setVisibility(View.INVISIBLE);
        rx.setVisibility(View.INVISIBLE);
        ecg.setVisibility(View.INVISIBLE);

        // set visibility of icons if note has those types
        List<NoteItem> noteItems = note.getNoteItems();
        for (NoteItem item : noteItems) {
            switch (item.getNoteType()) {
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

        ImageView indicator = (ImageView) v.findViewById(R.id.note_list_group_indicator);

        if (isExpanded) {
            indicator.setImageResource(R.drawable.ic_action_collapse);
        } else {
            indicator.setImageResource(R.drawable.ic_action_expand);
        }

        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.note_list_child, parent, false);
        }
        // Do not allow focus from keyboard
        v.setFocusable(false);

        LinearLayout childLayout = (LinearLayout) v.findViewById(R.id.note_list_child_layout);
        // make sure not old views still there
        childLayout.removeAllViews();

        PatientNote note = this.notes.get(groupPosition);

        List<NoteItem> noteItems = note.getNoteItems();
        for (NoteItem item : noteItems) {
            switch (item.getNoteType()) {
                case TEXT:
                    // Add text to layout
                    TextView text = new TextView(this.context);
                    text.setText(((NoteItemText) item).getNoteText());
                    childLayout.addView(text);
                    break;
                case IMAGE:
                    // Add image to layout
                    ImageView img = new ImageView(this.context);
                    Bitmap imageFromFile = BitmapFactory.decodeFile(((NoteItemImage) item).getImagePath());
                    if (imageFromFile != null) {
                        img.setImageBitmap(imageFromFile);
                    } else {
                        img.setImageResource(R.drawable.image_placeholder);
                    }

                    int sizePixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, this.context.getResources().getDisplayMetrics());
                    img.setMaxWidth(sizePixels);
                    img.setMaxHeight(sizePixels);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.width = sizePixels;
                    params.height = sizePixels;
                    img.setLayoutParams(params);

                    childLayout.addView(img);
                    break;
                case VOICE:

                    break;
                case DRUG:

                    break;
                case ECG:

                    break;
            }
        }

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // no child is selectable at the moment
        return false;
    }

    /**
     * Comparator to compare two {@link PatientNote} based on date so that
     * the newest Note is at the top of the list.
     */
    private class NoteDateComparator implements Comparator<PatientNote> {

        @Override
        public int compare(PatientNote lhs, PatientNote rhs) {
            long lhsTime = lhs.getDate().getTime();
            long rhsTime = rhs.getDate().getTime();
            if (lhsTime < rhsTime) {
                // want newest at top, so return positive number for this comparison
                return 1;
            } else if (lhsTime > rhsTime) {
                return -1;
            } else {
                return 0;
            }
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }
    }
}
