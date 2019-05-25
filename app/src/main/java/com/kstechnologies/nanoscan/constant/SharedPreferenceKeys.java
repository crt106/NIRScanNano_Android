package com.kstechnologies.nanoscan.constant;

/**
 * 存储SharedPreferrence中键数据的地方哦
 * 之前的作者将一部分keys和数据放在SDK中{@link com.kstechnologies.nirscannanolibrary.SettingsManager}
 * 所以这里是一些更多不涉及到扫描时配置的数据
 * @author crt106 on 2019/5/13.
 */
public class SharedPreferenceKeys {

    //region GLOBAL_DATA文件

    /**
     * GLOBAL_DATA文件名
     */
    public static final String GLOBAL_DATA = "global";

    /**
     * (Boolean)预置的光谱数据是否已经释放到存储空间中
     */
    public static final String SAMPLE_DATA_LOADED = "SAMPLE_DATA_LOADED";

    //endregion

}
