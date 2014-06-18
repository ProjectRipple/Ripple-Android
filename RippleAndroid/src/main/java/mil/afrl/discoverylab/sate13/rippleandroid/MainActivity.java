package mil.afrl.discoverylab.sate13.rippleandroid;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;

import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.Banner;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.PrefsFragment;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient.PatientLeft;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.scene.SceneLeft;
import mil.afrl.discoverylab.sate13.rippleandroid.mqtt.MQTTClientService;
import mil.afrl.discoverylab.sate13.rippleandroid.mqtt.MQTTServiceConstants;
import mil.afrl.discoverylab.sate13.rippleandroid.mqtt.MQTTServiceManager;
import mil.afrl.discoverylab.sate13.rippleandroid.mqtt.PublishedMessage;


public class MainActivity extends Activity implements ActivityClickInterface, LocationSource.OnLocationChangedListener, View.OnClickListener {

    /*Inter-Fragment MGMT*/
    private boolean isPatient = true;
    private Banner banner;
    private PatientLeft patLeft;

    /*Mapping Vars*/
    private GoogleMap map;
    private LocationManager lm;

    // MQTT
    private MQTTServiceManager mqttServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (savedInstanceState == null) {
            // First run of activity, need to add banner
            banner = new Banner();
            transaction.add(R.id.top_frag, banner);
            transaction.commit();
        } else {
            // Banner already exists from first run, just need to grab it.
            banner = (Banner) fragmentManager.findFragmentById(R.id.top_frag);
        }


        patLeft = (PatientLeft) fragmentManager.findFragmentById(R.id.bottomleft);
        patLeft.setBannerHandler(banner.getHandler());

        initMap();

        mqttServiceManager = new MQTTServiceManager(this, MQTTClientService.class, new MQTTHandler(this));
    }

    private void initMap() {

        MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        // Map is only there for larger screens
        if (fm != null) {
            map = fm.getMap();
            // map may be null if tablet is rotated from landscape to portrait, which will remove the map fragment from user's view
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        }
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, true);
        Location location = lm.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.mqttServiceManager != null && this.mqttServiceManager.isServiceRunning()) {
            this.mqttServiceManager.unbind();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.mqttServiceManager != null && this.mqttServiceManager.isServiceRunning()) {
            this.mqttServiceManager.bind();
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
        // map may be null for smaller devices
        if (map != null) {
            // Showing the current location in Google Map
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof PatientView) {
            PatientView pView = (PatientView) view;
            //Log.d(Common.LOG_TAG, "Patient id selected: " + pView.getPid());
            //this.patLeft.setPatient(pView.getPid());
            Log.d(Common.LOG_TAG, "Patient src selected: " + pView.getPatientSrc());
            this.patLeft.setPatientSrc(pView.getPatientSrc());
        }
    }

    public void startMQTTService() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.mqttServiceManager.start(prefs.getString(PrefsFragment.IP_FROM_PREFS, WSConfig.DEFAULT_IP), prefs.getString(PrefsFragment.PORT_NUM_MQTT_PREFS, WSConfig.DEFAULT_MQTT_PORT));
    }

    public void stopMQTTService() {
        if (this.mqttServiceManager != null && this.mqttServiceManager.isServiceRunning()) {
            this.mqttServiceManager.stop();
        }
    }

    public boolean isMQTTServiceRunning() {
        return this.mqttServiceManager.isServiceRunning();
    }

    public void subscribeToTopic(String topicName) {
        Message msg = Message.obtain(null, MQTTServiceConstants.MSG_SUBSCRIBE);
        Bundle bundle = new Bundle();
        bundle.putString(MQTTServiceConstants.MQTT_TOPIC, topicName);
        msg.setData(bundle);
        try {
            this.mqttServiceManager.send(msg);
            Log.d(Common.LOG_TAG, "Subscribed for: " + topicName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsubscribeFromTopic(String topicName) {
        Message msg = Message.obtain(null, MQTTServiceConstants.MSG_UNSUBSCRIBE);
        Bundle bundle = new Bundle();
        bundle.putString(MQTTServiceConstants.MQTT_TOPIC, topicName);
        msg.setData(bundle);
        try {
            this.mqttServiceManager.send(msg);
            Log.d(Common.LOG_TAG, "Unsubscribed from: " + topicName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processPublishedMessage(PublishedMessage msg) {
        String topic = msg.getTopic();
        if (topic.equals(Common.MQTT_TOPIC_VITALPROP) || topic.matches(Common.MQTT_TOPIC_MATCH_VITALCAST)) {
            JsonObject recordJson = Common.GSON.fromJson(msg.getPayload(), JsonObject.class);
            this.banner.getHandler().obtainMessage(Common.RIPPLE_MSG_RECORD, recordJson).sendToTarget();
            this.patLeft.getHandler().obtainMessage(Common.RIPPLE_MSG_RECORD, recordJson).sendToTarget();
        } else if (topic.matches(Common.MQTT_TOPIC_MATCH_ECG_STREAM)) {
            this.patLeft.getHandler().obtainMessage(Common.RIPPLE_MSG_ECG_STREAM, msg).sendToTarget();
        } else {
            Log.d(Common.LOG_TAG, "Unknown MQTT topic recieved:" + topic);
        }
    }

    /**
     * Class to handle messages from MQTT client
     */
    private static class MQTTHandler extends Handler {
        private WeakReference<MainActivity> activityReference;

        public MQTTHandler(MainActivity activity) {
            this.activityReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = activityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MQTTServiceConstants.MSG_CONNECTED:
                        activity.subscribeToTopic(Common.MQTT_TOPIC_VITALPROP);
                        activity.subscribeToTopic(Common.MQTT_TOPIC_VITALCAST.replace(Common.MQTT_TOPIC_ID_STRING, Common.MQTT_TOPIC_WILDCARD_SINGLE_LEVEL));
                        Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                        // Clear old patient list
                        activity.banner.clearPatientBanner();
                        activity.patLeft.setPatientSrc("");
                        break;
                    case MQTTServiceConstants.MSG_CANT_CONNECT:
                        Toast.makeText(activity, "Unable to connect", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_PUBLISHED_MESSAGE:
                        activity.processPublishedMessage((PublishedMessage) msg.obj);
                        break;
                    default:
                        Log.d(Common.LOG_TAG, "Unknown message type :" + msg.what);
                        break;
                }
            }
        }
    }

}
