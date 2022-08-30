package com.vslc.tools.sketch;

import com.vslc.dao.ISequenceDao;
import com.vslc.model.Sequence;
import com.vslc.tools.SketchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created by chenlele
 * 2018/6/25 10:09
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class SketchResultTest {

    @Autowired
    private ISequenceDao sequenceDao;

    @Test
    public void getNoduleInfo() {
        Sequence sequence = sequenceDao.findBySequenceID(51);
        byte sketchNum = 1;
        Map<String, Object> noduleInfo = SketchResult.D3NoduleInfo(sequence, sketchNum);
        System.out.println("position: " + noduleInfo.get("position"));
        System.out.println("volume: " + noduleInfo.get("volume"));
        System.out.println("diameter: " + noduleInfo.get("diameter"));
        System.out.println("avgCT: " + noduleInfo.get("avgCT"));
    }
}