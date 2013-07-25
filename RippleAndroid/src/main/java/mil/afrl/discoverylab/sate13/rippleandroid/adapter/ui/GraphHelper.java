package mil.afrl.discoverylab.sate13.rippleandroid.adapter.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by burt on 7/23/13.
 */
public class GraphHelper {

    private static final int DEFAULT_MAX_ITEMS = 100;

    /**
     * The chart view that displays the data.
     */
    public GraphicalView chartView;

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
    public XYSeries currentSeries;


    public GraphHelper(Activity activity, LinearLayout layout) {
        // set some properties on the main renderer
        chartRenderer.setApplyBackgroundColor(true);
        chartRenderer.setBackgroundColor(Color.argb(255, 238, 237, 240));
        chartRenderer.setLabelsColor(Color.BLACK);
        chartRenderer.setMarginsColor(Color.WHITE);
        //chartRenderer.setAxisTitleTextSize(16);
        //chartRenderer.setChartTitleTextSize(16);
        //chartRenderer.setLabelsTextSize(16);
        //chartRenderer.setLegendTextSize(16);
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
        //chartRenderer.setInitialRange(new double[]{0, 2000, -0.3, 0.7});
        chartRenderer.setYAxisMax(0.7);
        chartRenderer.setYAxisMin(-0.3);
        chartRenderer.setClickEnabled(false);
        chartRenderer.setPanEnabled(false);
        chartRenderer.setZoomEnabled(false);

        setupSeries();
        initializeChart(activity, layout);

        //chartView.
        chartView.repaint();
    }

    private void setupSeries() {
        String seriesTitle = "";

        // create a new series of data
        XYSeries series = new XYSeries(seriesTitle);
        chartSeriesSset.addSeries(series);
        currentSeries = series;

        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(Color.RED);

        chartRenderer.addSeriesRenderer(renderer);
        seriesRenderer = renderer;

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

    public void initializeChart(Activity activity, LinearLayout layout) {
        chartView = ChartFactory.getLineChartView(activity, chartSeriesSset, chartRenderer);
    }

    public void clearGraph() {
        currentSeries.clear();
        setupSeries();
        chartView.repaint();
    }

    public boolean addPoint(double x, double y) {
        boolean res = false;
        if (x > currentSeries.getMaxX()) {
            if (currentSeries.getItemCount() > DEFAULT_MAX_ITEMS) {
                currentSeries.remove(0);
            }
            currentSeries.add(x, y);
            //Log.i(Common.LOG_TAG, "Added point: (" + (vital.timestamp / 1000.0)
            // + ", " + (vital.value / 10000000.0) + ")");
            chartView.repaint();
            res = true;
        }
        return res;
    }
}
