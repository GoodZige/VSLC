package com.vslc.tools.excel;

import com.vslc.dao.IExaminationDao;
import com.vslc.dao.IPatientDao;
import com.vslc.model.Examination;
import com.vslc.model.Patient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by chenlele
 * 2018/5/29 9:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class ExaminationExcelTest {

    @Autowired
    private IPatientDao patientDao;

    @Autowired
    private IExaminationDao pathologyDao;

    //@Test
    public void readExcel() {
        List<Map<String, Object>> infoList = ExaminationExcel.readExcel("D:/2012-2016肺癌(带P号).xls");
        for (Map<String, Object> info : infoList) {
            Examination examination = (Examination) info.get("examination");
            Patient patient = (Patient) info.get("patient");
            pathologyDao.add(examination);
            //patientDao.update(patient);
        }
    }
}