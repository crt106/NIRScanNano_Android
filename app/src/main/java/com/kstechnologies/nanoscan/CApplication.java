package com.kstechnologies.nanoscan;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kstechnologies.nanoscan.constant.SharedPreferenceKeys;
import com.kstechnologies.nanoscan.utils.FileUtil;


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


    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext = getApplicationContext();
        //初始化SharedPreference们
        globalPref = getSharedPreferences(SharedPreferenceKeys.GLOBAL_DATA, MODE_PRIVATE);
        //初始化其他数据
        scanDataPath=getExternalFilesDir(null).getAbsolutePath();
        releaseSampleData();
    }

    /**
     * 如果应用初次安装的时候没有将示例数据导出,则导出
     */
    private void releaseSampleData(){
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
}
