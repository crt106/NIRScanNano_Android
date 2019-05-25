package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件 -发送扫描请求 触发一次扫描
 * 与{@link StartScanEvent}的不同可能就是在于最终调用的方法不同 我有点搞不清楚原来是怎么想的
 * 本事件在原先的本地广播中最终调用{@link KSTNanoSDK#startScan(byte[])} 该方法直接指示一次扫描
 * 发送者
 * TODO 添加发送者newScanActivity
 *
 * @author crt106 on 2019/5/24.
 */
public class SendDataEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.SEND_DATA;

    @Override
    protected String getActionName() {
        return ACTION;
    }
}

