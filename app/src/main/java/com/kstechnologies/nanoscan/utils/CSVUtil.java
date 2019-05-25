package com.kstechnologies.nanoscan.utils;

import com.kstechnologies.nanoscan.model.MeasurePoint;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV 工具类 负责csv文件的读写
 *
 * @author crt106 on 2019/5/12.
 */
public class CSVUtil {

    public enum CsvHeaders {
        WaveLength,
        Intensity,
        Absorbance,
        Reflectance
    }

    /**
     * 从csv文件中读取所有的测量点数据
     *
     * @return 返回的测量点列表
     */
    public static List<MeasurePoint> readMeasurePoints(String filename) throws IOException
    {
        List<MeasurePoint> measurePoints = new ArrayList<>();
        Reader reader = new FileReader(filename);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord r : records) {
            float waveLength = 0;
            float intensity = 0;
            float absorbance = 0;
            float reflectance = 0;
            try {
                waveLength = Float.parseFloat(r.get("Wavelength"));
                absorbance = Float.parseFloat(r.get("Absorbance"));
                reflectance = Float.parseFloat(r.get("Reflectance"));
                //这里示例数据里Intensity可能为空 放到最后
                intensity = Float.parseFloat(r.get("Intensity"));
            } catch (NumberFormatException e) {

            }
            measurePoints.add(new MeasurePoint(waveLength, intensity, absorbance, reflectance));
        }
        return measurePoints;
    }


    /**
     * 将测量点数据输出到csv文件内
     *
     * @param filename
     * @return
     */
    public static boolean writeMeasurePoints(String filename, List<MeasurePoint> beans) throws IOException
    {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                //此处出现IOException
            }
        }
        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(',').withHeader(CsvHeaders.class);
        Writer writer = new FileWriter(file);
        CSVPrinter csvPrinter = new CSVPrinter(writer, format);
        csvPrinter.printRecords(beans);
        writer.close();
        return true;
    }


}
