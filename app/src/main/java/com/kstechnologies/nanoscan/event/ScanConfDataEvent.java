package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-发送扫描配置
 * 发送方
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService#broadcastScanConfig(String, byte[])}
 * 接收方
 * {@link com.kstechnologies.nanoscan.activity.mainactivity.MainActivity#onReceiveEvent(ScanConfDataEvent)}
 *
 * @author crt106 on 2019/5/24.
 */
public class ScanConfDataEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.SCAN_CONF_DATA;

    @Override
    protected String getActionName() {
        return ACTION;
    }

    private byte[] scanData;

    public ScanConfDataEvent(byte[] scanData) {
        this.scanData = scanData;
    }

    public byte[] getScanData() {
        return scanData;
    }

    public void setScanData(byte[] scanData) {
        this.scanData = scanData;
    }
}

