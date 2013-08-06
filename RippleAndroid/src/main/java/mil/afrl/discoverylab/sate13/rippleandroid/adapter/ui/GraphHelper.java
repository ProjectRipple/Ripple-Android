package mil.afrl.discoverylab.sate13.rippleandroid.adapter.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.LinkedList;
import java.util.Queue;

import mil.afrl.discoverylab.sate13.ripple.data.model.MultiValueVital;
import mil.afrl.discoverylab.sate13.ripple.data.model.Vital;
import mil.afrl.discoverylab.sate13.rippleandroid.Common;

public class GraphHelper {

    private boolean plotting = false;

    private Queue<MultiValueVital[]> vitalsQ = new LinkedList<MultiValueVital[]>();

    private static final Long DEFAULT_MAX_X_RANGE = 1000L;

    private double maxX = 0.0;

    private Thread plotter;

    /**
     * The chart view that displays the data.
     */
    private GraphicalView chartView;

    /**
     * The main dataset that includes all the series that go into a chart.
     */
    private XYMultipleSeriesDataset chartSeriesSset = new XYMultipleSeriesDataset();
    /**
     * The main renderer that includes all the renderers customizing a chart.
     */
    private XYMultipleSeriesRenderer chartRenderer = new XYMultipleSeriesRenderer();
    /**
     * The most recently created renderer, customizing the current series.
     */
    private XYSeriesRenderer seriesRenderer;
    /**
     * The most recently added series.
     */
    private XYSeries currentSeries;

    public GraphHelper(Activity activity) {
        // set some properties on the main renderer
        chartRenderer.setApplyBackgroundColor(true);
        chartRenderer.setBackgroundColor(Color.argb(255, 238, 237, 240));
        chartRenderer.setLabelsColor(Color.BLACK);
        chartRenderer.setMarginsColor(Color.WHITE);
        //chartRenderer.setMargins(new int[]{32, 32, 32, 32});
        chartRenderer.setYLabelsPadding((float) 10);
        chartRenderer.setZoomButtonsVisible(false);
        chartRenderer.setShowGrid(true);
        chartRenderer.setGridColor(Color.GRAY);
        chartRenderer.setAxesColor(Color.BLACK);
        chartRenderer.setXLabelsColor(Color.BLACK);
        chartRenderer.setYLabelsColor(0, Color.BLACK);
        chartRenderer.setShowLegend(false);
        chartRenderer.setDisplayValues(false);
        chartRenderer.setYAxisMin(1000.0);
        chartRenderer.setYAxisMax(3000.0);
        chartRenderer.setInitialRange(new double[]{0, 1000, 1500.0, 3000.0});
        chartRenderer.setClickEnabled(false);
        chartRenderer.setPanEnabled(false);
        chartRenderer.setZoomEnabled(false);

        setupSeries();

        chartView = ChartFactory.getLineChartView(activity, chartSeriesSset, chartRenderer);

        chartView.repaint();
    }

    /**
     * save the current data, for instance when changing screen orientation
     *
     * @param outState
     */
    public void save(Bundle outState) {
        outState.putSerializable("dataset", chartSeriesSset);
        outState.putSerializable("renderer", chartRenderer);
        outState.putSerializable("current_series", currentSeries);
        outState.putSerializable("current_renderer", seriesRenderer);
    }

    /**
     * restore the current data, for instance when changing the screen orientation
     *
     * @param savedState
     */
    public void restore(Bundle savedState) {
        if (savedState != null) {
            // restore the current data, for instance when changing the screen orientation
            chartSeriesSset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
            chartRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
            currentSeries = (XYSeries) savedState.getSerializable("current_series");
            seriesRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
        }
    }

    public void clearGraph() {
        currentSeries.clear();
        setupSeries();
        chartView.repaint();
    }

    public GraphicalView getChartView() {
        return chartView;
    }

    public boolean getPlotting() {
        return plotting;
    }

    public boolean isVitalsQEmpty() {
        return vitalsQ.isEmpty();
    }
//
//    public Vital[] vitalsQRemove() {
//        return vitalsQ.remove();
//    }

    public MultiValueVital[] vitalsQRemove() {
        return vitalsQ.remove();
    }

//    public boolean offerVitals(Vital[] vitals) {
//        return vitalsQ.offer(vitals);
//    }

    public boolean offerVitals(MultiValueVital[] vitals) {
        return vitalsQ.offer(vitals);
    }

    public void startPlotter() {
        if (plotter == null) {

            plotter = new Thread(new PlottingThread());
            plotter.setName("Plotter Thread");
            plotter.setDaemon(true);

            plotting = true;
            plotter.start();
        }
    }

    public void stopPlotter() {
        if (plotter != null) {

            plotting = false;
            plotter.interrupt();

            vitalsQ.clear();
            plotter = null;
        }
    }

    private void setupSeries() {

        // create a new series of data
        XYSeries series = new XYSeries("");
        chartSeriesSset.addSeries(series);
        currentSeries = series;

        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(Color.RED);

        chartRenderer.addSeriesRenderer(renderer);
        seriesRenderer = renderer;
    }

    synchronized private boolean addVitalsPoint(double x, Integer y) {
        if (x > maxX) {

            if (maxX != 0) {
                while ((x - maxX) > 25) {
                    maxX += 5.0;
                    //Log.d(Common.LOG_TAG, "Adding (" + maxX + ", 0.0)");
                    currentSeries.add(maxX, 0.0);
                }
            }

            while ((x - currentSeries.getMinX()) > DEFAULT_MAX_X_RANGE && currentSeries.getItemCount() > 0) {
                currentSeries.remove(0);
            }

            //Log.d(Common.LOG_TAG, "Adding (" + x + ", " + y + ")");
            currentSeries.add(x, (double) y);

            maxX = x;

            return true;
        } else {
            Log.d(Common.LOG_TAG, "Graph: Out of order x values (" + x + ", " + y + ") vs. ("
                    + currentSeries.getX(currentSeries.getItemCount() - 1) + ", " +
                    +currentSeries.getY(currentSeries.getItemCount() - 1) + ")");
            return false;
        }
    }

    private class PlottingThread implements Runnable {

        public PlottingThread() {
        }

        @Override
        public void run() {
            while (getPlotting()) {

                if (!isVitalsQEmpty()) {

                    for (MultiValueVital v : vitalsQRemove()) {
                        long timestamp = v.sensor_timestamp;
                        int period = v.period_ms;

                        if (v.value_type == Common.VITAL_TYPES.VITAL_ECG.getValue()) {// && v.pid == curPatient) {

//                            if (addVitalsPoint((double) v.sensor_timestamp, v.value)) {
//                                getChartView().repaint();
//                            }
                            int vitalCount = v.values.length;
                            for (int counter = 0; counter < vitalCount; counter++) {
                                if (addVitalsPoint(timestamp, v.values[counter])) {
                                    getChartView().repaint();
                                }
                                // increment time and counter
                                timestamp += period;
                            }

                        }

                    }

                } else {
                    Thread.yield();
                }

            }
        }
    }

}
