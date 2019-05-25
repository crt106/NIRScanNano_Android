package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-Gatt设备连接
 * 发送者
 * @see com.kstechnologies.nanoscan.service.NanoBLEService#mGattCallback
 * 订阅者
 * @see com.kstechnologies.nanoscan.CApplication#onReceiveGattConnected(ActionGattConnectedEvent)
 * @author crt106 on 2019/5/24.
 */
public class ActionGattConnectedEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.ACTION_GATT_CONNECTED;

    @Override
    protected String getActionName() {
        return ACTION;
    }
}

