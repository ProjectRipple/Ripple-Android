package mil.afrl.discoverylab.sate13.rippleandroid.data.operation;

import android.content.Context;
import android.os.Bundle;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;

import java.util.HashMap;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.data.factory.SubscriptionResultFactory;

public class SubscriptionOperation implements RequestService.Operation {

    public static final String PARAM_PID = Common.PACKAGE_NAMESPACE + ".extra.pid";
    public static final String PARAM_ACTION = Common.PACKAGE_NAMESPACE + ".extra.action";
    public static final String PARAM_PORT = Common.PACKAGE_NAMESPACE + ".extra.port";

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {

        HashMap<String, String> params = new HashMap<String, String>(8);

        params.put(WSConfig.WS_PROPERTY_QUERYTYPE, "Subscription");
        params.put(WSConfig.WS_SUBSCRIPTION_PROPERTY_PID, request.getIntAsString(PARAM_PID));
        params.put(WSConfig.WS_SUBSCRIPTION_PROPERTY_ACTION, request.getString(PARAM_ACTION));
        params.put(WSConfig.WS_SUBSCRIPTION_PROPERTY_PORT, request.getIntAsString(PARAM_PORT));

        NetworkConnection networkConnection = new NetworkConnection(context, WSConfig.WS_QUERY_URL);

        networkConnection.setParameters(params);

        NetworkConnection.ConnectionResult res = networkConnection.execute();

        Bundle b = SubscriptionResultFactory.parseResult(res.body);

        return b;
    }

}
