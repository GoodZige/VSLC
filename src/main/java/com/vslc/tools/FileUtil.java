package com.vslc.tools;

import java.io.*;

/**
 * Created by chenlele
 * 2018/8/26 20:30
 */
public class FileUtil {

    public static void copy(String srcPath, String desPath) {
        File src = new File(srcPath);
        if (src.exists()) {
            if (src.isDirectory()) {
                copyDirectory(srcPath, desPath);
            } else {
                copyFile(srcPath, desPath);
            }
        }
    }

    public static boolean delete(File files) {
        if (files.exists()) {
            if (files.isDirectory()) {
                for (File file : files.listFiles()) {
                    if (!delete(file)) return false;
                }
            } else {
                return files.delete();
            }
            return files.delete();
        } else {
            return false;
        }
    }

    public static double getSizeGB(File file) {
        double length = getLength(file);
        double size = length / 1024 / 1024 / 1024;
        double result = (double) Math.round(size*100)/100;
        return result;
    }

    public static double getSizeMB(File file) {
        double length = getLength(file);
        double size = length / 1024 / 1024;
        double result = (double) Math.round(size*100)/100;
        return result;
    }

    public static double getSizeKB(File file) {
        double length = getLength(file);
        double size = length / 1024;
        double result = (double) Math.round(size*100)/100;
        return result;
    }

    public static double getLength(File file) {
        if (file.exists()) {
            double size = 0;
            if (file.isDirectory()) {
                for (File children : file.listFiles()) {
                    size += getLength(children);
                }
            } else {
                size = (double) file.length();
            }
            return size;
        } else {
            return 0;
        }
    }

    private static void copyDirectory(String srcPath, String desPath) {
        File srcDir = new File(srcPath);
        File desDir = new File(desPath);
        if (!desDir.exists()) desDir.mkdirs();
        for (File file : srcDir.listFiles()) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                String src = file.getAbsolutePath();
                String des = desPath + File.separator + fileName;
                copyDirectory(src, des);
            } else {
                String src = file.getAbsolutePath();
                String des = desPath + File.separator + fileName;
                copyFile(src, des);
            }
        }
    }

    private static void copyFile(String srcPath, String desPath) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcPath));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(desPath));
            int len; //记录长度
            byte[] bytes = new byte[1024]; //缓冲区
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
                bos.flush();
            }
            //刷新此缓冲的输出流，保证数据全部都能写出
            bos.flush();
            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
