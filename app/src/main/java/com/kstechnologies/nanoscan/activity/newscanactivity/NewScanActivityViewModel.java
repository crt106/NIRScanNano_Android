package com.kstechnologies.nanoscan.activity.newscanactivity;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.kstechnologies.nanoscan.viewmodel.BaseViewModel;

/**
 * ViewModel for {@link NewScanActivity}
 *
 * @author crt106 on 2019/5/21.
 */
public class NewScanActivityViewModel extends BaseViewModel {

    /**
     * 文件名前缀
     * 其为public的原因是方便在代码中设置值
     * 而下述getter setter方法是用于DataBinding 其他字段同理
     */
    public ObservableField<String> fileNamePrefix = new ObservableField<>();

    /**
     * 是否保存到仪器SD卡
     */
    public ObservableBoolean save2SDCard = new ObservableBoolean();

    /**
     * 是否保存到手机
     */
    public ObservableBoolean save2Phone = new ObservableBoolean();

    /**
     * 是否连续扫描
     */
    public ObservableBoolean continueScan = new ObservableBoolean();

    /**
     * 扫描按钮的文字显示
     */
    public ObservableField<String> scanBtnText = new ObservableField<>();

    /**
     * 扫描配置(column等)
     */
    public ObservableField<String> scanConfig = new ObservableField<>();


    public ObservableField<String> getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(ObservableField<String> fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public ObservableBoolean getSave2SDCard() {
        return save2SDCard;
    }

    public void setSave2SDCard(ObservableBoolean save2SDCard) {
        this.save2SDCard = save2SDCard;
    }

    public ObservableBoolean getSave2Phone() {
        return save2Phone;
    }

    public void setSave2Phone(ObservableBoolean save2Phone) {
        this.save2Phone = save2Phone;
    }

    public ObservableBoolean getContinueScan() {
        return continueScan;
    }

    public void setContinueScan(ObservableBoolean continueScan) {
        this.continueScan = continueScan;
    }

    public ObservableField<String> getScanBtnText() {
        return scanBtnText;
    }

    public void setScanBtnText(ObservableField<String> scanBtnText) {
        this.scanBtnText = scanBtnText;
    }

    public ObservableField<String> getScanConfig() {
        return scanConfig;
    }

    public void setScanConfig(ObservableField<String> scanConfig) {
        this.scanConfig = scanConfig;
    }
}
