package mil.afrl.discoverylab.sate13.rippleandroid;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import mil.afrl.discoverylab.sate13.rippleandroid.Parse.CSVParser;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.DatabaseAdapter;

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
        DatabaseAdapter.getInstance(this.getApplicationContext());
        if (DatabaseAdapter.getInstance().isTableEmpty(DatabaseAdapter.TableType.VITAL.name())) {
            Log.d(Common.LOG_TAG, "Vitals table is empty, parsing CSV file and inserting data.");
            CSVParser.initializeCSVParser(this.getApplicationContext());
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


