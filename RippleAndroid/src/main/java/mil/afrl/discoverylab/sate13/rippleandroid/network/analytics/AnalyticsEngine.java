package mil.afrl.discoverylab.sate13.rippleandroid.network.analytics;

import java.net.InetSocketAddress;

import mil.afrl.discoverylab.sate13.rippleandroid.network.Reference;
import mil.afrl.discoverylab.sate13.rippleandroid.network.RippleMoteMessage;

/**
 * This class handles the analytics for a given set of vitals
 * Created by harmonbc on 1/6/14.
 */
public class AnalyticsEngine {

    public AnalyticsResponse analyze(InetSocketAddress sender, byte[] message) {
        //TODO: Need medical specialist to determine what they would like to have checked, create config file for customization
        AnalyticsResponse response = new AnalyticsResponse();
        response.response_code = Reference.ANALYTICS_RESPONSE.MEDIUM_PRIORITY;
        response.details = "All Works";

        return response;
    }
}