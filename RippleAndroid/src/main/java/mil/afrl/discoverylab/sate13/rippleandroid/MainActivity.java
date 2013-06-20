package mil.afrl.discoverylab.sate13.rippleandroid;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import mil.afrl.discoverylab.sate13.rippleandroid.adapter.DatabaseAdapter;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.Banner;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient.PatientLeft;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.scene.SceneLeft;


public class MainActivity extends Activity implements ActivityClickInterface {

    private boolean isPatient = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Banner banner = new Banner();
        transaction.add(R.id.top_frag, banner);

        transaction.commit();

        DatabaseAdapter.getInstance(this).storeScanData(1, "ip", "first", "last", "ssn", 1, "M", 1, "type");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
        //We don't need menus right now
        return false;
    }

    @Override
    public void onClickListener(int id) {
        Fragment fragment = (isPatient) ? new SceneLeft() : new PatientLeft();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.bottomleft, fragment);
        transaction.commit();

        isPatient = !isPatient;
    }
}
