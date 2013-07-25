package mil.afrl.discoverylab.sate13.rippleandroid.data.factory;

import com.foxykeep.datadroid.exception.DataException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.config.JSONTag;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Patient;

public final class PatientListJsonFactory {
    private static Gson gson = new GsonBuilder().setDateFormat(Common.DATE_TIME_FORMAT).create();

    private PatientListJsonFactory() {
        // No public constructor
    }

    public static ArrayList<Patient> parseResult(String wsResponse) throws DataException {

        JsonObject json = gson.fromJson(wsResponse, JsonObject.class);
        JsonArray pList = json.getAsJsonArray(JSONTag.PATIENTS);

        ArrayList<Patient> patientList = new ArrayList<Patient>(pList.size());

        for (JsonElement j : pList) {
            patientList.add(gson.fromJson(j, Patient.class));
        }
        return patientList;
    }
}
