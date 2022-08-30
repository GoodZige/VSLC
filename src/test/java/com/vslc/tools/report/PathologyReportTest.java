package com.vslc.tools.report;

import com.vslc.dao.IPathologyDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by chenlele
 * 2018/5/26 11:56
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class PathologyReportTest {

    @Autowired
    private IPathologyDao pathologyDao;

    @Test
    public void getReport() {

    }
}