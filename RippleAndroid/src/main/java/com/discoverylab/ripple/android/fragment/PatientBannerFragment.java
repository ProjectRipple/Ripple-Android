package com.discoverylab.ripple.android.fragment;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discoverylab.ripple.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PatientBannerFragment extends Fragment {


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientBannerFragment.
     */
    public static PatientBannerFragment newInstance() {
        PatientBannerFragment fragment = new PatientBannerFragment();
        return fragment;
    }
    public PatientBannerFragment() {
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
        View v = inflater.inflate(R.layout.fragment_patient_banner, container, false);

        LinearLayout viewLayout = (LinearLayout) v.findViewById(R.id.patient_banner_view_layout);

        // TODO: remove after debugging
        for(int i = 0; i < 20; i++){
            TextView textView = new TextView(getActivity());
            textView.setText("Patient " + i + "! ");
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            viewLayout.addView(textView);
        }

        return v;
    }


}
