package com.vslc.tools;

import com.vslc.model.Sequence;

/**
 * Created by chenlele
 * 2018/7/5 10:05
 */
public class PathUtil {

    //通过数据库查询到序列对象获取序列文件夹
    public static String getSeriesDir(Sequence sequence, String rootPath) {
        StringBuilder sb = new StringBuilder();
        if (rootPath != null) sb.append(rootPath);
        sb.append(sequence.getInspection().getSavePath());
        sb.append("\\");
        sb.append(sequence.getSequenceNum());
        return sb.toString();
    }

    //序列下mask文件夹
    public static String getMaskFile(Sequence sequence, String rootPath, String fileName) {
        StringBuilder sb = new StringBuilder();
        if (rootPath != null) sb.append(rootPath);
        sb.append(getSeriesDir(sequence, null));
        sb.append("\\MASK");
        if (fileName != null) {
            sb.append("\\");
            sb.append(fileName);
        }
        return sb.toString();
    }

    //序列下原始图像文件夹
    public static String getDcmFile(Sequence sequence, String rootPath, String fileName) {
        StringBuilder sb = new StringBuilder();
        if (rootPath != null) sb.append(rootPath);
        sb.append(getSeriesDir(sequence, null));
        sb.append("\\DCM");
        if (fileName != null) {
            sb.append("\\");
            sb.append(fileName);
        }
        return sb.toString();
    }

    //旧标注结果文件夹 用处不大
    public static String getBinFile(Sequence sequence, String rootPath) {
        StringBuilder sb = new StringBuilder();
        if (rootPath != null) sb.append(rootPath);
        sb.append(getSeriesDir(sequence, null));
        sb.append("\\BIN");
        return sb.toString();
    }

    //序列下类型级别xml文件路径
    public static String getTypeXmlPath(Sequence sequence, String rootPath) {
        StringBuilder sb = new StringBuilder();
        if (rootPath != null) sb.append(rootPath);
        sb.append(getSeriesDir(sequence, null));
        sb.append("\\info.xml");
        return sb.toString();
    }
}
