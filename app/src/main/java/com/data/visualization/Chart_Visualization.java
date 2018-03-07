package com.data.visualization;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Chart_Visualization extends Activity implements OnSeekBarChangeListener,
        OnChartGestureListener, OnChartValueSelectedListener {

    protected BarChart bar_chart1;
    private LineChart line_chart1;
    private PieChart pie_Chart1;
    private RadarChart radar_Chart;
    private SeekBar seekbar_Chart12, seekbar_Chart34;
    private TextView Xaxis, Yaxis;
    int prevMX1 = 0, prevMX2 = 0, prevMY1 = 0, prevMY2 = 0;
    String[] arrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chart_visualization);
        Xaxis = (TextView) findViewById(R.id.tvXMax);
        Yaxis = (TextView) findViewById(R.id.tvYMax);
        seekbar_Chart12 = (SeekBar) findViewById(R.id.seekBar1);
        seekbar_Chart34 = (SeekBar) findViewById(R.id.seekBar2);
        bar_chart1 = (BarChart) findViewById(R.id.chart1);
        bar_chart1.setOnChartValueSelectedListener(this);
        bar_chart1.setDrawBarShadow(false);
        bar_chart1.setDrawValueAboveBar(true);
        bar_chart1.getDescription().setEnabled(false);
        bar_chart1.setMaxVisibleValueCount(60);
        bar_chart1.setPinchZoom(false);
        bar_chart1.setDrawGridBackground(false);
        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(bar_chart1);
        XAxis xAxis = bar_chart1.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);
        IAxisValueFormatter custom = new MyAxisValueFormatter();
        YAxis leftAxis = bar_chart1.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        YAxis rightAxis = bar_chart1.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        Legend l1 = bar_chart1.getLegend();
        l1.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l1.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l1.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l1.setDrawInside(false);
        l1.setForm(LegendForm.SQUARE);
        l1.setFormSize(9f);
        l1.setTextSize(11f);
        l1.setXEntrySpace(4f);
        XYMarkerView mv1 = new XYMarkerView(this, xAxisFormatter);
        mv1.setChartView(bar_chart1); // For bounds control
        bar_chart1.setMarker(mv1); // Set the marker to the chart
        seekbar_Chart34.setOnSeekBarChangeListener(this);
        seekbar_Chart12.setOnSeekBarChangeListener(this);
        seekbar_Chart12.setProgress(12);
        seekbar_Chart34.setProgress(6);

        //--------------------------------------------------------------
        line_chart1 = (LineChart) findViewById(R.id.chart2);
        line_chart1.setOnChartGestureListener(this);
        line_chart1.setOnChartValueSelectedListener(this);
        line_chart1.setDrawGridBackground(false);
        line_chart1.getDescription().setEnabled(false);
        line_chart1.setTouchEnabled(true);
        line_chart1.setDragEnabled(true);
        line_chart1.setScaleEnabled(true);
        line_chart1.setPinchZoom(true);
        MyMarkerView mv2 = new MyMarkerView(this, R.layout.custom_marker_view);
        mv2.setChartView(line_chart1); // For bounds control
        line_chart1.setMarker(mv2); // Set the marker to the chart
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);
        XAxis xAxis2 = line_chart1.getXAxis();
        xAxis2.enableGridDashedLine(10f, 10f, 0f);
        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);
        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);
        YAxis leftAxis2 = line_chart1.getAxisLeft();
        leftAxis2.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis2.addLimitLine(ll1);
        leftAxis2.addLimitLine(ll2);
        leftAxis2.setAxisMaximum(200f);
        leftAxis2.setAxisMinimum(-50f);
        leftAxis2.enableGridDashedLine(10f, 10f, 0f);
        leftAxis2.setDrawZeroLine(false);
        leftAxis2.setDrawLimitLinesBehindData(true);
        line_chart1.getAxisRight().setEnabled(false);
        line_chart1.animateX(2500);
        Legend l2 = line_chart1.getLegend();
        l2.setForm(LegendForm.LINE);

        //--------------------------------------------------
        pie_Chart1 = (PieChart) findViewById(R.id.chart3);
        pie_Chart1.setUsePercentValues(true);
        pie_Chart1.getDescription().setEnabled(false);
        pie_Chart1.setExtraOffsets(5, 10, 5, 5);
        pie_Chart1.setDragDecelerationFrictionCoef(0.95f);
        pie_Chart1.setCenterText("Elections");
        pie_Chart1.setDrawHoleEnabled(true);
        pie_Chart1.setHoleColor(Color.WHITE);
        pie_Chart1.setTransparentCircleColor(Color.WHITE);
        pie_Chart1.setTransparentCircleAlpha(110);
        pie_Chart1.setHoleRadius(58f);
        pie_Chart1.setTransparentCircleRadius(61f);
        pie_Chart1.setDrawCenterText(true);
        pie_Chart1.setRotationAngle(0);
        pie_Chart1.setRotationEnabled(true);
        pie_Chart1.setHighlightPerTapEnabled(true);
        pie_Chart1.setOnChartValueSelectedListener(this);
        pie_Chart1.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = pie_Chart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        pie_Chart1.setEntryLabelColor(Color.WHITE);
        pie_Chart1.setEntryLabelTextSize(12f);

        //-----------------------------------------------------
        radar_Chart = (RadarChart) findViewById(R.id.chart4);
        radar_Chart.setBackgroundColor(Color.rgb(60, 65, 82));
        radar_Chart.getDescription().setEnabled(false);
        radar_Chart.setWebLineWidth(1f);
        radar_Chart.setWebColor(Color.LTGRAY);
        radar_Chart.setWebLineWidthInner(1f);
        radar_Chart.setWebColorInner(Color.LTGRAY);
        radar_Chart.setWebAlpha(100);
        MarkerView mv3 = new RadarMarkerView(this, R.layout.radar_markerview);
        mv3.setChartView(radar_Chart); // For bounds control
        radar_Chart.setMarker(mv3); // Set the marker to the chart
        radar_Chart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);
        XAxis xAxis4 = radar_Chart.getXAxis();

        xAxis4.setTextSize(9f);
        xAxis4.setYOffset(0f);
        xAxis4.setXOffset(0f);
        try {
            JSONObject jObj = new JSONObject(readJSON());
            JSONArray jArr = jObj.getJSONArray("Party vs Salary");
            arrs = new String[jArr.length()];
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jSubObj = jArr.getJSONObject(i);
                arrs[i] = jSubObj.getString("name");
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        xAxis4.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = arrs;

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis4.setTextColor(Color.WHITE);
        YAxis yAxis4 = radar_Chart.getYAxis();

        yAxis4.setLabelCount(5, false);
        yAxis4.setTextSize(9f);
        yAxis4.setAxisMinimum(0f);
        yAxis4.setAxisMaximum(100f);
        yAxis4.setDrawLabels(true);
        Legend l3 = radar_Chart.getLegend();
        l3.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l3.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l3.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l3.setDrawInside(false);

        l3.setXEntrySpace(7f);
        l3.setYEntrySpace(5f);
        l3.setTextColor(Color.WHITE);

        //--------------------------------------------------------

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekbar_Chart12.getProgress() == 0)
            seekbar_Chart12.setProgress(1);
        if (seekbar_Chart34.getProgress() == 0)
            seekbar_Chart34.setProgress(1);
        Xaxis.setText(seekbar_Chart12.getProgress() + " Days");
        Yaxis.setText(seekbar_Chart34.getProgress() + " parties");
        try {
            setBarChart(seekbar_Chart12.getProgress(), 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setLineChart(seekbar_Chart12.getProgress(), 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setPieChart(seekbar_Chart34.getProgress(), 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setRadarChart(5, seekbar_Chart34.getProgress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    public String readJSON() {
        String json = null;
        try {
            InputStream is = getAssets().open("Political_Parties.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setBarChart(int count, float range) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        try {
            JSONObject jObj = new JSONObject(readJSON());
            JSONArray jArr = jObj.getJSONArray("Average_Age");
            for (int i = 0; i < count; i++) {
                if (Math.random() * 100 < 25) {
                    yVals1.add(new BarEntry(i + 1, jArr.getInt(i), getResources().getDrawable(R.mipmap.star)));
                } else {
                    yVals1.add(new BarEntry(i + 1, jArr.getInt(i)));
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }

        BarDataSet set1;

        if (bar_chart1.getData() != null &&
                bar_chart1.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) bar_chart1.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            bar_chart1.getData().notifyDataChanged();
            bar_chart1.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year 2011");

            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);

            data.setBarWidth(0.9f);
            bar_chart1.setData(data);
        }
        if (prevMX1 != count) {
            prevMX1 = count;
            bar_chart1.invalidate();
        }
    }

    private void setLineChart(int count, float range) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        try {
            JSONObject jObj = new JSONObject(readJSON());
            JSONArray jArr = jObj.getJSONArray("First_Career_Politics");
            for (int i = 0; i < count; i++) {
                values.add(new Entry(i, jArr.getInt(i), getResources().getDrawable(R.mipmap.star)));
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        LineDataSet set1;
        if (line_chart1.getData() != null &&
                line_chart1.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) line_chart1.getData().getDataSetByIndex(0);
            set1.setValues(values);
            line_chart1.getData().notifyDataChanged();
            line_chart1.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "DataSet 1");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            line_chart1.setData(data);
        }
        if (prevMX2 != count) {
            prevMX2 = count;
            line_chart1.invalidate();
        }
    }

    private void setPieChart(int count, int range) {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        try {
            JSONObject jObj = new JSONObject(readJSON());
            JSONArray jArr = jObj.getJSONArray("Parties vs Age");
            for (int i = 0; i < count; i++) {
                JSONObject jSubObj = jArr.getJSONObject(i);
                entries.add(new PieEntry(Float.parseFloat(jSubObj.getString("value")),
                        jSubObj.getString("name"),
                        getResources().getDrawable(R.mipmap.star)));
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        if (prevMY1 != count) {
            prevMY1 = count;
            pie_Chart1.setData(data);
            pie_Chart1.highlightValues(null);
            pie_Chart1.invalidate();
        }
    }

    public void setRadarChart(int a, int b) {
        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();
        try {
            JSONObject jObj = new JSONObject(readJSON());
            JSONArray jArr = jObj.getJSONArray("Party vs Salary");
            for (int i = 0; i < b; i++) {
                JSONObject jSubObj = jArr.getJSONObject(i);
                entries1.add(new RadarEntry(Float.parseFloat((jSubObj.getString("value1")))));
                entries2.add(new RadarEntry(Float.parseFloat((jSubObj.getString("value2")))));
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        RadarDataSet set1 = new RadarDataSet(entries2, "Last Week");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries1, "This Week");
        set2.setColor(Color.rgb(121, 162, 175));
        set2.setFillColor(Color.rgb(121, 162, 175));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);

        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);
        if (prevMY2 != b) {
            prevMY2 = b;
            radar_Chart.setData(data);
            radar_Chart.invalidate();
        }
    }

    protected RectF mOnValueSelectedRectF = new RectF();

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        try {
            RectF bounds = mOnValueSelectedRectF;
            bar_chart1.getBarBounds((BarEntry) e, bounds);
            MPPointF position = bar_chart1.getPosition(e, AxisDependency.LEFT);
            MPPointF.recycleInstance(position);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            line_chart1.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

}