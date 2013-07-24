package mil.afrl.discoverylab.sate13.rippleandroid;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import mil.afrl.discoverylab.sate13.rippleandroid.fragment.Banner;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient.PatientLeft;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.scene.SceneLeft;


public class MainActivity extends Activity implements ActivityClickInterface, LocationSource.OnLocationChangedListener, View.OnClickListener {

    /*Inter-Fragment MGMT*/
    private boolean isPatient = true;
    private Banner banner;
    private PatientLeft patLeft;

    /*Mapping Vars*/
    private GoogleMap map;
    private LocationManager lm;

    /*Network Clients Message Handler*/
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == Common.NW_MSG_TYPES.UDP_BANNER_VITAL.ordinal()) {

            } else if (msg.what == Common.NW_MSG_TYPES.TCP_STREAM_VITAL.ordinal()) {
                //Banner.update((String) msg.obj);
            } else {
                Log.e(Common.LOG_TAG, "Unknown Network Message type: " + msg.what);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(savedInstanceState == null)
        {
            // First run of activity, need to add banner
            banner = new Banner();
            transaction.add(R.id.top_frag, banner);
            transaction.commit();
        } else {
            // Banner already exists from first run, just need to grab it.
            banner = (Banner)fragmentManager.findFragmentById(R.id.top_frag);
        }


        patLeft = (PatientLeft) fragmentManager.findFragmentById(R.id.bottomleft);

        initMap();
    }

    private void initMap() {

        MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = fm.getMap();
        map.setMyLocationEnabled(true);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, true);
        Location location = lm.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }
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

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onClick(View view) {
        if(view instanceof PatientView)
        {
            PatientView pView = (PatientView)view;
            Log.d(Common.LOG_TAG, "Patient id selected: " + pView.getPid());
            this.patLeft.setPatient(pView.getPid());
        }
    }
}
