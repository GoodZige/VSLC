package com.vslc.tools.array;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by chenlele
 * 2018/7/16 17:03
 */
public class ArrayLidcTest {

    private String srcPath = "D:\\LIDC位置不对";

    @Test
    public void main() {
        ArrayLidc lidc = new ArrayLidc();
        //lidc.arraySingle(new File("D:\\temp\\LIDC-IDRI-0031"));
        //lidc.maskSingle(new File("D:\\temp\\LIDC-IDRI-0031"));
        //lidc.arrayMultiple(srcPath);
        lidc.maskMultiple(srcPath);
        //lidc.systemSingle(new File("D:\\temp2\\LIDC-IDRI-0031"));
    }
}