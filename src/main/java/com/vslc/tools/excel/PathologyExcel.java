package com.vslc.tools.excel;

import com.vslc.model.PathologicalType;
import com.vslc.model.Pathology;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 病理报告读取（已整理入库 没用）
 * Created by chenlele
 * 2018/5/29 8:27
 */
public class PathologyExcel {

    /**
     * jxl工具包 不能读取xlsx文件
     * @param path
     */
    public static List<Pathology> readExcel(String path) {
        List<Pathology> pathologyList = new ArrayList<>();
        try {
            File file = new File(path);
            Workbook work = Workbook.getWorkbook(file);
            //Excel的页签数量
            int sheetSize = work.getNumberOfSheets();
            //获取第一个页签
            Sheet sheet = work.getSheet(0);
            //r=2第3行开始
            for (int r = 2; r < sheet.getRows(); r++) {
                Pathology pathology = new Pathology();
                pathology.setAdmissionNum(sheet.getCell(1, r).getContents());
                PathologicalType pathologicalType = new PathologicalType();
                pathologicalType.setPathologicalTypeID(ExcelUtil.toInteger(sheet.getCell(3, r).getContents()));
                pathology.setPathologicalType(pathologicalType);
                pathology.setIsCIS(get123(sheet.getCell(4, r).getContents()));
                pathology.setGrowthMode(ExcelUtil.toInteger(sheet.getCell(5, r).getContents()));
                pathology.setPosition(ExcelUtil.getValue(sheet.getCell(6, r).getContents()));
                pathology.setSize(ExcelUtil.getValue(sheet.getCell(7, r).getContents().replaceAll("�", "*")));
                pathology.setVTS(get01(sheet.getCell(8, r).getContents()));
                pathology.setLymphonodus2(ExcelUtil.getValue(sheet.getCell(9, r).getContents()));
                pathology.setLymphonodus3(ExcelUtil.getValue(sheet.getCell(10, r).getContents()));
                pathology.setLymphonodus4(ExcelUtil.getValue(sheet.getCell(11, r).getContents()));
                pathology.setLymphonodus5(ExcelUtil.getValue(sheet.getCell(12, r).getContents()));
                pathology.setLymphonodus6(ExcelUtil.getValue(sheet.getCell(13, r).getContents()));
                pathology.setLymphonodus7(ExcelUtil.getValue(sheet.getCell(14, r).getContents()));
                pathology.setLymphonodus8(ExcelUtil.getValue(sheet.getCell(15, r).getContents()));
                pathology.setLymphonodus9(ExcelUtil.getValue(sheet.getCell(16, r).getContents()));
                pathology.setLymphonodus10(ExcelUtil.getValue(sheet.getCell(17, r).getContents()));
                pathology.setLymphonodus11(ExcelUtil.getValue(sheet.getCell(18, r).getContents()));
                pathology.setLymphonodus12(ExcelUtil.getValue(sheet.getCell(19, r).getContents()));

                pathologyList.add(pathology);
            }
            work.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return pathologyList;
    }

    private static Integer get123(String value) {
        if (value.equals("")) {
            return null;
        } else {
            if (ExcelUtil.isInteger(value)) return Integer.valueOf(value);
            else return 3;
        }
    }

    private static Integer get01(String value) {
        if (value.equals("")) return 0;
        else {
            if (ExcelUtil.isInteger(value)) return Integer.valueOf(value);
            else return 0;
        }
    }
}
