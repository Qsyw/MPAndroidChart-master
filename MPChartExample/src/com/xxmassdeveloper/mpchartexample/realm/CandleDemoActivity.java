
package com.xxmassdeveloper.mpchartexample.realm;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.CombineTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.custom.DayAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.custom.MyAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.custom.XYMarkerView;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class CandleDemoActivity extends DemoBase implements OnSeekBarChangeListener, OnChartValueSelectedListener {

    private CandleStickChart mCandleChart;
    private SeekBar          mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private BarChart mBarChart;
    protected RectF mOnValueSelectedRectF = new RectF();
    // if more than 60 entries are displayed in the chart, no values will be drawn
    private   int   mMaxVisibleValueCount = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_candle_demo);

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarX.setOnSeekBarChangeListener(this);

        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarY.setOnSeekBarChangeListener(this);

        initBarChart();
        initCandleChart();
        handleTouch();
    }

    private void handleTouch() {
        final BarLineChartTouchListener candleTouchListener        = (BarLineChartTouchListener) mCandleChart.getOnTouchListener();
        final BarLineChartTouchListener barTouchListener        =  (BarLineChartTouchListener) mBarChart.getOnTouchListener();
        candleTouchListener.setCombineTouchListener(new CombineTouchListener() {
            @Override
            public void onZoom(MotionEvent event) {
                barTouchListener.onTouch(mBarChart, event, false);
            }
        });
        barTouchListener.setCombineTouchListener(new CombineTouchListener() {
            @Override
            public void onZoom(MotionEvent event) {
                candleTouchListener.onTouch(mCandleChart, event, false);
            }
        });
    }

    private void initBarChart() {

        mBarChart = (BarChart) findViewById(R.id.chart2);
        mBarChart.setBackgroundColor(Color.WHITE);
        mBarChart.setOnChartValueSelectedListener(this);

        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);

        mBarChart.getDescription().setEnabled(false);

        mBarChart.setMaxVisibleValueCount(mMaxVisibleValueCount);

        // scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);

        mBarChart.setDrawGridBackground(false);
        // mBarChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mBarChart);

        XAxis xAxis = mBarChart.getXAxis();
//        xAxis.setPosition(XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTfLight);
//        xAxis.setDrawGridLines(false);
//        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mBarChart.getAxisLeft();
        //        leftAxis.setTypeface(mTfLight);
        //        leftAxis.setLabelCount(8, false);
        //        leftAxis.setDrawGridLines(false);
        //        leftAxis.setValueFormatter(custom);
        //        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //        leftAxis.setSpaceTop(15f);
                leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(mBarChart); // For bounds control
        mBarChart.setMarker(mv); // Set the marker to the chart
    }

    private void initCandleChart() {
        mCandleChart = (CandleStickChart) findViewById(R.id.chart1);
        mCandleChart.setBackgroundColor(Color.WHITE);

        mCandleChart.getDescription().setEnabled(false);

        mCandleChart.setMaxVisibleValueCount(mMaxVisibleValueCount);

        // scaling can now only be done on x- and y-axis separately
        mCandleChart.setPinchZoom(true);

        mCandleChart.setDrawGridBackground(false);

        XAxis xAxis = mCandleChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mCandleChart.getAxisLeft();
        //        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        //        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mCandleChart.getAxisRight();
        rightAxis.setEnabled(false);
        //        rightAxis.setStartAtZero(false);

        // setting data
        mSeekBarX.setProgress(40);
        mSeekBarY.setProgress(100);

        mCandleChart.getLegend().setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.candle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleHighlight: {// 是否有选中效果
                if (mCandleChart.getData() != null) {
                    mCandleChart.getData().setHighlightEnabled(!mCandleChart.getData().isHighlightEnabled());
                    mCandleChart.invalidate();
                }
                break;
            }
            case R.id.actionTogglePinch: {
                if (mCandleChart.isPinchZoomEnabled())
                    mCandleChart.setPinchZoom(false);
                else
                    mCandleChart.setPinchZoom(true);

                mCandleChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mCandleChart.setAutoScaleMinMaxEnabled(!mCandleChart.isAutoScaleMinMaxEnabled());
                mCandleChart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleMakeShadowSameColorAsCandle: {
                for (ICandleDataSet set : mCandleChart.getData().getDataSets()) {
                    //TODO: set.setShadowColorSameAsCandle(!set.getShadowColorSameAsCandle());
                }

                mCandleChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mCandleChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mCandleChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {

                mCandleChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionSave: {
                if (mCandleChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int prog = (mSeekBarX.getProgress() + 1);

        tvX.setText("" + prog);
        tvY.setText("" + (mSeekBarY.getProgress()));

        mCandleChart.resetTracking();

        ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();
        ArrayList<BarEntry>    yVals2 = new ArrayList<BarEntry>();

        for (int i = 0; i < prog; i++) {
            float mult = (mSeekBarY.getProgress() + 1);
            float val  = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float low  = (float) (Math.random() * 9) + 8f;

            float open  = (float) (Math.random() * 6) + 1f;
            float close = (float) (Math.random() * 6) + 1f;

            boolean even = i % 2 == 0;

            yVals1.add(new CandleEntry(i, val + high, val - low, even ? val + open : val - open,
                    even ? val - close : val + close));

            final BarEntry barEntry = new BarEntry(i, Math.abs(open + close));
            barEntry.setColor(even ? Color.RED : Color.rgb(122, 242, 84));
            yVals2.add(barEntry);
        }

        CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.rgb(122, 242, 84));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.BLUE);
        set1.setHighlightLineWidth(1f);

        CandleData data = new CandleData(set1);

        mCandleChart.setData(data);
        mCandleChart.invalidate();
        setData(yVals2);
    }

    private void setData(ArrayList<BarEntry> yVals2) {

        BarDataSet set1;

        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals2);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals2, "Data Set");
             int[] colors = {Color.RED, Color.rgb(122, 242, 84)};
            set1.setColors(colors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            mBarChart.setData(data);
        }
        mBarChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mBarChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mBarChart.getPosition(e, AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mBarChart.getLowestVisibleX() + ", high: "
                        + mBarChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

    }
}
