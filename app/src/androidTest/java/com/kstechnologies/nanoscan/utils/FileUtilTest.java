package com.kstechnologies.nanoscan.utils;

import android.content.Context;

import androidx.test.runner.AndroidJUnit4;

import com.kstechnologies.nanoscan.CApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author crt106 on 2019/5/14.
 */
@RunWith(AndroidJUnit4.class)
public class FileUtilTest {


    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void copyAssets() {
        try {
            String target = CApplication.ApplicationContext.getExternalFilesDir(null).getAbsolutePath();
            FileUtil.copyAssets("sampledata", target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAvalibleData() {
        FileUtil.getAvalibleData();
    }
}