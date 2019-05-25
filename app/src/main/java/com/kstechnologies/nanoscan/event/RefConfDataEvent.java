package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件- 发送参考系数、参考矩阵信息
 * 发送方
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService#broadcastUpdate(String, byte[], byte[])}
 * 订阅方
 * {@link com.kstechnologies.nanoscan.activity.mainactivity.MainActivity#onReceiveEvent(RefConfDataEvent)}
 * @author crt106 on 2019/5/24.
 */
public class RefConfDataEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.REF_CONF_DATA;

    @Override
    protected String getActionName() {
        return ACTION;
    }

    /**
     * 参考系数
     */
    private byte[] refCoeff;

    /**
     * 参考矩阵
     */
    private byte[] refMatrix;

    /**
     * 扫描数据 有点不懂这个是指什么
     */
    private byte[] scanData;

    public RefConfDataEvent(byte[] refCoeff, byte[] refMatrix, byte[] scanData) {
        this.refCoeff = refCoeff;
        this.refMatrix = refMatrix;
        this.scanData = scanData;
    }

    public RefConfDataEvent(byte[] refCoeff, byte[] refMatrix) {
        this.refCoeff = refCoeff;
        this.refMatrix = refMatrix;
    }

    public byte[] getScanData() {
        return scanData;
    }

    public void setScanData(byte[] scanData) {
        this.scanData = scanData;
    }

    public byte[] getRefCoeff() {
        return refCoeff;
    }

    public void setRefCoeff(byte[] refCoeff) {
        this.refCoeff = refCoeff;
    }

    public byte[] getRefMatrix() {
        return refMatrix;
    }

    public void setRefMatrix(byte[] refMatrix) {
        this.refMatrix = refMatrix;
    }
}

