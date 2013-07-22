package mil.afrl.discoverylab.sate13.rippleandroid.data.operation;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.data.factory.PatientListJsonFactory;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Patient;
import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent;
import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleProvider;

/**
 * Created by burt on 7/3/13.
 */
public final class PatientListOperation implements Operation {

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {

        String url = WSConfig.WS_PATIENT_LIST_URL_JSON;
        NetworkConnection networkConnection = new NetworkConnection(context, url);
        NetworkConnection.ConnectionResult result = networkConnection.execute();

        ArrayList<Patient> PatientList;
        PatientList = PatientListJsonFactory.parseResult(result.body);

        // Clear the table
        context.getContentResolver().delete(RippleContent.db_patient.CONTENT_URI, null, null);

        // Adds the Patients in the database
        int PatientListSize = PatientList.size();
        if (PatientListSize > 0) {
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

            for (int i = 0; i < PatientListSize; i++) {
                operationList.add(ContentProviderOperation.newInsert(RippleContent.db_patient.CONTENT_URI)
                        .withValues(PatientList.get(i).toContentValues()).build());
            }

            try {
                context.getContentResolver().applyBatch(RippleProvider.AUTHORITY, operationList);
            } catch (RemoteException e) {
                throw new DataException(e);
            } catch (OperationApplicationException e) {
                throw new DataException(e);
            }
        }

        return null;
    }

}
