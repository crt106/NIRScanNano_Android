package com.kstechnologies.nanoscan.utils;

import androidx.databinding.BindingConversion;
import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableFloat;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用于DataBinding中的数据转换过程
 *
 * @author crt106 on 2019/6/4.
 */
public class ConverterUtil {

    @BindingConversion
    public static String convertInt(int value) {
        return String.valueOf(value);
    }

    @BindingConversion
    public static String convertInt(ObservableInt value) {
        return String.valueOf(value.get());
    }

    @BindingConversion
    public static String convertLong(long value) {
        return String.valueOf(value);
    }

    @BindingConversion
    public static String convertLong(ObservableLong value) {
        return String.valueOf(value.get());
    }

    @BindingConversion
    public static String convertDouble(double value) {
        return String.valueOf(value);
    }

    @BindingConversion
    public static String convertDouble(ObservableDouble value) {
        return String.valueOf(value.get());
    }

    @BindingConversion
    public static String convertFloat(float value) {
        return String.valueOf(value);
    }

    @BindingConversion
    public static String convertFloat(ObservableFloat value) {
        return String.valueOf(value.get());
    }

    @BindingConversion
    public static String convertDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(date);
    }

}
