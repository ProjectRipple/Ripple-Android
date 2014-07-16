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
import android.widget.ImageView;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.util.PatientTagHelper;
import com.discoverylab.ripple.android.util.PatientTagHelper.BODY_PARTS;

/**
 * Use the {@link PatientNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientNoteFragment extends DialogFragment implements View.OnTouchListener {

    private static final String TAG = PatientNoteFragment.class.getSimpleName();
    private ImageView tagHighlight;
    private BODY_PARTS selectedBodyPart = BODY_PARTS.NONE;

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

        ImageView tag = (ImageView) v.findViewById(R.id.patient_tag);
        tag.setOnTouchListener(this);

        this.tagHighlight = (ImageView) v.findViewById(R.id.patient_tag_highlight);
        this.tagHighlight.setAlpha((float) 0.4);



        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
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
