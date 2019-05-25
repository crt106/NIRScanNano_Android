package com.kstechnologies.nanoscan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 可以自由设定能否左右上下滑动的ViewPager
 *
 * @author crt106 on 2019/4/11.
 */
public class NoScrollViewPager extends ViewPager {

    /**
     * 能否滑动
     */
    private boolean canScroll = true;

    public NoScrollViewPager(@NonNull Context context) {
        this(context, null);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        canScroll = true;
    }

    /**
     * 设置能否左右滑动
     *
     * @param canScroll
     */
    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return canScroll && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canScroll && super.onInterceptTouchEvent(ev);
    }
}
