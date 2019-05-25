package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件-当所有的Characteristic都写入了[接收GATT通知]之后触发 通知其他组件可以开始操作了
 * 发送方
 * {@link com.kstechnologies.nanoscan.service.NanoBLEService}
 * 订阅方
 * {@link com.kstechnologies.nanoscan.activity.mainactivity.MainActivity#onReceiveEvent(ActionNotifyDoneEvent)}
 * {@link com.kstechnologies.nanoscan.fragment.setttingfragment.SettingFragment#onReceiveEvent(ActionNotifyDoneEvent)}
 * @author crt106 on 2019/5/24.
 */
public class ActionNotifyDoneEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.ACTION_NOTIFY_DONE;

    @Override
    protected String getActionName() {
        return ACTION;
    }
}

