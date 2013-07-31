package mil.afrl.discoverylab.sate13.rippleandroid.data.factory;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.DataException;
import com.google.gson.JsonObject;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.SubscriptionResponse;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;

public final class SubscriptionResultFactory {

    private SubscriptionResultFactory() {
        // No public constructor
    }

    public static Bundle parseResult(String wsResponse) throws DataException {

        JsonObject json = Common.GSON.fromJson(wsResponse, JsonObject.class);
        SubscriptionResponse response = Common.GSON.fromJson(json, SubscriptionResponse.class);

        /*boolean success = json.get(JSONTag.SUCCESS).getAsBoolean();
        String exception = json.get(JSONTag.EXCEPTION).getAsString();
        int pid_echo = json.get(JSONTag.PID_ECHO).getAsInt();
        String action_echo = json.get(JSONTag.ACTION_ECHO).getAsString();
        int port_echo = json.get(JSONTag.PORT_ECHO).getAsInt();*/

        Bundle bundle = new Bundle();
        bundle.putParcelable(RippleRequestFactory.BUNDLE_EXTRA_SUBSCRIPTION, response);

        return bundle;
    }
}
