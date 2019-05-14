package com.kstechnologies.nanoscan.activity.graphactivity;

/**
 * view-model Graph中显示扫描信息的承载体
 * @author crt106 on 2019/5/14.
 */
public class GraphListItem {

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

    public GraphListItem(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
