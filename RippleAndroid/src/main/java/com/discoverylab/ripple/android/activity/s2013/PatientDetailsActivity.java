package com.discoverylab.ripple.android.activity.s2013;


import android.app.Activity;
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

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.WSConfig;
import com.discoverylab.ripple.android.fragment.s2013.Banner;
import com.discoverylab.ripple.android.fragment.s2013.PatientDetailsFragment;
import com.discoverylab.ripple.android.fragment.PrefsFragment;
import com.discoverylab.ripple.android.mqtt.MQTTClientService;
import com.discoverylab.ripple.android.mqtt.MQTTServiceConstants;
import com.discoverylab.ripple.android.mqtt.MQTTServiceManager;
import com.discoverylab.ripple.android.mqtt.PublishedMessage;
import com.discoverylab.ripple.android.util.RandomPatient;
import com.discoverylab.ripple.android.view.PatientView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;

/**
 * Main activity of application
 */
public class PatientDetailsActivity extends Activity implements LocationSource.OnLocationChangedListener, View.OnClickListener {

    /*Inter-Fragment MGMT*/
    private Banner banner;
    private PatientDetailsFragment patLeft;

    /*Mapping Vars*/
    private GoogleMap map;
    private LocationManager lm;
    // Center of contiguous USA land
    public static final LatLng CONTIGUOUS_USA_CENTER = new LatLng(39.830000, -98.580000);

    // MQTT
    private MQTTServiceManager mqttServiceManager;

    // Set to true to fill banner with fake patients on start
    private boolean createFakePatients = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // no title, fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set layout
        setContentView(R.layout.activity_main);

        // set banner fragment dynamically
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

        // set banner handler for patient details
        patLeft = (PatientDetailsFragment) fragmentManager.findFragmentById(R.id.bottomleft);
        patLeft.setBannerHandler(banner.getHandler());

        // setup map
        initMap();

        // get MQTT service manager
        mqttServiceManager = new MQTTServiceManager(this, MQTTClientService.class, new MQTTHandler(this));
    }

    /**
     * Initialize map fragment
     */
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
        // setup location manager settings
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, true);
        Location location = lm.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        } else if (map != null) {
            // zoom to USA
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(CONTIGUOUS_USA_CENTER, (float) 4.0));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unbind service
        if (this.mqttServiceManager != null && this.mqttServiceManager.isServiceRunning()) {
            this.mqttServiceManager.unbind();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // bind service
        if (this.mqttServiceManager != null && this.mqttServiceManager.isServiceRunning()) {
            this.mqttServiceManager.bind();
        }
        // check if we should create fake patients (for debugging/demo)
        if (createFakePatients) {
            for (int i = 0; i < RandomPatient.MAX_UNIQUE_PATIENTS; i++) {
                this.banner.addPatient(RandomPatient.getRandomPatient());
            }
            createFakePatients = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //We don't need menus right now
        return false;
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
            Log.d(Common.LOG_TAG, "Patient src selected: " + pView.getPatientSrc());
            this.patLeft.setPatientSrc(pView.getPatientSrc());
        }
    }

    /**
     * Start MQTT service with current connection preferences
     */
    public void startMQTTService() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.mqttServiceManager.start(prefs.getString(PrefsFragment.IP_FROM_PREFS, WSConfig.DEFAULT_BROKER_IP), prefs.getString(PrefsFragment.PORT_NUM_MQTT_PREFS, WSConfig.DEFAULT_MQTT_PORT));
    }

    /**
     * Stop MQTT service (disconnect from Broker)
     */
    public void stopMQTTService() {
        if (this.mqttServiceManager != null && this.mqttServiceManager.isServiceRunning()) {
            this.mqttServiceManager.stop();
        }
    }

    /**
     * @return true if MQTT service is currently running, false otherwise
     */
    public boolean isMQTTServiceRunning() {
        return this.mqttServiceManager.isServiceRunning();
    }

    /**
     * Subscribe to a MQTT topic
     *
     * @param topicName MQTT topic to subscribe to
     */
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

    /**
     * Unsubscribe from an MQTT topic
     *
     * @param topicName MQTT topic to unsubscribe from
     */
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

    /**
     * Processes message from MQTT client
     *
     * @param msg MQTT message to process
     */
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
        private WeakReference<PatientDetailsActivity> activityReference;

        public MQTTHandler(PatientDetailsActivity activity) {
            this.activityReference = new WeakReference<PatientDetailsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PatientDetailsActivity activity = activityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MQTTServiceConstants.MSG_CONNECTED:
                        activity.subscribeToTopic(Common.MQTT_TOPIC_VITALPROP);
                        activity.subscribeToTopic(Common.MQTT_TOPIC_VITALCAST.replace(Common.MQTT_TOPIC_PATIENT_ID_STRING, Common.MQTT_TOPIC_WILDCARD_SINGLE_LEVEL));
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
                    case MQTTServiceConstants.MSG_DISCONNECTED:
                        Toast.makeText(activity, "Disconnected from Broker.", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_NO_NETWORK:
                        Toast.makeText(activity, "No network connection, will attempt to reconnect with broker when network is restored.", Toast.LENGTH_LONG).show();
                        break;
                    case MQTTServiceConstants.MSG_RECONNECTING:
                        Toast.makeText(activity, "Reconnecting to Broker...", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_CONNECTION_STATUS:

                        break;
                    default:
                        Log.d(Common.LOG_TAG, "Unknown message type :" + msg.what);
                        break;
                }
            }
        }
    }

}
