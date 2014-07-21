package com.discoverylab.ripple.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.WSConfig;
import com.discoverylab.ripple.android.fragment.PrefsFragment;

/**
 * RippleApplication object initializes the connection preferences and stores global variables
 * <p/>
 * <p/>
 * Created by matt on 6/20/13.
 */
public class RippleApp extends Application {

    // Log tag
    private static final String TAG = RippleApp.class.getSimpleName();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Load preferences
        loadConnectionPreferences();

        // lookup device id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String deviceId = prefs.getString(Common.RESPONDER_ID_PREF, "");

        if (deviceId.equals("")) {
            // get device ID
            deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (deviceId == null) {

                Log.d(TAG, "Device ID not found, generating random ID.");
                // generate ID if ANDROID_ID cannot be found
                deviceId = org.eclipse.paho.client.mqttv3.MqttClient.generateClientId();
            }
            // save id in preferences
            prefs.edit().putString(Common.RESPONDER_ID_PREF, deviceId).apply();
        }
        // set id to static global value
        Common.RESPONDER_ID = deviceId;

        // Database ignored for now
        /*DatabaseAdapter.getInstance(this.getApplicationContext());
        if (DatabaseAdapter.getInstance().isTableEmpty(DatabaseAdapter.TableType.VITAL.name())) {
            Log.d(Common.LOG_TAG, "Vitals table is empty, parsing CSV file and inserting data.");
            CSVParser.initializeCSVParser(this.getApplicationContext());
        }*/
    }

    /**
     * Load and verify connection preferences, loading defaults if needed.
     */
    private void loadConnectionPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor myEditor = prefs.edit();

        // Load ip from preferences
        String brokerIP = prefs.getString(PrefsFragment.IP_FROM_PREFS, WSConfig.DEFAULT_BROKER_IP);

        boolean validIPv4 = brokerIP.matches(PrefsFragment.IP_REG_EXPRESSION);
        boolean validIPv6 = brokerIP.matches(PrefsFragment.IPV6_HEXCOMPRESSED_REGEX) || brokerIP.matches(PrefsFragment.IPV6_REGEX);

        // load ports
        String portNumMqtt = prefs.getString(PrefsFragment.PORT_NUM_MQTT_PREFS, WSConfig.DEFAULT_MQTT_PORT);
        myEditor.putString(PrefsFragment.PORT_NUM_MQTT_PREFS, portNumMqtt);
        myEditor.apply();

        String portNumRest = prefs.getString(PrefsFragment.PORT_NUM_REST_PREFS, WSConfig.DEFAULT_REST_PORT);
        myEditor.putString(PrefsFragment.PORT_NUM_REST_PREFS, portNumRest);
        myEditor.apply();

        if (validIPv4) {
            myEditor.putString(PrefsFragment.IP_FROM_PREFS, brokerIP);
            myEditor.apply();
            WSConfig.ROOT_URL = "http://" + brokerIP + ":" + portNumRest;
        } else if (validIPv6) {
            myEditor.putString(PrefsFragment.IP_FROM_PREFS, brokerIP);
            myEditor.apply();
            WSConfig.ROOT_URL = "http://[" + brokerIP + "]:" + portNumRest;
        } else {
            Log.d(Common.LOG_TAG, this.getClass().getName() + " -- Invalid ip loaded from preferences:" + brokerIP);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}


