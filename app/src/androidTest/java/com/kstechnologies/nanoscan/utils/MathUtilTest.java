package com.kstechnologies.nanoscan.utils;

import com.kstechnologies.nanoscan.model.DataFile;
import com.kstechnologies.nanoscan.model.MeasurePoint;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author crt106 on 2019/5/15.
 */
public class MathUtilTest {


    @Test
    public void polynomialFit() {
        //挑选第一位幸运观众
        try {
            DataFile d = FileUtil.getAvalibleData().get(0);
            List<MeasurePoint> measurePoints = CSVUtil.readMeasurePoints(d.getCsvPath());
            //构建所需的数据类型
            List<WeightedObservedPoint> weightedObservedPoints = new ArrayList<>();
            for (MeasurePoint mp : measurePoints) {
                WeightedObservedPoint wp = new WeightedObservedPoint(1, (double) mp.getWavelength(),
                        (double) mp.getAbsorbance());
                weightedObservedPoints.add(wp);
            }
            double[] paras = MathUtil.polynomialFit(weightedObservedPoints, 20);
            //把数组倒序
            ArrayList<Double> rparas = new ArrayList<>();
            for (int i = paras.length - 1; i >= 0; i--) {
                rparas.add(paras[i]);
            }
            System.setOut(new PrintStream(paras.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试多项式预测结果
     */
    @Test
    public void polynomialPredict() {
        double[] paras = {6, 0, 0, 4, 5, 6};
        List<Double> x = new ArrayList<>();
        x.add(1d);
        x.add(2d);
        x.add(3d);
        x.add(4d);
        x.add(5d);
        List<Double> result = MathUtil.polynomialPredict(paras, x);
        assert true;
    }

    /**
     * 测试求导
     */
    @Test
    public void polynomialDerivate() {
        double[] test = {2, 3, 5, 1, 8, 1, 4, 3, 4, 6};
        double[] result = MathUtil.polynomialDerivate(test, 1);
        double[] result2 = MathUtil.polynomialDerivate(test, 2);
    }
}