package com.kstechnologies.nanoscan.activity.analyseactivity;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;

/**
 * @author crt106 on 2019/6/4.
 */
public class AnalyseActivityViewModel {

    /**
     * 计算处理时间
     */
    public ObservableField<String> calcTime = new ObservableField<>();

    /**
     * 曲线拟合阶数
     */
    public ObservableField<String> level = new ObservableField<>();

    public ObservableField<String> getCalcTime() {
        return calcTime;
    }

    public void setCalcTime(ObservableField<String> calcTime) {
        this.calcTime = calcTime;
    }

    public ObservableField<String> getLevel() {
        return level;
    }

    public void setLevel(ObservableField<String> level) {
        this.level = level;
    }
}
