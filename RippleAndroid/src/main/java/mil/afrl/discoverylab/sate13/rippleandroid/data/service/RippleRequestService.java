package mil.afrl.discoverylab.sate13.rippleandroid.data.service;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;

import mil.afrl.discoverylab.sate13.rippleandroid.data.exception.MyCustomRequestException;
import mil.afrl.discoverylab.sate13.rippleandroid.data.operation.PatientListOperation;
import mil.afrl.discoverylab.sate13.rippleandroid.data.operation.SubscriptionOperation;
import mil.afrl.discoverylab.sate13.rippleandroid.data.operation.VitalsListOperation;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;

/**
 * This class is called by the RippleRequestManager through the {@link android.content.Intent} system.
 *
 * @author Foxykeep
 */
public final class RippleRequestService extends RequestService {

    @Override
    protected int getMaximumNumberOfThreads() {
        return 3;
    }

    @Override
    public Operation getOperationForType(int requestType) {
        switch (requestType) {
            case RippleRequestFactory.REQUEST_TYPE_PATIENT_LIST:
                return new PatientListOperation();
            case RippleRequestFactory.REQUEST_TYPE_VITAL_LIST:
                return new VitalsListOperation();
            case RippleRequestFactory.REQUEST_TYPE_SUBSCRIPTION:
                return new SubscriptionOperation();
        }
        return null;
    }

    @Override
    protected Bundle onCustomRequestException(Request request, CustomRequestException exception) {
        if (exception instanceof MyCustomRequestException) {
            Bundle bundle = new Bundle();
            bundle.putString(RippleRequestFactory.BUNDLE_EXTRA_ERROR_MESSAGE,
                    "MyCustomRequestException thrown.");
            return bundle;
        }
        return super.onCustomRequestException(request, exception);
    }


}
