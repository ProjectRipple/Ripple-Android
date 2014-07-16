package com.discoverylab.ripple.android.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.util.PatientTagHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScenarioNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScenarioNoteFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private static final String TAG = ScenarioNoteFragment.class.getSimpleName();


    private static final String ADD_NOTE_FRAG_TAG = "AddNoteFragment";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScenarioNoteFragment.
     */
    public static ScenarioNoteFragment newInstance() {
        ScenarioNoteFragment fragment = new ScenarioNoteFragment();

        return fragment;
    }

    public ScenarioNoteFragment() {
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
        View v = inflater.inflate(R.layout.fragment_scenario_note, container, false);

        Button addNote = (Button) v.findViewById(R.id.scenario_note_add_note);
        Button viewNotes = (Button) v.findViewById(R.id.scenario_note_view_notes);

        addNote.setOnClickListener(this);
        viewNotes.setOnClickListener(this);

        ImageView tag = (ImageView) v.findViewById(R.id.patient_tag);
        tag.setOnTouchListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scenario_note_add_note:
                PatientNoteFragment noteFragment = PatientNoteFragment.newInstance();
                noteFragment.show(getFragmentManager(), ADD_NOTE_FRAG_TAG);
                break;
            case R.id.scenario_note_view_notes:
                Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.d(TAG, "Unknown item clicked.");
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

                    switch (partSelected) {
                        case FRONT_HEAD:
                            Toast.makeText(getActivity(), "Front Head touched.", Toast.LENGTH_SHORT).show();
                            break;
                        case FRONT_TORSO:
                            Toast.makeText(getActivity(), "Front Torso touched.", Toast.LENGTH_SHORT).show();
                            break;
                        case FRONT_LEFT_ARM:
                            Toast.makeText(getActivity(), "Front Left Arm touched.", Toast.LENGTH_SHORT).show();
                            break;
                        case FRONT_RIGHT_ARM:
                            Toast.makeText(getActivity(), "Front Right Arm touched.", Toast.LENGTH_SHORT).show();
                            break;
                        case FRONT_LEFT_LEG:
                            Toast.makeText(getActivity(), "Front Left Leg touched.", Toast.LENGTH_SHORT).show();
                            break;
                        case FRONT_RIGHT_LEG:
                            Toast.makeText(getActivity(), "Front Right Leg touched.", Toast.LENGTH_SHORT).show();
                            break;
                        case NONE:
                            break;
                    }
                }

                break;
        }

        return true;
    }


}
