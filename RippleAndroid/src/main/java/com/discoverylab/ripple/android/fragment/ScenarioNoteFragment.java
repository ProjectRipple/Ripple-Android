package com.discoverylab.ripple.android.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discoverylab.ripple.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScenarioNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ScenarioNoteFragment extends Fragment {
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
        return inflater.inflate(R.layout.fragment_scenario_note, container, false);
    }


}
