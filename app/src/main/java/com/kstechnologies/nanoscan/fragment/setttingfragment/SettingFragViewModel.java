package com.kstechnologies.nanoscan.fragment.setttingfragment;

import androidx.databinding.ObservableBoolean;

import com.kstechnologies.nanoscan.CApplication;
import com.kstechnologies.nanoscan.viewmodel.BaseViewModel;

/**
 * ViewModel for SettingFragment
 *
 * @author crt106 on 2019/5/19.
 */
public class SettingFragViewModel extends BaseViewModel {


    /**
     * 是否连接到Nano 直接与{@link CApplication#connected}相关
     */
    public ObservableBoolean connected = new ObservableBoolean();

    public ObservableBoolean getConnected() {
        return connected;
    }

    public void setConnected(ObservableBoolean connected) {
        this.connected = connected;
    }

}
