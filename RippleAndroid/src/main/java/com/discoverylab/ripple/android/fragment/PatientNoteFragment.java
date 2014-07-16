package com.discoverylab.ripple.android.fragment;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.util.PatientTagHelper;
import com.discoverylab.ripple.android.util.PatientTagHelper.BODY_PARTS;

import java.util.ArrayList;
import java.util.List;

/**
 * Use the {@link PatientNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientNoteFragment extends DialogFragment implements View.OnTouchListener, View.OnClickListener {

    private static final String TAG = PatientNoteFragment.class.getSimpleName();
    private ImageView tagHighlight;
    private BODY_PARTS selectedBodyPart = BODY_PARTS.NONE;
    private LinearLayout noteItemsLayout;
    private List<View> noteViews = new ArrayList<View>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientNoteFragment.
     */
    public static PatientNoteFragment newInstance() {
        PatientNoteFragment fragment = new PatientNoteFragment();
        return fragment;
    }

    public PatientNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_patient_note, container, false);

        this.noteItemsLayout = (LinearLayout) v.findViewById(R.id.patient_note_items_layout);

        ImageView tag = (ImageView) v.findViewById(R.id.patient_tag);
        tag.setOnTouchListener(this);

        this.tagHighlight = (ImageView) v.findViewById(R.id.patient_tag_highlight);
        this.tagHighlight.setAlpha((float) 0.4);

        ImageButton textNote = (ImageButton) v.findViewById(R.id.patient_note_add_text);
        ImageButton imageNote = (ImageButton) v.findViewById(R.id.patient_note_add_image);
        ImageButton voiceNote = (ImageButton) v.findViewById(R.id.patient_note_add_voice);
        Button drugNote = (Button) v.findViewById(R.id.patient_note_add_drug);
        Button ecgNote = (Button) v.findViewById(R.id.patient_note_add_ecg);

        textNote.setOnClickListener(this);
        imageNote.setOnClickListener(this);
        voiceNote.setOnClickListener(this);
        drugNote.setOnClickListener(this);
        ecgNote.setOnClickListener(this);


        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remove view references
        this.noteItemsLayout = null;
        this.tagHighlight = null;
        this.noteViews.clear();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.patient_note_add_text:
                finishCurrentNote();
                addTextNote();
                break;
            case R.id.patient_note_add_image:
                finishCurrentNote();
                addImageNote();
                break;
            case R.id.patient_note_add_voice:
                finishCurrentNote();
                addVoiceNote();
                break;
            case R.id.patient_note_add_drug:
                finishCurrentNote();
                addDrugNote();
                break;
            case R.id.patient_note_add_ecg:
                finishCurrentNote();
                addEcgNote();
                break;
        }
    }

    private void addTextNote() {
        EditText textNote = new EditText(getActivity());
        textNote.setMaxLines(3);
        textNote.setTextColor(getResources().getColor(R.color.black));
        this.noteViews.add(textNote);
        this.noteItemsLayout.addView(textNote);
    }

    private void addImageNote() {

    }

    private void addVoiceNote() {

    }

    private void addDrugNote() {

    }

    private void addEcgNote() {

    }

    private void finishCurrentNote() {
        if(this.noteViews.size() == 0){
            return;
        }
        if(this.noteViews.get(this.noteViews.size() - 1) instanceof EditText){
            EditText textNote = (EditText) this.noteViews.get(this.noteViews.size() - 1);
            textNote.setEnabled(false);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int action = event.getAction();
        final int evX = (int) event.getX();
        final int evY = (int) event.getY();

        Log.d(TAG, "ACTION: " + action + " at (x,y) " + evX + ", " + evY);

        switch (action) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                if (evX >= 0 && evY >= 0) {
                    ImageView img = (ImageView) getView().findViewById(R.id.patient_tag_painted);
                    img.setDrawingCacheEnabled(true);
                    Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
                    img.setDrawingCacheEnabled(false);

                    PatientTagHelper.BODY_PARTS partSelected = PatientTagHelper.getBodyPartSelected(hotspots, evX, evY);

                    if (this.selectedBodyPart == partSelected) {
                        // deselecting
                        this.selectedBodyPart = BODY_PARTS.NONE;
                        this.tagHighlight.setImageDrawable(null);
                    } else {
                        this.selectedBodyPart = partSelected;

                        switch (partSelected) {
                            case FRONT_HEAD:
                                Toast.makeText(getActivity(), "Front Head touched.", Toast.LENGTH_SHORT).show();
                                this.tagHighlight.setImageResource(R.drawable.tag_highlight_head);
                                break;
                            case FRONT_TORSO:
                                Toast.makeText(getActivity(), "Front Torso touched.", Toast.LENGTH_SHORT).show();
                                this.tagHighlight.setImageResource(R.drawable.tag_highlight_torso);
                                break;
                            case FRONT_LEFT_ARM:
                                Toast.makeText(getActivity(), "Front Left Arm touched.", Toast.LENGTH_SHORT).show();
                                this.tagHighlight.setImageResource(R.drawable.tag_highlight_leftarm);
                                break;
                            case FRONT_RIGHT_ARM:
                                Toast.makeText(getActivity(), "Front Right Arm touched.", Toast.LENGTH_SHORT).show();
                                this.tagHighlight.setImageResource(R.drawable.tag_highlight_rightarm);
                                break;
                            case FRONT_LEFT_LEG:
                                Toast.makeText(getActivity(), "Front Left Leg touched.", Toast.LENGTH_SHORT).show();
                                this.tagHighlight.setImageResource(R.drawable.tag_highlight_leftleg);
                                break;
                            case FRONT_RIGHT_LEG:
                                Toast.makeText(getActivity(), "Front Right Leg touched.", Toast.LENGTH_SHORT).show();
                                this.tagHighlight.setImageResource(R.drawable.tag_highlight_rightleg);
                                break;
                            case NONE:
                                this.tagHighlight.setImageDrawable(null);
                                break;
                        }
                    }

                }

                break;
        }

        return true;
    }

}
