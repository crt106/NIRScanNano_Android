package com.kstechnologies.nanoscan.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity基类
 *
 * @author crt106 on 2019/5/12.
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 快速显示短Toast
     *
     * @param text
     */
    protected void showShortToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 快速显示长Toast
     *
     * @param text
     */
    protected void showLongToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

}
