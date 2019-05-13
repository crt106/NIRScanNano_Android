package com.kstechnologies.nanoscan.utils;

import com.kstechnologies.nanoscan.model.MeasurePoint;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * CSV 工具类
 *
 * @author crt106 on 2019/5/12.
 */
public class CSVUtil
{

    /**
     * 从csv文件中读取所有的测量点数据
     *
     * @return 返回的测量点列表
     */
    public static List<MeasurePoint> ReadMeasurePoints(String filename) throws FileNotFoundException
    {
        File csvFile = new File(filename);
        List<MeasurePoint> measurePoints = new CsvToBeanBuilder(new FileReader(csvFile))
                .withType(MeasurePoint.class).build().parse();
        return measurePoints;
    }


    /**
     * 将测量点数据输出到csv文件内
     *
     * @param filename
     * @return
     */
    public static boolean WriteMeasurePoints(String filename, List<MeasurePoint> beans) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException
    {

        Writer writer = new FileWriter(filename);
        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
        beanToCsv.write(beans);
        writer.close();
        return true;
    }
}
