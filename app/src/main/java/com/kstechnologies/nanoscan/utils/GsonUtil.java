package com.kstechnologies.nanoscan.utils;

import com.google.gson.Gson;
import com.kstechnologies.nanoscan.model.MeasureDictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Gson处理工具类
 *
 * @author crt106 on 2019/5/13.
 */
public class GsonUtil {

    /**
     * 从json文件中读取扫描信息
     *
     * @return
     */
    public static MeasureDictionary ReadDictFromFile(String file) throws IOException {
        // 从sd卡中读取json文件并Gson解析
        File read = new File(file);
        // 读取文件

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)));

        // 创建StringBuffer
        StringBuffer stringBuffer = new StringBuffer();
        String temp = "";

        // 一行一行的读
        while ((temp = bufferedReader.readLine()) != null) {
            stringBuffer.append(temp);
        }

        String presonsString = stringBuffer.toString();
        // 解析,创建Gson,需要导入gson的jar包
        Gson gson = new Gson();
        MeasureDictionary md = gson.fromJson(presonsString, MeasureDictionary.class);
        return md;
    }
}

