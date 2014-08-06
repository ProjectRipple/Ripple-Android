package com.discoverylab.ripple.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.WSConfig;
import com.discoverylab.ripple.android.fragment.PrefsFragment;
import com.discoverylab.ripple.android.object.Patient;
import com.discoverylab.ripple.android.object.PatientNote;
import com.discoverylab.ripple.android.object.PatientNotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        // Setup triage colors from values
        Resources resources = getResources();
        Common.TRIAGE_COLORS.UNKNOWN.setColor(resources.getColor(R.color.triage_unknown));
        Common.TRIAGE_COLORS.GREEN.setColor(resources.getColor(R.color.triage_green));
        Common.TRIAGE_COLORS.YELLOW.setColor(resources.getColor(R.color.triage_yellow));
        Common.TRIAGE_COLORS.RED.setColor(resources.getColor(R.color.triage_red));
        Common.TRIAGE_COLORS.BLACK.setColor(resources.getColor(R.color.triage_black));

        // Load old notes into app
        // TODO: is this something we always want? How to clear old patients?
        loadCachedNotes();

        // Load debug settings
        loadDebugSettings();

        // test that patient is still parcelable
        Patient p = new Patient("hello");
        p.setTriageState(Common.TRIAGE_COLORS.RED);

        Bundle b = new Bundle();
        b.putParcelable("temptag", p);

        Parcel parcel = Parcel.obtain();
        b.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        Bundle b2 = parcel.readBundle();
        b2.setClassLoader(Patient.class.getClassLoader());
        Patient p2 = b2.getParcelable("temptag");

        Log.d(TAG, "P2 triage is " + p2.getTriageState().toString());
        // end test

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

    private void loadCachedNotes() {
        File noteDir = getDir(Common.NOTES_DIR, Context.MODE_PRIVATE);

        if (!noteDir.exists()) {
            // create notes directory and return as there are no notes
            if (!noteDir.mkdirs()) {
                Log.e(TAG, "Failed to create notes directory");
            }
            return;
        }

        PatientNotes notes = PatientNotes.getInstance();
        File[] noteDirFiles = noteDir.listFiles();
        File[] jsonFiles;
        FilenameFilter jsonFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.contains(".json");
            }
        };
        FileInputStream fin = null;

        for (File f : noteDirFiles) {
            if (f.isDirectory()) {
                // dig deeper for notes
                jsonFiles = f.listFiles(jsonFilter);

                for (File noteFile : jsonFiles) {
                    try {
                        fin = new FileInputStream(noteFile);
                        String json = convertStreamToString(fin);

                        PatientNote note = PatientNote.fromJson(json);
                        if (note != null) {
                            notes.addNote(note);
                            Log.d(TAG, "Loaded note from cached file " + noteFile.getName());
                        } else {
                            Log.e(TAG, "Failed to parse file json for file " + noteFile.getName());
                        }

                    } catch (IOException ie) {
                        Log.e(TAG, "Failed to read file " + noteFile.getName());
                    } finally {
                        if(fin != null){
                            try {
                                fin.close();
                            } catch (IOException e) {
                                Log.d(TAG, "Failed to close file input stream.");
                            }
                            fin = null;
                        }
                    }
                }
            }
        }
    }

    private void loadDebugSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor myEditor = prefs.edit();

        // Pref to send image as base 64 string in MQTT message
        Common.SEND_IMAGE_BASE64 = prefs.getBoolean(PrefsFragment.SEND_IMAGE_BASE64_PREF, false);
        myEditor.putBoolean(PrefsFragment.SEND_IMAGE_BASE64_PREF, Common.SEND_IMAGE_BASE64);
        myEditor.apply();

    }

    private String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
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


