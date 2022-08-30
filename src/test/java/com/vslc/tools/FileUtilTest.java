package com.vslc.tools;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by chenlele
 * 2018/8/26 20:38
 */
public class FileUtilTest {

    @Test
    public void getSize() {
        File file = new File("D:\\肺勾画");
        double size = FileUtil.getSizeGB(file);
        System.out.println(size);
    }

    @Test
    public void copy() {
        String srcPath = "D:\\test\\vpn";
        String desPath = "D:\\test\\des";
        FileUtil.copy(srcPath, desPath);
    }

    @Test
    public void delete() {
        File dir = new File("D:\\test\\vpn");
        FileUtil.delete(dir);
    }
}