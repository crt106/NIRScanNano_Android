package com.kstechnologies.nanoscan.model;

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
    private float waveLength;

    /**
     * 强度
     */
    private float intensity;

    /**
     * 吸收率
     */
    private float absorbance;

    /**
     * 反射率
     */
    private float reflectance;

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
