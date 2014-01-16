package mil.afrl.discoverylab.sate13.rippleandroid;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import mil.afrl.discoverylab.sate13.rippleandroid.adapter.DatabaseAdapter;
import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.fragment.PrefsFragment;
import mil.afrl.discoverylab.sate13.rippleandroid.network.Controller;

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

        new Thread(new Controller()).start();
        SharedPreferences.Editor myEditor = prefs.edit();

        // Load ip from preferences
        String proxyIP = prefs.getString(PrefsFragment.IP_FROM_PREFS, WSConfig.DEFAULT_IP);

        boolean validIPv6 = proxyIP.matches(PrefsFragment.IPV6_HEXCOMPRESSED_REGEX) || proxyIP.matches(PrefsFragment.IPV6_REGEX);
        if (validIPv6) {
            myEditor.putString(PrefsFragment.IP_FROM_PREFS, proxyIP);
            myEditor.commit();

            //TODO: ADD JeroMQ handshake
        } else {
            Log.d(Common.LOG_TAG, this.getClass().getName() + " -- Invalid ip loaded from preferences:" + proxyIP);
        }


        DatabaseAdapter.getInstance(this.getApplicationContext());
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


