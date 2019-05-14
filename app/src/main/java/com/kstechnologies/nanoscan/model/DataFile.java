package com.kstechnologies.nanoscan.model;

import java.io.Serializable;

/**
 * 记录一组扫描数据文件位置的model
 *
 * @author crt106 on 2019/5/13.
 */
public class DataFile implements Serializable {

    /**
     * data单独的文件名 例如aspirin
     */
    private String fileName;

    /**
     * data所在的路径名
     */
    private String filePath;

    /**
     * 获得数据的csv文件路径
     *
     * @return
     */
    public String getCsvPath() {
        return filePath + '/' + fileName + ".csv";
    }

    /**
     * 获得数据的json文件路径
     *
     * @return
     */
    public String getJsonPath() {
        return filePath + '/' + fileName + ".json";
    }

    public DataFile(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "DataFile{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
