package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * 事件 -发送扫描请求 触发一次扫描
 * 与{@link SendDataEvent}的不同可能就是在于最终调用的方法不同 我有点搞不清楚原来是怎么想的
 * 本事件在原先的本地广播中最终调用{@link KSTNanoSDK#setStub(byte[])}
 * 该方法先设置文件名?然后再在设置完成的回调中调用{@link KSTNanoSDK#startScan(byte[])}}
 * TODO 添加发送者newScanActivity
 *
 * @author crt106 on 2019/5/24.
 */
public class StartScanEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.START_SCAN;

    @Override
    protected String getActionName() {
        return ACTION;
    }
}

