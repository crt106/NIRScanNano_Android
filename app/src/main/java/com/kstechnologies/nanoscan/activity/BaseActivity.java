package com.kstechnologies.nanoscan.activity;

import android.app.Activity;
import android.widget.Toast;

/**
 * Activity基类
 *
 * @author crt106 on 2019/5/12.
 */
public class BaseActivity extends Activity {


    /**
     * 快速显示短Toast
     * @param text
     */
    protected void showShortToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 快速显示长Toast
     * @param text
     */
    protected void showLongToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
