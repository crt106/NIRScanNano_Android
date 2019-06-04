package com.kstechnologies.nanoscan.model;

import java.io.Serializable;

/**
 * 单个测量点数据 每个单独的测量点包含
 * 波长(WaveLength) nm
 * 强度(Intensity)
 * 吸收率(Absorbance)
 * 反射率(Reflectance)
 *
 * @author crt106 on 2019/5/12.
 */
public class MeasurePoint implements Serializable {

    /**
     * 波长
     */
    private float wavelength;

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

    public MeasurePoint() {

    }

    /**
     * 全参数构造方法
     *
     * @param wavelength
     * @param intensity
     * @param absorbance
     * @param reflectance
     */
    public MeasurePoint(float wavelength, float intensity, float absorbance, float reflectance)
    {
        this.wavelength = wavelength;
        this.intensity = intensity;
        this.absorbance = absorbance;
        this.reflectance = reflectance;
    }


    public float getWavelength() {
        return wavelength;
    }

    public void setWavelength(float wavelength) {
        this.wavelength = wavelength;
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

    @Override
    public String toString() {
        return "MeasurePoint{" +
                "wavelength=" + wavelength +
                ", intensity=" + intensity +
                ", absorbance=" + absorbance +
                ", reflectance=" + reflectance +
                '}';
    }

    /**
     * 转换为CSV输出形式的数组 这个方法有点笨的
     *
     * @return
     */
    public String[] toCSV() {
        String[] out = new String[4];
        out[0] = String.valueOf(getWavelength());
        out[1] = String.valueOf(getIntensity());
        out[2] = String.valueOf(getAbsorbance());
        out[3] = String.valueOf(getReflectance());
        return out;
    }
}
