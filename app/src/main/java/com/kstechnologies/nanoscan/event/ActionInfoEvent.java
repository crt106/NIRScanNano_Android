package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nanoscan.model.DeviceInfo;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-接收到设备信息时触发 传递设备信息
 * 发送方
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService}
 *
 * @author crt106 on 2019/5/24.
 */
public class ActionInfoEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.ACTION_INFO;

    @Override
    protected String getActionName() {
        return ACTION;
    }

    private DeviceInfo deviceInfo;

    public ActionInfoEvent(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}

