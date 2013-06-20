package mil.afrl.discoverylab.sate13.rippleandroid;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.Window;

import mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient.PatientLeft;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.scene.SceneLeft;

public class MainActivity extends FragmentActivity implements ActivityClickInterface{

    private boolean isPatient = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        PatientLeft patientLeft = new PatientLeft();
        transaction.add(R.id.bottomleft, patientLeft);
        transaction.commit();
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
