package com.vslc.tools.excel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;

/**
 * Created by chenlele
 * 2018/6/2 11:16
 */
public class NoduleExcelTest {

    @Test
    public void readExcel() throws IOException {
        File file = new File("D:/MASK-2_OBJECT1.xlsx");
        InputStream is = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        //工作簿1
        for (int numSheet = 0; numSheet < 1; numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    XSSFCell one = xssfRow.getCell(0);
                    XSSFCell two = xssfRow.getCell(1);

                    String o = one.toString();
                    String t = two.toString();
                    double d = Double.valueOf(t);
                    System.out.println(o + d);
                }
            }
        }
    }
}
