package mil.afrl.discoverylab.sate13.rippleandroid.data.factory;

import android.util.Log;

import com.foxykeep.datadroid.exception.DataException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.config.JSONTag;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Patient;

/**
 * Created by burt on 7/3/13.
 */
public final class PatientListJsonFactory {
    private static final String TAG = PatientListJsonFactory.class.getSimpleName();

    private PatientListJsonFactory() {
        // No public constructor
    }

    public static ArrayList<Patient> parseResult(String wsResponse) throws DataException {
        ArrayList<Patient> PatientList = new ArrayList<Patient>();

        try {
            JSONObject parser = new JSONObject(wsResponse);
            JSONObject jsonRoot = parser.getJSONObject(JSONTag.PATIENTS);
            JSONArray jsonPatientArray = jsonRoot.getJSONArray(JSONTag.PATIENT);
            int size = jsonPatientArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonPatient = jsonPatientArray.getJSONObject(i);
                Patient Patient = new Patient();

                Patient.pid = jsonPatient.getInt(JSONTag.PATIENT_PID);
                Patient.first_name = jsonPatient.getString(JSONTag.PATIENT_FIRST_NAME);
                Patient.last_name = jsonPatient.getString(JSONTag.PATIENT_LAST_NAME);
                Patient.ssn = jsonPatient.getInt(JSONTag.PATIENT_SSN);
                Patient.birthday = jsonPatient.getString(JSONTag.PATIENT_BIRTHDAY);
                Patient.sex = jsonPatient.getBoolean(JSONTag.PATIENT_SEX);
                Patient.nbc_contamination = jsonPatient.getBoolean(JSONTag.PATIENT_NBC_CONTAMINATION);
                Patient.type = jsonPatient.getString(JSONTag.PATIENT_TYPE);

                PatientList.add(Patient);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            throw new DataException(e);
        }

        return PatientList;
    }

}
