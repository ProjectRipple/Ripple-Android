package mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager;

import com.foxykeep.datadroid.requestmanager.Request;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;

/**
 * Class used to create the {@link com.foxykeep.datadroid.requestmanager.Request}s.
 *
 * @author Foxykeep
 */
public final class RippleRequestFactory {

    // Request types
    public static final int REQUEST_TYPE_PATIENT_LIST = 0;
    public static final int REQUEST_TYPE_VITALS_LIST = 1;

    // Response data
    public static final String BUNDLE_EXTRA_VITALS_LIST = Common.PACKAGE_NAMESPACE + ".extra.vitalsList";
    public static final String BUNDLE_EXTRA_ERROR_MESSAGE = Common.PACKAGE_NAMESPACE + ".extra.errorMessage";

    private RippleRequestFactory() {
        // no public constructor
    }

    /**
     * Create the request to get the list of patients and save it in the database.
     *
     * @return The request.
     */
    public static Request getPatientListRequest() {
        return new Request(REQUEST_TYPE_PATIENT_LIST);
    }

    /**
     * Create the request to get the list of vitals and save it in the database.
     *
     * @return The request.
     */
    public static Request getVitalsListRequest() {
        return new Request(REQUEST_TYPE_VITALS_LIST);
    }

}
