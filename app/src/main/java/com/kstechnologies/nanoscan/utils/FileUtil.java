package com.kstechnologies.nanoscan.utils;

import android.content.res.AssetManager;
import android.util.Log;

import com.kstechnologies.nanoscan.CApplication;
import com.kstechnologies.nanoscan.model.DataFile;
import com.kstechnologies.nanoscan.model.MeasureDictionary;
import com.kstechnologies.nanoscan.model.MeasurePoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件处理工具类
 *
 * @author crt106 on 2019/5/13.
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * 先根遍历序递归删除文件夹
     *
     * @param dirFile 要被删除的文件或者目录
     * @return 删除成功返回true, 否则返回false
     */
    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }

        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        return dirFile.delete();
    }

    /**
     * 将Assets指定内容递归复制到外部存储指定路径
     *
     * @param assetDir
     * @param dir
     */
    public static void copyAssets(String assetDir, String dir) {
        AssetManager assetManager = CApplication.ApplicationContext.getResources().getAssets();
        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = assetManager.list(assetDir);
        } catch (IOException e1) {
            return;
        }

        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {

            // 创建文件夹
            if (!mWorkingPath.mkdirs()) {
                // 文件夹创建不成功时调用
            }
        }

        for (int i = 0; i < files.length; i++) {
            try {
                // 获得每个文件的名字
                String fileName = files[i];

                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyAssets(fileName, dir + fileName + "/");
                    } else {
                        copyAssets(assetDir + "/" + fileName, dir + "/" + fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }

                InputStream in = null;
                if (0 != assetDir.length()) {
                    in = assetManager.open(assetDir + "/" + fileName);
                } else {
                    in = assetManager.open(fileName);
                }
                OutputStream out = new FileOutputStream(outFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取所有可用的扫描数据文件信息
     *
     * @return
     */
    public static List<DataFile> getAvalibleData() {

        List<DataFile> dataFiles = new ArrayList<>();
        //获取所有的数据文件夹
        File[] scanDatas = new File(CApplication.scanDataPath).listFiles();
        for (File dir : scanDatas) {
            try {
                File[] data = dir.listFiles();
                //判断数据是否符合格式要求
                boolean ok = data.length == 2 && data[0].isFile() && data[1].isFile();
                if (ok) {
                    String dirName = dir.getName();
                    DataFile d = new DataFile(dirName, dir.getAbsolutePath());
                    dataFiles.add(d);
                }
            } catch (Exception e) {
                Log.w(TAG, "读取目录时出现异常" + e.toString());
                continue;
            }
        }
        return dataFiles;
    }

    /**
     * 输出Data文件到外部存储
     */
    public static void writeData(DataFile dataFile, List<MeasurePoint> measurePoints,
                                 MeasureDictionary measureDictionary) throws IOException
    {

        File dir = new File(dataFile.getFilePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        CSVUtil.writeMeasurePoints(dataFile.getCsvPath(), measurePoints);
        GsonUtil.writeDictToFile(dataFile.getJsonPath(), measureDictionary);
    }
}
