package com.kstechnologies.nanoscan.utils;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * 数学分析综合工具类
 *
 * @author crt106 on 2019/5/15.
 */
public class MathUtil {

    /**
     * 多项式拟合方式
     *
     * @param points 待拟合的离散点
     * @param degree 多项式次数
     * @return 返回的多项式函数的系数[从0次到n次]
     */
    public static double[] polynomialFit(List<WeightedObservedPoint> points, int degree) {
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
        return fitter.fit(points);
    }

    /**
     * 根据已经求得的多项式系数进行多项式求值
     *
     * @param paras   多项式系数[从0次到n次]
     * @param xValues x值集合
     * @return
     */
    public static List<Double> polynomialPredict(double[] paras, List<Double> xValues) {
        //判断多项式次数 (level-1)
        double level = paras.length;
        List<Double> results = new ArrayList<>();
        for (double x : xValues) {
            double resultone = 0;
            for (int i = 0; i < level; i++) {
                resultone += Math.pow(x, i) * paras[i];
            }
            results.add(resultone);
        }
        return results;
    }

    /**
     * 多项式系数求导
     *
     * @param paras 多项式系数[从0次到n次]
     * @param level 求导阶数
     * @return
     */
    public static double[] polynomialDerivate(double[] paras, int level) {
        int len = paras.length;
        if (len <= 1 || level <= 0 || level >= len) {
            return new double[]{0};
        } else {
            //进行一下数组反转 之前之所以从0次到n次是适应commons math的方式
            double[] rpara = reserveArray(paras);
            //构建求导后的系数
            int maxlevel = len - 1;
            double[] result = new double[len - level];
            for (int i = 0; i < result.length; i++) {
                double pre = rpara[i];
                double after = pre;
                for (int j = 0; j < level; j++) {
                    after *= maxlevel - j;
                }
                result[i] = after;
                maxlevel--;
            }
            return reserveArray(result);
        }
    }

    /**
     * 计算Brix值
     *
     * @return
     */
    public static double getBrix(double p1, double p2, double p3, double p4) {
        return 10.922 + 2230.823 * p1 + 3044.681 * p2 - 623.687 * p3 - 55.074 * p4;
    }

    /**
     * 颠倒数组 返回新数组
     *
     * @param source
     * @return
     */
    private static double[] reserveArray(double[] source) {
        int len = source.length;
        double[] rarray = new double[len];
        for (int i = len - 1; i >= 0; i--) {
            rarray[len - 1 - i] = source[i];
        }
        return rarray;
    }

}
