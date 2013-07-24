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
import java.util.HashMap;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.data.factory.VitalListJsonFactory;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vital;
import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleContent;
import mil.afrl.discoverylab.sate13.rippleandroid.data.provider.RippleProvider;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;

public final class VitalsListOperation implements Operation {

    public static final String PARAM_PID = Common.PACKAGE_NAMESPACE + ".extra.pid";
    public static final String PARAM_VIDI = Common.PACKAGE_NAMESPACE + ".extra.vidi";
    public static final String PARAM_LIMIT = Common.PACKAGE_NAMESPACE + ".extra.limit";

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {

        HashMap<String, String> params = new HashMap<String, String>(8);
        params.put(WSConfig.WS_PROPERTY_QUERYTYPE, "Vital");
        params.put(WSConfig.WS_VITAL_PROPERTY_PID, request.getIntAsString(PARAM_PID));
        params.put(WSConfig.WS_VITAL_PROPERTY_VIDI, request.getIntAsString(PARAM_VIDI));
        params.put(WSConfig.WS_VITAL_PROPERTY_LIMIT, request.getIntAsString(PARAM_LIMIT));

        String url = WSConfig.WS_QUERY_URL;
        NetworkConnection networkConnection = new NetworkConnection(context, url);
        networkConnection.setParameters(params);

        NetworkConnection.ConnectionResult result = networkConnection.execute();

        Bundle bundle = VitalListJsonFactory.parseResult(result.body);
        ArrayList<Vital> vitalList = bundle.getParcelableArrayList(RippleRequestFactory.BUNDLE_EXTRA_VITAL_LIST);

        // Adds the Vital in the database
        if (!vitalList.isEmpty()) {
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            for (Vital v : vitalList) {
                operationList.add(ContentProviderOperation.newInsert(RippleContent.db_vital.CONTENT_URI)
                        .withValues(v.toContentValues()).build());
                try {
                    context.getContentResolver().applyBatch(RippleProvider.AUTHORITY, operationList);
                } catch (RemoteException e) {
                    throw new DataException(e);
                } catch (OperationApplicationException e) {
                    throw new DataException(e);
                }
            }
        }
        return bundle;
    }
}
