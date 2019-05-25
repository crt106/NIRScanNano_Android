package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-发送存储在仪器SD卡上的数据
 * 发送方
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService#broadcastUpdate(String, String, byte[])}
 * @author crt106 on 2019/5/24.
 */
public class StoredScanDataEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.STORED_SCAN_DATA;

    @Override
    protected String getActionName() {
        return ACTION;
    }

    /**
     * 扫描文件名称
     */
    private String scanName;

    /**
     * 扫描日期
     */
    private String scanDate;

    /**
     * 扫描文件序号
     */
    private byte[] scanIndex;

    public StoredScanDataEvent(String scanName, String scanDate, byte[] scanIndex) {
        this.scanName = scanName;
        this.scanDate = scanDate;
        this.scanIndex = scanIndex;
    }

    public String getScanName() {
        return scanName;
    }

    public void setScanName(String scanName) {
        this.scanName = scanName;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }

    public byte[] getScanIndex() {
        return scanIndex;
    }

    public void setScanIndex(byte[] scanIndex) {
        this.scanIndex = scanIndex;
    }
}

