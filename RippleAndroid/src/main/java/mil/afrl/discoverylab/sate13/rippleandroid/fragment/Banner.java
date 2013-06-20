package mil.afrl.discoverylab.sate13.rippleandroid.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.PatientView;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.object.Patient;

/**
 * This displays the banner at the top of the display
 *
 * Created by harmonbc on 6/19/13.
 */
public class Banner extends Fragment {

    ArrayList<Patient> mPatients;
    private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewgroup, Bundle bundle){
        super.onCreate(bundle);
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.banner, viewgroup, false);
        TableLayout tableLayout = (TableLayout)view.findViewById(R.id.bannerTableLayout);
        mPatients = new ArrayList<Patient>();

        TableRow tableRow = (TableRow)view.findViewById(R.id.bannerTableRow);

        //This is only here for debugging purposes till we start generating patients.
        for(int i = 0; i < 20; i++){
            mPatients.add(new Patient());

            //Implements a custom view, the custom view is passed the patient object
            PatientView v = new PatientView(mContext, mPatients.get(i), i);
            v.setMinimumHeight(100);
            v.setMinimumWidth(200);
            tableRow.addView(v);
        }
//        tableLayout.addView(tableRow, new TableLayout.LayoutParams());
        return view;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mContext = activity;
    }
}
