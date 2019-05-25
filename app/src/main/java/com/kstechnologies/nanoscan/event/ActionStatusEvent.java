package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nanoscan.model.DeviceStatus;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-设备状态接收完毕触发 分发设备状态
 * 发送方
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService}
 * @author crt106 on 2019/5/24.
 */
public class ActionStatusEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.ACTION_STATUS;

    @Override
    protected String getActionName() {
        return ACTION;
    }

    private DeviceStatus deviceStatus;

    public ActionStatusEvent(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}

