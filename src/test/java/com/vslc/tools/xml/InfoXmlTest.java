package com.vslc.tools.xml;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by chenlele
 * 2018/8/27 10:40
 */
public class InfoXmlTest {

    @Test
    public void ergodicInspe() {
        String path = "D:\\LungCancer\\ZHE_JIANG_CANCER_HOSPITAL\\1.2.840.113619.2.55.3.604677527.233.1412140532.815";
        File file = new File(path);
        InfoXml.ergodicInspe(file);
    }
}