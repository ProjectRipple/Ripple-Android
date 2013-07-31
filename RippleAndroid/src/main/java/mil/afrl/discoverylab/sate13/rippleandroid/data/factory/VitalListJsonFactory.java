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

public final class VitalListJsonFactory {
    private static final String TAG = VitalListJsonFactory.class.getSimpleName();

    private VitalListJsonFactory() {
        // No public constructor
    }

    public static Bundle parseResult(String wsResponse) throws DataException {
        ArrayList<Vital> vitalList = new ArrayList<Vital>();

        try {
            JSONObject parser = new JSONObject(wsResponse);
            JSONArray jsonVITALSArray = parser.getJSONArray(JSONTag.VITALS);
            int size = jsonVITALSArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonVITALS = jsonVITALSArray.getJSONObject(i);

                vitalList.add(new Vital(
                        jsonVITALS.getInt(JSONTag.VITALS_PID),
                        jsonVITALS.getString(JSONTag.VITALS_SERVER_TIMESTAMP),
                        jsonVITALS.getInt(JSONTag.VITALS_SENSOR_TIMESTAMP),
                        jsonVITALS.getInt(JSONTag.VITALS_SENSOR_TYPE),
                        jsonVITALS.getInt(JSONTag.VITALS_VALUE_TYPE),
                        jsonVITALS.getInt(JSONTag.VITALS_VALUE)));
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException", e);
            //throw new DataException(e);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RippleRequestFactory.BUNDLE_EXTRA_VITAL_LIST, vitalList);
        return bundle;
    }
}

/*public final class VitalListJsonFactory {
    private static Gson gson = new GsonBuilder().setDateFormat(Common.DATE_TIME_FORMAT).create();

    private VitalListJsonFactory() {
        // No public constructor
    }

    public static Bundle parseResult(String wsResponse) throws DataException {

        JsonObject json = gson.fromJson(wsResponse, JsonObject.class);
        JsonArray vList = json.getAsJsonArray(JSONTag.VITALS);

        ArrayList<Vital> vitalList = new ArrayList<Vital>(vList.size());

        for (JsonElement j : vList) {
            vitalList.add(gson.fromJson(j, Vital.class));
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RippleRequestFactory.BUNDLE_EXTRA_VITAL_LIST, vitalList);
        return bundle;
    }
}*/
