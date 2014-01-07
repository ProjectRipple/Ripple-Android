package mil.afrl.discoverylab.sate13.rippleandroid.network;

/**
 * This class handles the analytics for a given set of vitals
 * Created by harmonbc on 1/6/14.
 */
public class AnalyticsEngine {

    public Reference.ANALYTICS_RESPONSE analyze(RippleMoteMessage message){
        //TODO: Need medical specialist to determine what they would like to have checked, create config file for customization
        return Reference.ANALYTICS_RESPONSE.MEDIUM_PRIORITY;
    }
}
