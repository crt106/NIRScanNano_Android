package com.kstechnologies.nanoscan.event;

import com.kstechnologies.nanoscan.event.base.BaseEvent;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

/**
 * @author crt106 on 2019/5/24.
 */
public class GetScanConfEvent extends BaseEvent {
    public static final String ACTION = KSTNanoSDK.GET_SCAN_CONF;

    @Override
    protected String getActionName() {
        return ACTION;
    }
}

