package com.vslc.tools.excel;

import com.vslc.dao.IPathologyDao;
import com.vslc.dao.IPatientDao;
import com.vslc.model.Pathology;
import com.vslc.model.Patient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by chenlele
 * 2018/5/29 9:31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class PathologyExcelTest {

    @Autowired
    private IPathologyDao pathologyDao;

    @Autowired
    private IPatientDao patientDao;

    @Test
    public void readExcel() {
        List<Pathology> pathologyList = PathologyExcel.readExcel("D:\\肺病理与影像汇总2012-2016前五种病理类型.xls");
        Map<String, String> temp = new HashMap<>();
        int index = 0;
        for (Pathology pathology : pathologyList) {
            String admissionNum = pathology.getAdmissionNum();
            Patient patient = patientDao.findByAdmissionNum(admissionNum);
            if (patient != null) {
                if (temp.get(admissionNum) == null) {
                    temp.put(admissionNum, admissionNum);
                    index++;
                }
            }
        }
        System.out.println(index);
    }
}