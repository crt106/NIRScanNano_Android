package com.kstechnologies.nanoscan.model;

/**
 * 记录设备状态
 *
 * @author crt106 on 2019/5/25.
 */
public class DeviceStatus {

    /**
     * 电量
     */
    private int battLevel;

    /**
     * 温度
     */
    private float temp;

    /**
     * 湿度
     */
    private float humidity;

    /**
     * 设备状态
     */
    private String devStatus;

    /**
     * 错误状态
     */
    private String errStatus;

    public DeviceStatus(int battLevel, float temp, float humidity, String devStatus, String errStatus) {
        this.battLevel = battLevel;
        this.temp = temp;
        this.humidity = humidity;
        this.devStatus = devStatus;
        this.errStatus = errStatus;
    }

    public int getBattLevel() {
        return battLevel;
    }

    public void setBattLevel(int battLevel) {
        this.battLevel = battLevel;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public String getDevStatus() {
        return devStatus;
    }

    public void setDevStatus(String devStatus) {
        this.devStatus = devStatus;
    }

    public String getErrStatus() {
        return errStatus;
    }

    public void setErrStatus(String errStatus) {
        this.errStatus = errStatus;
    }
}
