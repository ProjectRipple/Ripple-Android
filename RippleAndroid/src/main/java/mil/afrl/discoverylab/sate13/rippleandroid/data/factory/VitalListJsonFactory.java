package mil.afrl.discoverylab.sate13.rippleandroid.data.factory;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.DataException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.config.JSONTag;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vital;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;

public final class VitalListJsonFactory {
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
}
