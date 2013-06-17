package mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mil.afrl.discoverylab.sate13.rippleandroid.ActivityClickInterface;
import mil.afrl.discoverylab.sate13.rippleandroid.R;

/**
 *
 * Created by Brandon on 6/17/13.
 */
public class PatientLeft extends Fragment implements View.OnClickListener {

    ActivityClickInterface aci;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_left, container, false);

        assert view != null;

        Button button = (Button) view.findViewById(R.id.button2);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        aci = (ActivityClickInterface)activity;
    }

    @Override
    public void onClick(View view) {
        aci.onClickListener(view.getId());
    }
}
