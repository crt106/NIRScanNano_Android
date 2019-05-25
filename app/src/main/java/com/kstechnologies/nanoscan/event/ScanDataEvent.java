package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-接收一次扫描结果
 * 发送者
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService#broadcastUpdate(String, byte[])}
 * 订阅者
 * {@link com.kstechnologies.nanoscan.activity.newscanactivity.NewScanActivity#onReceiveEvent(ScanDataEvent)}
 *
 * @author crt106 on 2019/5/24.
 */
public class ScanDataEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.SCAN_DATA;

    @Override
    protected String getActionName() {
        return ACTION;
    }

    /**
     * 扫描名称
     */
    private String scanName;
    /**
     * 扫描类型
     */
    private String scanType;
    /**
     * 扫描日期
     */
    private String scanDate;
    /**
     * 真不知道他这个是干什么的
     */
    private String scanPktFmtVer;

    /**
     * 扫描数据
     */
    private byte[] scanData;

    public ScanDataEvent(String scanName, String scanType, String scanDate, String scanPktFmtVer, byte[] scanData) {
        this.scanName = scanName;
        this.scanType = scanType;
        this.scanDate = scanDate;
        this.scanPktFmtVer = scanPktFmtVer;
        this.scanData = scanData;
    }

    public String getScanName() {
        return scanName;
    }

    public void setScanName(String scanName) {
        this.scanName = scanName;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }

    public String getScanPktFmtVer() {
        return scanPktFmtVer;
    }

    public void setScanPktFmtVer(String scanPktFmtVer) {
        this.scanPktFmtVer = scanPktFmtVer;
    }

    public byte[] getScanData() {
        return scanData;
    }

    public void setScanData(byte[] scanData) {
        this.scanData = scanData;
    }
}

