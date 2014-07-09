package com.discoverylab.ripple.android.api;

import com.discoverylab.ripple.android.config.WSConfig;
import com.discoverylab.ripple.android.model.EcgRequestData;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Client for accessing Broker's REST API
 * Created by James on 6/1/2014.
 */
public class ApiClient {

    private static RippleApiInterface interfaceInstance;

    public static RippleApiInterface getRippleApiClient() {
        if (interfaceInstance == null) {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(WSConfig.ROOT_URL).build();

            interfaceInstance = restAdapter.create(RippleApiInterface.class);
        }

        return interfaceInstance;
    }

    public static void updateEndPoint() {
        // reset interface so new requests use new endpoint
        interfaceInstance = null;
    }

    public interface RippleApiInterface {
        @FormUrlEncoded
        @POST("/ecgrequest")
        void requestEcgStream(@Field("id") String id, Callback<EcgRequestData> callback);
    }

}
