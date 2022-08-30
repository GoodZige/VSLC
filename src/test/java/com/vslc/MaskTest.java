package com.vslc;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by chenlele
 * 2018/8/4 18:00
 */
public class MaskTest {

    @Test
    public void mask() {
        String path = "D:\\LungCancer\\1ST_AFFIL_HOSP_ZJU\\86.571.1.1320102.20140514.70.976024.1\\2\\MASK\\nodule.bin";
        File maskFile = new File(path);
        try {
            RandomAccessFile raf = new RandomAccessFile(maskFile, "rw");
            byte[] img = new byte[512*512];
            raf.read(img);
            int index = 0;
            for (int y = 0;y < 512; y++) {
                for (int x = 0; x < 512; x++) {
                    if (img[index] == 1) img[index] = 0;
                    index++;
                }
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
