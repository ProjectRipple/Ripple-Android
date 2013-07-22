package mil.afrl.discoverylab.sate13.rippleandroid.data.factory;

import android.os.Bundle;
import android.util.Log;

import com.foxykeep.datadroid.exception.DataException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.config.JSONTag;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vital;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;

/**
 * Created by burt on 7/3/13.
 */
public final class VitalListJsonFactory {
    private static final String TAG = VitalListJsonFactory.class.getSimpleName();

    private VitalListJsonFactory() {
        // No public constructor
    }

    public static Bundle parseResult(String wsResponse) throws DataException {
        ArrayList<Vital> vitalList = new ArrayList<Vital>();

        try {
            JSONObject parser = new JSONObject(wsResponse);
            JSONObject jsonRoot = parser.getJSONObject(JSONTag.VITALS);
            JSONArray jsonVITALSArray = jsonRoot.getJSONArray(JSONTag.VITAL);
            int size = jsonVITALSArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonVITALS = jsonVITALSArray.getJSONObject(i);
                Vital vital = new Vital();

                vital.vid = jsonVITALS.getInt(JSONTag.VITALS_VID);
                vital.pid = jsonVITALS.getInt(JSONTag.VITALS_PID);
                vital.server_timestamp = jsonVITALS.getString(JSONTag.VITALS_SERVER_TIMESTAMP);
                vital.sensor_timestamp = jsonVITALS.getInt(JSONTag.VITALS_SENSOR_TIMESTAMP);
                vital.sensor_type = jsonVITALS.getInt(JSONTag.VITALS_SENSOR_TYPE);
                vital.value_type = jsonVITALS.getInt(JSONTag.VITALS_VALUE_TYPE);
                vital.value = jsonVITALS.getInt(JSONTag.VITALS_VALUE);

                vitalList.add(vital);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            throw new DataException(e);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RippleRequestFactory.BUNDLE_EXTRA_VITAL_LIST, vitalList);
        return bundle;
    }

}
