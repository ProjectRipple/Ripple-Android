package mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.ui.GraphHelper;
import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vital;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestManager;

/**
 * The left patient fragment is used to display the most recent health data and information
 * for the patient
 * <p/>
 * The a chart is added to the bottom left linear layout and is used to display the ecg waveform
 */
public class PatientLeft extends Fragment implements View.OnClickListener, RequestManager.RequestListener {

    private static final Long POLL_DELAY = 250L; // in milliseconds
    private static final String SAVED_STATE_REQUEST_LIST = "savedStateRequestList";
    //private static Gson gson = new GsonBuilder().setDateFormat(Common.DATE_TIME_FORMAT).create();
    private int curPatient = 1;
    private int curVital;
    private Long prevTime;
    protected RippleRequestManager mRequestManager;
    protected ArrayList<Request> mRequestList;
    private View view;
    private TextView patientName;
    private GraphHelper graphHelper;

    private synchronized void callVitalsListWS(int pid, int vidi) {
        prevTime = System.currentTimeMillis();
        Request request = RippleRequestFactory.getVitalListRequest(
                pid,
                vidi,
                WSConfig.WS_VITAL_PARAM_DEFAULT_ROWLIMIT,
                WSConfig.WS_VITAL_PARAM_DEFAULT_TIMELIMIT);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    /**
     * TODO: Query the content provider for a list of available patients,
     * otherwise wait for a notification from the Banner
     * <p/>
     * TODO: on addition of a new patient query the content provider for existing vitals
     * and update the curVital
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.patient_left, container, false);
        assert view != null;

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.chart);
        graphHelper = new GraphHelper(this.getActivity(), layout);
        layout.addView(graphHelper.chartView,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

        ImageView tagview = (ImageView) view.findViewById(R.id.tagview);
        assert tagview != null;
        tagview.setClickable(true);
        tagview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.finger_paint_dialog);
                dialog.setTitle(" Draw Something Friend ");

                // set the custom dialog components - text, image and button
                Button dialogButton = (Button) dialog.findViewById(R.id.fingerbuttonok);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mRequestManager = RippleRequestManager.from(getActivity());
        if (savedInstanceState != null) {
            mRequestList = savedInstanceState.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);
        } else {
            mRequestList = new ArrayList<Request>();
        }

        callVitalsListWS(curPatient, 0);

        this.patientName = (TextView) view.findViewById(R.id.name_value_tv);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        graphHelper.save(outState);
        outState.putParcelableArrayList(SAVED_STATE_REQUEST_LIST, mRequestList);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        if (savedState != null) {
            graphHelper.restore(savedState);
            mRequestManager = RippleRequestManager.from(getActivity());
            if (savedState != null) {
                mRequestList = savedState.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);
            } else {
                mRequestList = new ArrayList<Request>();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < mRequestList.size(); i++) {
            Request request = mRequestList.get(i);
            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
            } else {
                mRequestManager.callListenerWithCachedData(this, request);
                i--;
                mRequestList.remove(request);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mRequestList.isEmpty()) {
            mRequestManager.removeRequestListener(this);
        }
    }

    @Override
    public void onRequestFinished(final Request request, final Bundle resultData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int pid;
                if (mRequestList.contains(request)) {
                    mRequestList.remove(request);
                    ArrayList<Vital> vitalList = resultData.getParcelableArrayList(RippleRequestFactory.BUNDLE_EXTRA_VITAL_LIST);
                    pid = vitalList.get(0).pid;
                    if (curPatient == pid) {
                        int cnt = 0;
                        for (Vital vital : vitalList) {
                            if (graphHelper.addPoint(
                                    (double) vital.sensor_timestamp,
                                    (double) vital.value / 10000000.0)) {
                                curVital = vital.vid;
                                cnt++;
                            }
                            Thread.currentThread().yield();
                        }
                        Log.i(Common.LOG_TAG, "Added " + cnt + " data points");
                    }
                    curVital = Math.max(0, curVital - 1);
                } else {
                    curVital = 0;
                }
                while (System.currentTimeMillis() - prevTime < POLL_DELAY) {
                    Thread.currentThread().yield();
                }
                callVitalsListWS(curPatient, curVital);
            }
        }).start();
    }

    @Override
    public void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            mRequestList.remove(request);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            mRequestList.remove(request);
        }
    }

    @Override
    public void onRequestCustomError(Request request, Bundle resultData) {
        // Never called.
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onClick(View view) {
    }

    public void setPatient(int pid) {
        if (curPatient != pid) {
            // TODO: derive the initial vid from a DB query on the new PID
            // TODO: clear the old patient graphics
            graphHelper.clearGraph();
            callVitalsListWS(pid, 0);
        }
        this.curPatient = pid;
        // TODO: may need settext on UI thread
        this.patientName.setText("Dummy Patient(" + this.curPatient + ")");
    }

}
