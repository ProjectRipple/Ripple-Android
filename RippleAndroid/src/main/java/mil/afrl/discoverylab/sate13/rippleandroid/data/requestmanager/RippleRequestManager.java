package mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager;

import android.content.Context;

import com.foxykeep.datadroid.requestmanager.RequestManager;

import mil.afrl.discoverylab.sate13.rippleandroid.data.service.RippleRequestService;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use methods to call the
 * service and manages the Intent creation. It also assures that a request will not be sent again if
 * an exactly identical one is already in progress.
 *
 * @author Foxykeep
 */
public final class RippleRequestManager extends RequestManager {

    // Singleton management
    private static RippleRequestManager sInstance;

    public synchronized static RippleRequestManager from(Context context) {
        if (sInstance == null) {
            sInstance = new RippleRequestManager(context);
        }

        return sInstance;
    }

    private RippleRequestManager(Context context) {
        super(context, RippleRequestService.class);
    }
    
    
}
