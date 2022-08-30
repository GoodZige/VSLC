package com.vslc.dao;

import com.vslc.model.Pathology;
import com.vslc.model.Patient;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chenlele
 * 2018/7/25 15:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class IPathologyDaoTest {

    @Autowired
    private IPathologyDao pathologyDao;

    @Test
    public void findByGrowthMode() {
        List<Pathology> pathologyList = pathologyDao.findByGrowthMode(5);
        write("D:\\病理代码小于5.xls", pathologyList);
    }

    public void write(String path, List<Pathology> pathologyList) {
        try {
            File file = new File(path);
            WritableWorkbook book;
            WritableSheet writeSheet;
            if (file.exists()) {
                Workbook work = Workbook.getWorkbook(file);
                book = Workbook.createWorkbook(file, work);
                writeSheet = book.getSheet(0);
                work.close();
            } else {
                book = Workbook.createWorkbook(file);
                writeSheet = book.createSheet("Sheet1", 0);
            }
            writeSheet.addCell(new Label(0, 0, "住院号"));
            writeSheet.addCell(new Label(1, 0, "病理代码"));

            int row = 1;
            for (Pathology pathology : pathologyList) {
                writeSheet.addCell(new Label(0, row, pathology.getAdmissionNum()));
                writeSheet.addCell(new Label(1, row, Integer.toString(pathology.getGrowthMode())));
                row++;
            }
            book.write();
            book.close();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}