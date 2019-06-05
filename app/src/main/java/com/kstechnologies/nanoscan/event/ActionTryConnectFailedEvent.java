package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;

/**
 * 事件-尝试连接到Nano失败
 * 发送者
 * {@link com.kstechnologies.nanoscan.activity.mainactivity.MainActivity}
 * 订阅者
 * {@link com.kstechnologies.nanoscan.fragment.setttingfragment.SettingFragment#onReceiveEvent(ActionTryConnectFailedEvent)}
 * @author crt106 on 2019/6/5.
 */
public class ActionTryConnectFailedEvent extends BaseEvent {

    public static final String ACTION = "Try_Connect_failed";

    @Override
    protected String getActionName() {
        return null;
    }
}
