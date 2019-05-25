package com.kstechnologies.nanoscan.model;

/**
 * 记录设备信息
 * @author crt106 on 2019/5/25.
 */
public class DeviceInfo {

    /**
     * 制造商名称
     */
    private String manufName;

    /**
     * 型号名称
     */
    private String modelNum;

    /**
     * 序列号
     */
    private String serialNum;

    /**
     * 硬件版本
     */
    private String hardwareRev;

    /**
     * Tiva版本 不知道干啥的
     */
    private String tivaRev;

    /**
     * 光谱版本
     */
    private String spectrumRev;

    public DeviceInfo(String manufName, String modelNum, String serialNum, String hardwareRev, String tivaRev,
                           String spectrumRev)
    {
        this.manufName = manufName;
        this.modelNum = modelNum;
        this.serialNum = serialNum;
        this.hardwareRev = hardwareRev;
        this.tivaRev = tivaRev;
        this.spectrumRev = spectrumRev;
    }

    public String getManufName() {
        return manufName;
    }

    public void setManufName(String manufName) {
        this.manufName = manufName;
    }

    public String getModelNum() {
        return modelNum;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getHardwareRev() {
        return hardwareRev;
    }

    public void setHardwareRev(String hardwareRev) {
        this.hardwareRev = hardwareRev;
    }

    public String getTivaRev() {
        return tivaRev;
    }

    public void setTivaRev(String tivaRev) {
        this.tivaRev = tivaRev;
    }

    public String getSpectrumRev() {
        return spectrumRev;
    }

    public void setSpectrumRev(String spectrumRev) {
        this.spectrumRev = spectrumRev;
    }
}
