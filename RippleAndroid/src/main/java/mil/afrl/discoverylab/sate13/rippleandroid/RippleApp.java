package mil.afrl.discoverylab.sate13.rippleandroid;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.PrefsFragment;

/**
 * RippleApplication object initializes the DatabaseAdapter singleton and stores global variables
 * <p/>
 * <p/>
 * Created by matt on 6/20/13.
 */
public class RippleApp extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Load preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor myEditor = prefs.edit();

        // Load ip from preferences
        String brokerIP = prefs.getString(PrefsFragment.IP_FROM_PREFS, WSConfig.DEFAULT_IP);

        boolean validIPv4 = brokerIP.matches(PrefsFragment.IP_REG_EXPRESSION);
        boolean validIPv6 = brokerIP.matches(PrefsFragment.IPV6_HEXCOMPRESSED_REGEX) || brokerIP.matches(PrefsFragment.IPV6_REGEX);

        // load ports
        String portNumMqtt = prefs.getString(PrefsFragment.PORT_NUM_MQTT_PREFS, WSConfig.DEFAULT_MQTT_PORT);
        myEditor.putString(PrefsFragment.PORT_NUM_MQTT_PREFS, portNumMqtt);
        myEditor.commit();

        String portNumRest = prefs.getString(PrefsFragment.PORT_NUM_REST_PREFS, WSConfig.DEFAULT_REST_PORT);
        myEditor.putString(PrefsFragment.PORT_NUM_REST_PREFS, portNumRest);
        myEditor.commit();

        if (validIPv4) {
            myEditor.putString(PrefsFragment.IP_FROM_PREFS, brokerIP);
            myEditor.commit();
            WSConfig.ROOT_URL = "http://" + brokerIP + ":" + portNumRest;// + "/" + WSConfig.BROKER_ROOT + "/";
            WSConfig.WS_QUERY_URL = WSConfig.ROOT_URL + "Query";
        } else if (validIPv6) {
            myEditor.putString(PrefsFragment.IP_FROM_PREFS, brokerIP);
            myEditor.commit();
            WSConfig.ROOT_URL = "http://[" + brokerIP + "]:" + portNumRest;// + "/" + WSConfig.BROKER_ROOT + "/";
            WSConfig.WS_QUERY_URL = WSConfig.ROOT_URL + "Query";
        } else {
            Log.d(Common.LOG_TAG, this.getClass().getName() + " -- Invalid ip loaded from preferences:" + brokerIP);
        }



        /*DatabaseAdapter.getInstance(this.getApplicationContext());
        if (DatabaseAdapter.getInstance().isTableEmpty(DatabaseAdapter.TableType.VITAL.name())) {
            Log.d(Common.LOG_TAG, "Vitals table is empty, parsing CSV file and inserting data.");
            CSVParser.initializeCSVParser(this.getApplicationContext());
        }*/
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


