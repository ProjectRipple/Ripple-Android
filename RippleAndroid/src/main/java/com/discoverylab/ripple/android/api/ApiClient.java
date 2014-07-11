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

    // Reference to Singleton instance
    private static RippleApiInterface interfaceInstance;

    /**
     * Retrieve the interface for Broker's REST API
     * @return The interface for the Broker's REST API
     */
    public static RippleApiInterface getRippleApiClient() {
        if (interfaceInstance == null) {
            // Create a new adapter with current URL
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(WSConfig.ROOT_URL).build();

            interfaceInstance = restAdapter.create(RippleApiInterface.class);
        }

        return interfaceInstance;
    }

    /**
     * Tell the client to update its endpoint. Will only take effect on next call to {@link #getRippleApiClient}
     */
    public static void updateEndPoint() {
        // reset interface so new requests use new endpoint
        interfaceInstance = null;
    }

    /**
     * Interface of the Broker's REST API
     */
    public interface RippleApiInterface {
        /**
         * Request an ECG stream from a particular mote
         * @param id Device ID of the mote we are requesting ECG from.
         * @param callback Callback for when the request finishes.
         */
        @FormUrlEncoded
        @POST("/ecgrequest")
        void requestEcgStream(@Field("id") String id, Callback<EcgRequestData> callback);
    }

}
