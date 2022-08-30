package com.vslc.tools.report;

import com.vslc.dao.IExaminationDao;
import com.vslc.model.Examination;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class ExaminationReportTest {

    @Autowired
    private IExaminationDao examinationDao;

    //@Test
    public void getReport() {
        Map<String, Object> param = new HashMap<>();
        Examination examination = examinationDao.findByExaminationID(792);
        param.put("patient", examination.getPatient());
        param.put("examination", examination);
        param.put("format", 1);
        ExaminationReport.getReport(param);
    }
}