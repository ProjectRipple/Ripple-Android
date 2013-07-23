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
public class MapHelper {

    /**
     * The chart view that displays the data.
     */
    public GraphicalView mChartView;
    /**
     * The most recently added series.
     */
    public XYSeries mCurrentSeries;

    /**
     * The main dataset that includes all the series that go into a chart.
     */
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    /**
     * The main renderer that includes all the renderers customizing a chart.
     */
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    /**
     * The most recently created renderer, customizing the current series.
     */
    private XYSeriesRenderer mCurrentRenderer;

    public MapHelper(Activity activity, LinearLayout layout) {
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
        initializeChart(activity, layout);

        mChartView.repaint();
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

    /**
     * save the current data, for instance when changing screen orientation
     *
     * @param outState
     */
    public void save(Bundle outState) {
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        outState.putSerializable("current_series", mCurrentSeries);
        outState.putSerializable("current_renderer", mCurrentRenderer);
    }

    /**
     * restore the current data, for instance when changing the screen orientation
     *
     * @param savedState
     */
    public void restore(Bundle savedState) {
        if (savedState != null) {
            // restore the current data, for instance when changing the screen orientation
            mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
            mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
            mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
            mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
        }
    }

    public void initializeChart(Activity activity, LinearLayout layout) {
        mChartView = ChartFactory.getLineChartView(activity, mDataset, mRenderer);
        mRenderer.setClickEnabled(false);
    }
}
