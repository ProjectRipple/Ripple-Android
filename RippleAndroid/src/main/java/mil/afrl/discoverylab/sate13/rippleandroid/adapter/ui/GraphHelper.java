package mil.afrl.discoverylab.sate13.rippleandroid.adapter.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.Util;
import mil.afrl.discoverylab.sate13.rippleandroid.mqtt.PublishedMessage;

public class GraphHelper {

    private boolean plotting = false;

    private Queue<PublishedMessage> vitalsQ = new LinkedList<PublishedMessage>();

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
    // The last sequence number found (reset when graph is cleared)
    private long lastEcgSeq = 0;
    // how much to increment x by between ECG values
    private final double X_INCREMENT = 1;
    // number of ecg readings per packet
    private final int ECG_SAMPLES_PER_PACKET = 100;
    // false at start, true after first samples is read to set a baseline for graph to grow from
    private boolean isBaselineSet = false;

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
        chartRenderer.setYAxisMin(1500.0);
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
        maxX = 0.0;
        currentSeries.clear();
        setupSeries();
        chartView.repaint();
        lastEcgSeq = 0;
        isBaselineSet = false;
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

    public PublishedMessage vitalsQRemove() {
        return vitalsQ.remove();
    }

    public boolean offerVitals(PublishedMessage vitals) {
        return vitalsQ.offer(vitals);
    }

    public void startPlotter() {
        if (plotter == null) {

            // reset may y
            chartRenderer.setYAxisMax(3000.0);

            plotter = new Thread(new PlottingThread());
            plotter.setName("Plotter Thread");
            plotter.setDaemon(true);

            plotting = true;
            plotter.start();
            lastEcgSeq = 0;
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

            if (!isBaselineSet && y > 25) {
                chartRenderer.setYAxisMax((double) (y + 25));
                chartRenderer.setYAxisMin((double) (y - 25));
                isBaselineSet = true;
            }
//            if (maxX != 0) {
//                while ((x - maxX) > 25) {
//                    maxX += 5.0;
//                    //Log.d(Common.LOG_TAG, "Adding (" + maxX + ", 0.0)");
//                    currentSeries.add(maxX, Common.SIM_BASELINE_GUESS);
//                }
//            }

            while ((x - currentSeries.getMinX()) > DEFAULT_MAX_X_RANGE && currentSeries.getItemCount() > 0) {
                currentSeries.remove(0);
            }

            if (y > chartRenderer.getYAxisMax() && y != 0) {
                chartRenderer.setYAxisMax((double) (y + 25));
            }
            if (y < chartRenderer.getYAxisMin() && y > 25) {
                chartRenderer.setYAxisMin((double) (y - 25));
            }
            //Log.d(Common.LOG_TAG, "Adding (" + x + ", " + y + ")");
            currentSeries.add(x, (double) y);

            maxX = x;

            return true;
        } else {
            Log.d(Common.LOG_TAG, "Graph: Out of order x values (" + x + ", " + y + ")");
            /*vs. ("
                    + currentSeries.getX(currentSeries.getItemCount() - 1) + ", " +
                    +currentSeries.getY(currentSeries.getItemCount() - 1) + ")");*/
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

                    PublishedMessage ecgMsg = vitalsQRemove();

                    byte[] streamBytes = Util.hexStringToByteArray(ecgMsg.getPayload());
                    long seq = Util.convert4BytesToUInt(Arrays.copyOfRange(streamBytes, 0, 4));


                    double x = maxX + X_INCREMENT;


                    if (seq > lastEcgSeq) {
                        if (seq != lastEcgSeq + 1 && lastEcgSeq != 0) {
                            Log.d(Common.LOG_TAG, "Missing sequence number between " + lastEcgSeq + " and " + seq);
                            // Missed stream messages
                            long missedSeq = seq - (lastEcgSeq + 1);
                            addVitalsPoint(maxX, 0);
                            addVitalsPoint(maxX + (ECG_SAMPLES_PER_PACKET * missedSeq), 0);
                            x = maxX + X_INCREMENT;
                        }
                        lastEcgSeq = seq;
                        // start at 6 instead of 4 because of 2 extra 0 bytes at start of stream data
                        for (int i = 6; i < streamBytes.length; i += 2) {
                            if (addVitalsPoint(x, Util.convert2BytesToUInt(streamBytes[i], streamBytes[i + 1]))) {
                                getChartView().repaint();
                            }
                            x += X_INCREMENT;
                        }
                    } else {
                        Log.d(Common.LOG_TAG, "ECG stream out of sequence. Current: " + lastEcgSeq + " got " + seq);
                        //TODO: how to handle out of sequence messages?
                        // If # of samples per frame and difference between sequence numbers is known, can calculate skip in graph
                        // otherwise save all the values somewhere and just insert missing sequence there and redraw
                    }

                } else {
                    Thread.yield();
                }

            }
        }
    }

}
