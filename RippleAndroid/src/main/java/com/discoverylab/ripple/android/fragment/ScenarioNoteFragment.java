package com.discoverylab.ripple.android.fragment;


import android.graphics.Bitmap;
import android.graphics.Color;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScenarioNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScenarioNoteFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private static final String TAG = ScenarioNoteFragment.class.getSimpleName();
    private static final int COLOR_TOLERANCE = 25;
    private static final int COLOR_FRONT_HEAD = Color.argb(255, 255, 255, 0);
    private static final int COLOR_FRONT_TORSO = Color.argb(255, 0, 255, 0);
    private static final int COLOR_FRONT_RIGHT_ARM = Color.argb(255, 255, 0, 0);
    private static final int COLOR_FRONT_LEFT_ARM = Color.argb(255, 0, 0, 255);
    private static final int COLOR_FRONT_RIGHT_LEG = Color.argb(255, 255, 0, 255);
    private static final int COLOR_FRONT_LEFT_LEG = Color.argb(255, 0, 255, 255);

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
                // get color of point user touched
                int touchColor = getTouchColor(R.id.patient_tag_painted, evX, evY);

                if (touchColor == -1) {
                    // invalid color
                    break;
                }
                // Check with closeMatch as display colors may not be exactly those specified due to scaling
                if (closeMatch(COLOR_FRONT_HEAD, touchColor, COLOR_TOLERANCE)) {

                    Toast.makeText(getActivity(), "Front head touched.", Toast.LENGTH_SHORT).show();
                } else if (closeMatch(COLOR_FRONT_TORSO, touchColor, COLOR_TOLERANCE)) {
                    Toast.makeText(getActivity(), "Front Torso touched.", Toast.LENGTH_SHORT).show();

                } else if (closeMatch(COLOR_FRONT_RIGHT_ARM, touchColor, COLOR_TOLERANCE)) {
                    Toast.makeText(getActivity(), "Front right arm touched.", Toast.LENGTH_SHORT).show();

                } else if (closeMatch(COLOR_FRONT_LEFT_ARM, touchColor, COLOR_TOLERANCE)) {
                    Toast.makeText(getActivity(), "Front left arm touched.", Toast.LENGTH_SHORT).show();

                } else if (closeMatch(COLOR_FRONT_RIGHT_LEG, touchColor, COLOR_TOLERANCE)) {
                    Toast.makeText(getActivity(), "Front right leg touched.", Toast.LENGTH_SHORT).show();

                } else if (closeMatch(COLOR_FRONT_LEFT_LEG, touchColor, COLOR_TOLERANCE)) {
                    Toast.makeText(getActivity(), "Front left leg touched.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "No body part touched.", Toast.LENGTH_SHORT).show();

                }

                break;
        }

        return true;
    }

    /**
     * Return the color of the pixel at coordinates (x,y) of the specified ImageView
     *
     * @param paintedImageId Resource id of image view
     * @param x              X coordinate of the pixel to retrieve
     * @param y              Y coordinate of the pixel to retrieve
     * @return Color of the pixel at (x,y) of the Imageview or -1 if point is outside the image area
     */
    private int getTouchColor(int paintedImageId, int x, int y) {
        int rtnValue = -1;
        if (x > 0 && y > 0) {

            ImageView img = (ImageView) getView().findViewById(paintedImageId);

            if (img != null) {
                img.setDrawingCacheEnabled(true);
                Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
                img.setDrawingCacheEnabled(false);
                if (x < hotspots.getWidth() && y < hotspots.getHeight()) {
                    rtnValue = hotspots.getPixel(x, y);
                }
            }
        }
        return rtnValue;
    }

    /**
     * Check if color1 and color2 are a close match (difference within the tolerance per channel)
     *
     * @param color1    An ARGB color value
     * @param color2    ARGB color to compare with the first
     * @param tolerance Allowable difference between the colors for each color channel
     * @return true if colors are within the tolerance of each other, false otherwise
     */
    private boolean closeMatch(int color1, int color2, int tolerance) {
        boolean rtnValue = true;
        if (Math.abs(Color.alpha(color1) - Color.alpha(color2)) > tolerance) {
            rtnValue = false;
        } else if (Math.abs(Color.red(color1) - Color.red(color2)) > tolerance) {
            rtnValue = false;
        } else if (Math.abs(Color.green(color1) - Color.green(color2)) > tolerance) {
            rtnValue = false;
        } else if (Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance) {
            rtnValue = false;
        }
        return rtnValue;
    }


}
