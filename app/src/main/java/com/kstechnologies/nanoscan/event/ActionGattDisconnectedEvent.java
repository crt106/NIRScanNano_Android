package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-Gatt断开连接
 * 发布者
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService#mGattCallback}
 * 订阅者
 * {@link com.kstechnologies.nanoscan.CApplication#onReceiveGattDisConnected(ActionGattDisconnectedEvent)}
 * {@link com.kstechnologies.nanoscan.activity.newscanactivity.NewScanActivity#onReceiveEvent(ActionGattDisconnectedEvent)}
 * {@link com.kstechnologies.nanoscan.fragment.setttingfragment.SettingFragment#onReceiveEvent(ActionGattDisconnectedEvent)}
 * {@link com.kstechnologies.nanoscan.activity.ConfigureActivity#onEventReceive(ActionGattDisconnectedEvent)}
 * @author crt106 on 2019/5/24.
 */
public class ActionGattDisconnectedEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.ACTION_GATT_DISCONNECTED;

    @Override
    protected String getActionName() {
        return ACTION;
    }
}

