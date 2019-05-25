package com.kstechnologies.nanoscan.activity.analyseactivity;

import com.github.mikephil.charting.data.Entry;
import com.kstechnologies.nanoscan.utils.MPAndroidChartUtil;

import java.util.List;

/**
 * 存放AnalyseActivity中线性图表信息的东东哦
 *
 * @author crt106 on 2019/5/16.
 */
public class LineChartPara {

    private String dataLabel;
    private List<Entry> yValues;
    private MPAndroidChartUtil.ChartType chartType;

    public LineChartPara(String dataLabel, List<Entry> yValues, MPAndroidChartUtil.ChartType chartType) {
        this.dataLabel = dataLabel;
        this.yValues = yValues;
        this.chartType = chartType;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public void setDataLabel(String dataLabel) {
        this.dataLabel = dataLabel;
    }

    public List<Entry> getyValues() {
        return yValues;
    }

    public void setyValues(List<Entry> yValues) {
        this.yValues = yValues;
    }

    public MPAndroidChartUtil.ChartType getChartType() {
        return chartType;
    }

    public void setChartType(MPAndroidChartUtil.ChartType chartType) {
        this.chartType = chartType;
    }
}
