package mil.afrl.discoverylab.sate13.rippleandroid.data.factory;

import android.os.Bundle;
import android.util.Log;

import com.foxykeep.datadroid.exception.DataException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.config.JSONTag;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vitals;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;

/**
 * Created by burt on 7/3/13.
 */
public final class VitalsListJsonFactory {
    private static final String TAG = VitalsListJsonFactory.class.getSimpleName();

    private VitalsListJsonFactory() {
        // No public constructor
    }

    public static Bundle parseResult(String wsResponse) throws DataException {
        ArrayList<Vitals> VitalsList = new ArrayList<Vitals>();

        try {
            JSONObject parser = new JSONObject(wsResponse);
            JSONObject jsonRoot = parser.getJSONObject(JSONTag.VITALS);
            JSONArray jsonVITALSArray = jsonRoot.getJSONArray(JSONTag.VITAL);
            int size = jsonVITALSArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonVITALS = jsonVITALSArray.getJSONObject(i);
                Vitals vitals = new Vitals();

                vitals.vid = jsonVITALS.getInt(JSONTag.VITALS_VID);
                vitals.ip_addr = jsonVITALS.getInt(JSONTag.VITALS_IP_ADDR);
                vitals.timestamp = jsonVITALS.getInt(JSONTag.VITALS_TIMESTAMP);
                vitals.sensor_type = jsonVITALS.getInt(JSONTag.VITALS_SENSOR_TYPE);
                vitals.value_type = jsonVITALS.getInt(JSONTag.VITALS_VALUE_TYPE);
                vitals.value = jsonVITALS.getInt(JSONTag.VITALS_VALUE);

                VitalsList.add(vitals);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            throw new DataException(e);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RippleRequestFactory.BUNDLE_EXTRA_VITALS_LIST, VitalsList);
        return bundle;
    }

}
