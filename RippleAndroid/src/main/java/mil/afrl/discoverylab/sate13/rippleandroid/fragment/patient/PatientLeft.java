package mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.network.TcpClient;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Patient;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vital;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestManager;

/**
 * Created by Brandon on 6/17/13.
 */
public class PatientLeft extends Fragment implements View.OnClickListener, RequestManager.RequestListener {

    private static final String SAVED_STATE_REQUEST_LIST = "savedStateRequestList";
    protected RippleRequestManager mRequestManager;
    protected ArrayList<Request> mRequestList;

    private Button mNewSeries;

    //private ActivityClickInterface aci;
    private boolean addedSeries = false;

    /**
     * The main dataset that includes all the series that go into a chart.
     */
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    /**
     * The main renderer that includes all the renderers customizing a chart.
     */
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    /**
     * The most recently added series.
     */
    private XYSeries mCurrentSeries;
    /**
     * The most recently created renderer, customizing the current series.
     */
    private XYSeriesRenderer mCurrentRenderer;
    /**
     * The chart view that displays the data.
     */
    private GraphicalView mChartView;

    private View view;
    private int currentPatient;
    private TextView patientName;

    Gson gson = new GsonBuilder().setDateFormat(Common.DATE_TIME_FORMAT).create();

    /*Network Listeners*/
    private TcpClient TCPC = new TcpClient();

    /*Network Clients Message Handler*/
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                gson.fromJson((String) msg.obj, Patient.class);
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, e.getMessage());
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the current data, for instance when changing screen orientation
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        outState.putSerializable("current_series", mCurrentSeries);
        outState.putSerializable("current_renderer", mCurrentRenderer);
        outState.putParcelableArrayList(SAVED_STATE_REQUEST_LIST, mRequestList);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        if (savedState != null) {
            // restore the current data, for instance when changing the screen orientation
            mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
            mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
            mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
            mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");

            mRequestManager = RippleRequestManager.from(getActivity());
            if (savedState != null) {
                mRequestList = savedState.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);
            } else {
                mRequestList = new ArrayList<Request>();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.patient_left, container, false);
        assert view != null;

        // set some properties on the main renderer
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(255, 238, 237, 240));
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setMarginsColor(Color.WHITE);
        //mRenderer.setAxisTitleTextSize(16);
        //mRenderer.setChartTitleTextSize(16);
        //mRenderer.setLabelsTextSize(16);
        //mRenderer.setLegendTextSize(16);
        //mRenderer.setMargins(new int[]{32, 32, 32, 32});
        mRenderer.setYLabelsPadding((float) 10);
        mRenderer.setZoomButtonsVisible(false);
        mRenderer.setShowGrid(true);
        mRenderer.setGridColor(Color.GRAY);
        mRenderer.setAxesColor(Color.BLACK);
        mRenderer.setXLabelsColor(Color.BLACK);
        mRenderer.setYLabelsColor(0, Color.BLACK);

        setupSeries();


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

        // the button that handles the new series of data creation
        mNewSeries = (Button) view.findViewById(R.id.new_series);
        mNewSeries.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callVitalsListWS();
            }
        });

        this.patientName = (TextView) view.findViewById(R.id.name_value_tv);

        return view;
    }

    private void setupSeries() {
        String seriesTitle = "";

        // create a new series of data
        XYSeries series = new XYSeries(seriesTitle);
        mDataset.addSeries(series);
        mCurrentSeries = series;

        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        // set some renderer properties
        renderer.setShowLegendItem(false);
        renderer.setColor(Color.RED);
        renderer.setDisplayChartValues(false);
        //renderer.setDisplayChartValuesDistance(16);

        mRenderer.addSeriesRenderer(renderer);
        mCurrentRenderer = renderer;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.chart);
            mChartView = ChartFactory.getLineChartView(this.getActivity(), mDataset, mRenderer);
            mRenderer.setClickEnabled(false);
            layout.addView(mChartView,
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
            setupSeries();
        }
        mChartView.repaint();

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

    private void callVitalsListWS() {
        Request request = RippleRequestFactory.getVitalListRequest();
        mRequestManager.execute(request, this);
        mRequestList.add(request);
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

                if (mRequestList.contains(request)) {
                    mRequestList.remove(request);

                    ArrayList<Vital> vitalList = resultData.getParcelableArrayList(RippleRequestFactory.BUNDLE_EXTRA_VITAL_LIST);

                    int cnt = 0;
                    for (Vital vital : vitalList) {
                        mCurrentSeries.add(
                                ((double) vital.sensor_timestamp) / 1000.0,
                                ((double) vital.value) / 10000000.0);
                        //Log.i(Common.LOG_TAG, "Added point: (" + (vital.timestamp / 1000.0) + ", " + (vital.value / 10000000.0) + ")");
                        mChartView.repaint();
                        cnt++;
                        Thread.currentThread().yield();
                    }
                    Log.i(Common.LOG_TAG, "Added " + cnt + " data points");
                }
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

    public void setPatient(int id)
    {
        this.currentPatient = id;
        // TODO: may need settext on UI thread
        this.patientName.setText("Dummy Patient(" + this.currentPatient + ")");
    }

}
