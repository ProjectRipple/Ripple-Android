package mil.afrl.discoverylab.sate13.rippleandroid.api;

import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.model.EcgRequestData;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by James on 6/1/2014.
 */
public class ApiClient {

    private static RippleApiInterface interfaceInstance;

    public static RippleApiInterface getRippleApiClient(){
        if(interfaceInstance == null){
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(WSConfig.ROOT_URL).build();

            interfaceInstance = restAdapter.create(RippleApiInterface.class);
        }

        return interfaceInstance;
    }

    public static void updateEndPoint(){
        // reset interface so new requests use new endpoint
        interfaceInstance = null;
    }

    public interface RippleApiInterface{
        @POST("/ecgrequest")
        void requestEcgStream(@Body String id, Callback<EcgRequestData> callback);
    }

}
