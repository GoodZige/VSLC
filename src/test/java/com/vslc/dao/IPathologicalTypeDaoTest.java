package com.vslc.dao;

import com.vslc.model.PathologicalType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chenlele
 * 2018/7/24 10:18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class IPathologicalTypeDaoTest {

    @Autowired
    private IPathologicalTypeDao pathologicalTypeDao;

    @Test
    public void find() {
        List<PathologicalType> pathologicalTypeList = pathologicalTypeDao.find();
//        for (PathologicalType pathologicalType : pathologicalTypeList) {
//            System.out.println(pathologicalType.getPathologicalTypeName());
//        }
    }
}