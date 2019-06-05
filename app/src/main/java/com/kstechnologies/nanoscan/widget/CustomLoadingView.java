package com.kstechnologies.nanoscan.widget;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.kstechnologies.nanoscan.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 自定义的一个小菊花显示吧 写的很烂
 *
 * @author crt106 on 2019/6/5.
 */
public class CustomLoadingView extends FrameLayout {

    private static final String TAG = "CustomLoadingView";
    private Context mContext;
    GifImageView gifImageView;

    public CustomLoadingView(Context context) {
        this(context, null, 0, 0);
    }

    public CustomLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public CustomLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView();
    }


    private void initView() {
        gifImageView = new GifImageView(mContext);
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.loading);
            int width = gifDrawable.getIntrinsicWidth();
            int height = gifDrawable.getIntrinsicHeight();
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            params.gravity = Gravity.CENTER;
            gifImageView.setLayoutParams(params);
            gifImageView.setImageDrawable(gifDrawable);
            addView(gifImageView);
            setBackgroundColor(Color.DKGRAY);
            getBackground().setAlpha(200);
            setVisibility(GONE);
        } catch (IOException e) {
            Log.e(TAG, "initView: ", e);
        }
    }

    /**
     * 显示时阻挡点击事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }


}
