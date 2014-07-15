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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScenarioNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScenarioNoteFragment extends Fragment implements View.OnTouchListener {

    private static final int COLOR_TOLERANCE = 25;
    private static final int COLOR_FRONT_HEAD = Color.argb(255, 255, 255, 0);
    private static final int COLOR_FRONT_TORSO = Color.argb(255, 0, 255, 0);
    private static final int COLOR_FRONT_RIGHT_ARM = Color.argb(255, 255, 0, 0);
    private static final int COLOR_FRONT_LEFT_ARM = Color.argb(255, 0, 0, 255);
    private static final int COLOR_FRONT_RIGHT_LEG = Color.argb(255, 255, 0, 255);
    private static final int COLOR_FRONT_LEFT_LEG = Color.argb(255, 0, 255, 255);


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

        ImageView tag = (ImageView) v.findViewById(R.id.patient_tag);
        tag.setOnTouchListener(this);

        return v;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int action = event.getAction();
        final int evX = (int) event.getX();
        final int evY = (int) event.getY();

        Log.d("lkahtse", "ACTION: " + action + " at (x,y) " + evX + ", " + evY);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                int touchColor = getTouchColor(R.id.patient_tag_painted, evX, evY);

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

    private int getTouchColor(int paintedImageId, int evX, int evY) {
        ImageView img = (ImageView) getView().findViewById(paintedImageId);

        img.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
        img.setDrawingCacheEnabled(false);
        return hotspots.getPixel(evX, evY);

    }

    private boolean closeMatch(int color1, int color2, int tolerance) {
        if ((int) Math.abs(Color.red(color1) - Color.red(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.green(color1) - Color.green(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance)
            return false;
        return true;
    }
}
