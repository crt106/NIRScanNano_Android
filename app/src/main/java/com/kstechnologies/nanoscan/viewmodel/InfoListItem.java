package com.kstechnologies.nanoscan.viewmodel;


import com.kstechnologies.nanoscan.activity.newscanactivity.NewScanActivity;

/**
 * view-model 中显示扫描信息和分析信息等的承载体
 * @see com.kstechnologies.nanoscan.activity.analyseactivity.AnalyseActivity
 * @see com.kstechnologies.nanoscan.activity.graphactivity.GraphActivity
 * @see NewScanActivity
 * @author crt106 on 2019/5/14.
 */
public class InfoListItem extends BaseViewModel {

    /**
     * 项目标题
     */
    private String title;
    /**
     * 项目内容
     */
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public InfoListItem(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
