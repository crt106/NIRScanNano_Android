package com.kstechnologies.nanoscan.model;

import com.opencsv.bean.CsvBindByName;

/**
 * 单个测量点数据 每个单独的测量点包含
 * 波长(WaveLength) nm
 * 强度(Intensity)
 * 吸收率(Absorbance)
 * 反射率(Reflectance)
 * @author crt106 on 2019/5/12.
 */
public class MeasurePoint {

    /**
     * 波长
     */
    @CsvBindByName(column = "WaveLength")
    private float waveLength;

    /**
     * 强度
     */
    @CsvBindByName(column = "Intensity")
    private float intensity;

    /**
     * 吸收率
     */
    @CsvBindByName(column = "Absorbance")
    private float absorbance;

    /**
     * 反射率
     */
    @CsvBindByName(column = "Reflectance")
    private float reflectance;

    public MeasurePoint(){

    }

    /**
     * 全参数构造方法
     * @param waveLength
     * @param intensity
     * @param absorbance
     * @param reflectance
     */
    public MeasurePoint(float waveLength, float intensity, float absorbance, float reflectance)
    {
        this.waveLength = waveLength;
        this.intensity = intensity;
        this.absorbance = absorbance;
        this.reflectance = reflectance;
    }


    public float getWaveLength() {
        return waveLength;
    }

    public void setWaveLength(float waveLength) {
        this.waveLength = waveLength;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getAbsorbance() {
        return absorbance;
    }

    public void setAbsorbance(float absorbance) {
        this.absorbance = absorbance;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

}
