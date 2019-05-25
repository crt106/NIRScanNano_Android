package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nanoscan.service.NanoBLEService;

/**
 * 事件-扫描请求已收到 扫描已开始
 * 发布者
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService}
 * 订阅者
 * {@link com.kstechnologies.nanoscan.activity.newscanactivity.NewScanActivity#onReceiveEvent(ActionScanStartedEvent)}
 *
 * @author crt106 on 2019/5/24.
 */
public class ActionScanStartedEvent extends BaseEvent {
    public static final String ACTION = NanoBLEService.ACTION_SCAN_STARTED;

    @Override
    protected String getActionName() {
        return ACTION;
    }
}

