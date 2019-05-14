package com.kstechnologies.nanoscan.utils;

import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * 操作MPAndroid图标的工具类
 *
 * @author crt106 on 2019/5/12.
 */
public class MPAndroidChartUtil {

    /**
     * 表格枚举类型
     * Enumeration of chart types
     */
    public enum ChartType {
        /**
         * 反射率
         */
        REFLECTANCE,

        /**
         * 吸收率
         */
        ABSORBANCE,

        /**
         * 强度
         */
        INTENSITY
    }

    /**
     * 设置LineChart类型表格 用于显示反射率、吸收率、强度图像
     *
     * @param mChart
     * @return
     */
    public static LineChart setLineChart(LineChart mChart, String dataLabel, ArrayList<Entry> yValues, ChartType chartType)
    {
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription(new Description());
        //mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines


        mChart.setAutoScaleMinMaxEnabled(true);

        leftAxis.setSpaceBottom(10f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        // add data
        setData(mChart, dataLabel, yValues, chartType);

        mChart.animateX(2500, Easing.EaseInOutQuart);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        return mChart;
    }

    /**
     * 对具体的表格设置具体的数据
     * Set the X-axis and Y-axis data for a specified chart
     *
     * @param mChart  the chart to update the data for
     * @param yValues the Y-axis values to be plotted
     * @param type    the type of chart to be displayed {@link ChartType}
     */
    private static void setData(LineChart mChart, String dataLabel, ArrayList<Entry> yValues, ChartType type) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet set1 = new LineDataSet(yValues, dataLabel);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setDrawFilled(true);
        if (type == ChartType.REFLECTANCE) {
            set1.setCircleColor(Color.RED);
            set1.setFillColor(Color.RED);

        } else if (type == ChartType.ABSORBANCE) {
            set1.setCircleColor(Color.GREEN);
            set1.setFillColor(Color.GREEN);

        } else if (type == ChartType.INTENSITY) {
            set1.setCircleColor(Color.BLUE);
            set1.setFillColor(Color.BLUE);

        }
        // 添加LineDataSet到数据集
        dataSets.add(set1);
        // create a data object with the datasets
        LineData data = new LineData(dataSets);
        // set data
        mChart.setData(data);
        mChart.setMaxVisibleValueCount(20);
    }
}
