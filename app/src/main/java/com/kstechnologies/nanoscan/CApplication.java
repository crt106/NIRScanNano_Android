package com.kstechnologies.nanoscan;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.kstechnologies.nanoscan.constant.SharedPreferenceKeys;
import com.kstechnologies.nanoscan.event.ActionGattConnectedEvent;
import com.kstechnologies.nanoscan.event.ActionGattDisconnectedEvent;
import com.kstechnologies.nanoscan.utils.FileUtil;
import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 自定义Application类
 *
 * @author crt106 on 2019/5/12.
 */
public class CApplication extends Application {

    public static Context ApplicationContext;
    public static String scanDataPath;

    //region SharedPreference

    private static SharedPreferences globalPref;

    //endregion

    //region 全局状态变量

    /**
     * 是否连接到Nano
     */
    public static boolean connected;

    /**
     * 当前被选中的扫描配置项
     */
    public static KSTNanoSDK.ScanConfiguration activeConf;

    //endregion


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        ApplicationContext = getApplicationContext();
        //初始化SharedPreference们
        globalPref = getSharedPreferences(SharedPreferenceKeys.GLOBAL_DATA, MODE_PRIVATE);
        //初始化其他数据
        scanDataPath = getExternalFilesDir(null).getAbsolutePath();
        releaseSampleData();
    }

    /**
     * 如果应用初次安装的时候没有将示例数据导出,则导出
     */
    private void releaseSampleData() {
        boolean isload = globalPref.getBoolean(SharedPreferenceKeys.SAMPLE_DATA_LOADED, false);
        if (!isload) {
            try {
                FileUtil.copyAssets("sampledata", scanDataPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            globalPref.edit().putBoolean(SharedPreferenceKeys.SAMPLE_DATA_LOADED, true).apply();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }


    //region EventBus接收

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveGattConnected(ActionGattConnectedEvent event) {
        connected = true;
        Toast.makeText(this, "Test:状态变为已连接", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveGattDisConnected(ActionGattDisconnectedEvent event) {
        connected = true;
        Toast.makeText(this, "Test:状态变为未连接", Toast.LENGTH_SHORT).show();
    }


    //endregion
}
