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
import mil.afrl.discoverylab.sate13.rippleandroid.data.factory.VitalsListJsonFactory;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vitals;
import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent;
import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleProvider;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;

/**
 * Created by burt on 7/3/13.
 */
public final class VitalsListOperation implements Operation {

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {

        String url = WSConfig.WS_VITALS_LIST_URL_JSON;
        NetworkConnection networkConnection = new NetworkConnection(context, url);
        NetworkConnection.ConnectionResult result = networkConnection.execute();

        ArrayList<Vitals> VitalsList;
        Bundle bundle = VitalsListJsonFactory.parseResult(result.body);
        VitalsList = bundle.getParcelableArrayList(RippleRequestFactory.BUNDLE_EXTRA_VITALS_LIST);

        // Clear the table
        context.getContentResolver().delete(RippleContent.DbVitals.CONTENT_URI, null, null);

        // Adds the Vitals in the database
        int VitalsListSize = VitalsList.size();
        if (VitalsListSize > 0) {
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

            for (int i = 0; i < VitalsListSize; i++) {
                operationList.add(ContentProviderOperation.newInsert(RippleContent.DbVitals.CONTENT_URI)
                        .withValues(VitalsList.get(i).toContentValues()).build());
            }

            try {
                context.getContentResolver().applyBatch(RippleProvider.AUTHORITY, operationList);
            } catch (RemoteException e) {
                throw new DataException(e);
            } catch (OperationApplicationException e) {
                throw new DataException(e);
            }
        }

        return bundle;
    }

}
