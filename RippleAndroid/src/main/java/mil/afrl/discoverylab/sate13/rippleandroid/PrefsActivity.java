package mil.afrl.discoverylab.sate13.rippleandroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import mil.afrl.discoverylab.sate13.rippleandroid.fragment.PrefsFragment;

/**
 * Created by james on 8/7/13.
 */
public class PrefsActivity extends PreferenceActivity {

    private PrefsFragment prefFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null)
        {
            // First run of activity, need to add fragment
            this.prefFragment = new PrefsFragment();
            getFragmentManager().beginTransaction().replace(android.R.id.content, this.prefFragment).commit();
        } else {

            this.prefFragment = (PrefsFragment)getFragmentManager().findFragmentById(android.R.id.content);
        }
    }
}
