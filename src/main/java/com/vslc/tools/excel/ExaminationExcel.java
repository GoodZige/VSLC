package com.vslc.tools.excel;

import com.vslc.model.Examination;
import com.vslc.model.Patient;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检验报告读取（已整理入库 没用）
 * Created by chenlele
 * 2018/5/29 8:27
 */
public class ExaminationExcel {

    /**
     * jxl工具包 不能读取xlsx文件
     * @param path
     */
    public static List<Map<String, Object>> readExcel(String path) {
        List<Map<String, Object>> infoList = new ArrayList<>();
        try {
            File file = new File(path);
            Workbook work = Workbook.getWorkbook(file);
            //每个页签创建一个Sheet对象
            Sheet sheet = work.getSheet(0);
            //r=1第二行开始
            for (int r = 1; r < sheet.getRows(); r++) {
                Examination examination = new Examination();
                Patient patient = new Patient();

                examination.setAdmissionNum(sheet.getCell(0, r).getContents());
                patient.setAdmissionNum(sheet.getCell(0, r).getContents());
                patient.setChineseName(sheet.getCell(2, r).getContents());
                patient.setPatientID(sheet.getCell(4, r).getContents());
                examination.setPatient(patient);
                patient.setIDNumber(sheet.getCell(7, r).getContents());
                patient.setNativePlace(sheet.getCell(8, r).getContents());
                patient.setContacts(sheet.getCell(9, r).getContents());
                patient.setTel(sheet.getCell(10, r).getContents());
                patient.setAdmissionDate(ExcelUtil.parseTime(sheet.getCell(11, r).getContents()));
                patient.setDischargeDate(ExcelUtil.parseTime(sheet.getCell(12, r).getContents()));
                patient.setSurgeon(sheet.getCell(13, r).getContents());
                patient.setDischargeDiagnosis(sheet.getCell(14, r).getContents());
                patient.setOperationDate(ExcelUtil.parseTime(sheet.getCell(15, r).getContents()));
                patient.setOperationName(sheet.getCell(16, r).getContents());
                examination.setAPTT(ExcelUtil.toFloat(sheet.getCell(17, r).getContents()));
                examination.setTT(ExcelUtil.toFloat(sheet.getCell(18, r).getContents()));
                examination.setPT(ExcelUtil.toFloat(sheet.getCell(19, r).getContents()));
                examination.setINR(ExcelUtil.toFloat(sheet.getCell(20, r).getContents()));
                examination.setD_D(ExcelUtil.toFloat(sheet.getCell(21, r).getContents()));
                examination.setTP(ExcelUtil.toFloat(sheet.getCell(22, r).getContents()));
                examination.setGLO(ExcelUtil.toFloat(sheet.getCell(23, r).getContents()));
                examination.setALB(ExcelUtil.toFloat(sheet.getCell(24, r).getContents()));
                examination.setAG(ExcelUtil.toFloat(sheet.getCell(25, r).getContents()));
                examination.setALT(ExcelUtil.toInteger(sheet.getCell(26, r).getContents()));
                examination.setCRP(ExcelUtil.toFloat(sheet.getCell(27, r).getContents()));
                examination.setBuN(ExcelUtil.toFloat(sheet.getCell(28, r).getContents()));
                examination.setCr(ExcelUtil.toFloat(sheet.getCell(29, r).getContents()));
                examination.setUA(ExcelUtil.toInteger(sheet.getCell(30, r).getContents()));
                examination.setTG(ExcelUtil.toFloat(sheet.getCell(31, r).getContents()));
                examination.setCH(ExcelUtil.toFloat(sheet.getCell(32, r).getContents()));
                examination.setHDL(ExcelUtil.toFloat(sheet.getCell(33, r).getContents()));
                examination.setLDH(ExcelUtil.toInteger(sheet.getCell(34, r).getContents()));
                examination.setALP(ExcelUtil.toInteger(sheet.getCell(35, r).getContents()));
                examination.setY_GT(ExcelUtil.toInteger(sheet.getCell(36, r).getContents()));
                examination.setCHE(ExcelUtil.toInteger(sheet.getCell(37, r).getContents()));
                examination.setADA(ExcelUtil.toFloat(sheet.getCell(38, r).getContents()));
                examination.setCA724(ExcelUtil.toFloat(sheet.getCell(39, r).getContents()));
                examination.setCA242(ExcelUtil.toFloat(sheet.getCell(40, r).getContents()));
                examination.setCYF211(ExcelUtil.toFloat(sheet.getCell(41, r).getContents()));
                examination.setNSE(ExcelUtil.toFloat(sheet.getCell(42, r).getContents()));
                examination.setAFP(ExcelUtil.toFloat(sheet.getCell(43, r).getContents()));
                if (sheet.getCell(44, r).getContents().equals("< 0.50")) {
                    examination.setCEA(0f);
                } else {
                    examination.setCEA(ExcelUtil.toFloat(sheet.getCell(44, r).getContents()));
                }
                examination.setCA125(ExcelUtil.toFloat(sheet.getCell(45, r).getContents()));
                examination.setCA153(ExcelUtil.toFloat(sheet.getCell(46, r).getContents()));
                if (sheet.getCell(47, r).getContents().equals("< 2.00")) {
                    examination.setCA199(0f);
                } else if (sheet.getCell(47, r).getContents().equals("> 12000.00")) {
                    examination.setCA199(12000.00f);
                } else {
                    examination.setCA199(ExcelUtil.toFloat(sheet.getCell(47, r).getContents()));
                }
                if (sheet.getCell(48, r).getContents().equals("> 70.0")) {
                    examination.setSCC(70f);
                } else {
                    examination.setSCC(ExcelUtil.toFloat(sheet.getCell(48, r).getContents()));
                }

                Map<String, Object> info = new HashMap<>();
                info.put("pathology", examination);
                info.put("patient", patient);
                infoList.add(info);
                work.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return infoList;
    }
}
