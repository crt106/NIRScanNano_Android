package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-发现Gatt Service
 * 发送方
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService}
 * @author crt106 on 2019/5/24.
 */
public class ActionGattServicesDiscoveredEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.ACTION_GATT_SERVICES_DISCOVERED;

    @Override
    protected String getActionName() {
        return ACTION;
    }
}

