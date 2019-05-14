package com.kstechnologies.nanoscan.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.kstechnologies.nanoscan.R;
import com.kstechnologies.nanoscan.activity.graphactivity.GraphListItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 记录单个csv文件对应的扫描信息
 *
 * @author crt106 on 2019/5/13.
 */
public class MeasureDictionary implements Serializable {

    /**
     * 字典名称
     */
    @SerializedName("Name")
    private String name;

    /**
     * 测量方式
     */
    @SerializedName("Method")
    private String method;

    /**
     * 扫描时间戳
     */
    @SerializedName("Timestamp")
    private String timeStamp;

    /**
     * 光谱范围起始值
     */
    @SerializedName("Spectral Range Start (nm)")
    private String spectralRangeStart;

    /**
     * 光谱范围结束值
     */
    @SerializedName("Spectral Range End (nm)")
    private String spectralRangeEnd;

    /**
     * 测量点个数
     */
    @SerializedName("Number of Wavelength Points")
    private String numberofWavelengthPoints;

    /**
     * 数字分辨率
     */
    @SerializedName("Digital Resolution")
    private String digitalResolution;

    /**
     * 平均扫描数
     */
    @SerializedName("Number of Scans to Average")
    private String numberofScanstoAverage;


    /**
     * 总计测量时间
     */
    @SerializedName("Total Measurement Time (s)")
    private String totalMeasurementTime;

    public MeasureDictionary() {

    }

    public MeasureDictionary(String name, String method, String timeStamp, String spectralRangeStart, String spectralRangeEnd, String numberofWavelengthPoints, String digitalResolution, String numberofScanstoAverage, String totalMeasurementTime) {
        this.name = name;
        this.method = method;
        this.timeStamp = timeStamp;
        this.spectralRangeStart = spectralRangeStart;
        this.spectralRangeEnd = spectralRangeEnd;
        this.numberofWavelengthPoints = numberofWavelengthPoints;
        this.digitalResolution = digitalResolution;
        this.numberofScanstoAverage = numberofScanstoAverage;
        this.totalMeasurementTime = totalMeasurementTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSpectralRangeStart() {
        return spectralRangeStart;
    }

    public void setSpectralRangeStart(String spectralRangeStart) {
        this.spectralRangeStart = spectralRangeStart;
    }

    public String getSpectralRangeEnd() {
        return spectralRangeEnd;
    }

    public void setSpectralRangeEnd(String spectralRangeEnd) {
        this.spectralRangeEnd = spectralRangeEnd;
    }

    public String getNumberofWavelengthPoints() {
        return numberofWavelengthPoints;
    }

    public void setNumberofWavelengthPoints(String numberofWavelengthPoints) {
        this.numberofWavelengthPoints = numberofWavelengthPoints;
    }

    public String getDigitalResolution() {
        return digitalResolution;
    }

    public void setDigitalResolution(String digitalResolution) {
        this.digitalResolution = digitalResolution;
    }

    public String getNumberofScanstoAverage() {
        return numberofScanstoAverage;
    }

    public void setNumberofScanstoAverage(String numberofScanstoAverage) {
        this.numberofScanstoAverage = numberofScanstoAverage;
    }

    public String getTotalMeasurementTime() {
        return totalMeasurementTime;
    }

    public void setTotalMeasurementTime(String totalMeasurementTime) {
        this.totalMeasurementTime = totalMeasurementTime;
    }

    /**
     * 获得展示在界面上的数据集
     *
     * @return
     */
    public ArrayList<GraphListItem> getDict(Context context) {
        ArrayList<GraphListItem> arrayList = new ArrayList<>();
        arrayList.add(new GraphListItem(context.getString(R.string.scan_method), method));
        arrayList.add(new GraphListItem(context.getString(R.string.timeStamp), timeStamp));
        arrayList.add(new GraphListItem(context.getString(R.string.spectralRangeStart), spectralRangeStart));
        arrayList.add(new GraphListItem(context.getString(R.string.spectralRangeEnd), spectralRangeEnd));
        arrayList.add(new GraphListItem(context.getString(R.string.number_of_WavelengthPoints), numberofWavelengthPoints));
        arrayList.add(new GraphListItem(context.getString(R.string.digitalResolution), digitalResolution));
        arrayList.add(new GraphListItem(context.getString(R.string.number_of_ScanstoAverage), numberofScanstoAverage));
        arrayList.add(new GraphListItem(context.getString(R.string.totalMeasurementTime), totalMeasurementTime));
        return arrayList;
    }

    @Override
    public String toString() {
        return "MeasureDictionary{" +
                "name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", spectralRangeStart='" + spectralRangeStart + '\'' +
                ", spectralRangeEnd='" + spectralRangeEnd + '\'' +
                ", numberofWavelengthPoints='" + numberofWavelengthPoints + '\'' +
                ", digitalResolution='" + digitalResolution + '\'' +
                ", numberofScanstoAverage='" + numberofScanstoAverage + '\'' +
                ", totalMeasurementTime='" + totalMeasurementTime + '\'' +
                '}';
    }
}
