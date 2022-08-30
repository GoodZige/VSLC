package com.vslc.tools.array;

import com.vslc.dao.IInspectionDao;
import com.vslc.dao.ISequenceDao;
import com.vslc.model.DcmInfo;
import com.vslc.model.Inspection;
import com.vslc.model.Sequence;
import com.vslc.tools.FileUtil;
import com.vslc.tools.PathUtil;
import com.vslc.tools.SavePath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chenlele
 * 2018/6/7 13:17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class RestructureMaskTest {

    @Autowired
    private ISequenceDao sequenceDao;

    @Autowired
    private IInspectionDao inspectionDao;

    //@Test
    public void export() throws IOException {
        List<Sequence> sequenceList = sequenceDao.findByIsSketch(1);
        for (Sequence sequence : sequenceList) {
            StringBuilder src = new StringBuilder();
            src.append(PathUtil.getSeriesDir(sequence, SavePath.rootPath));
            File dcmDir = new File(src.toString() + "\\DCM");
            String maskPath = src.toString() + "\\MASK";
            String typeInfoPath = src.toString() + "\\info.xml";
            DcmInfo dcmInfo = null;
            for (File dcm : dcmDir.listFiles()) {
                dcmInfo = new DcmInfo(dcm, false);
                break;
            }
            StringBuilder inspeName = new StringBuilder();
            inspeName.append(dcmInfo.getEnglishName());
            inspeName.append("_");
            inspeName.append(dcmInfo.getCTNum());
            inspeName.append("_");
            inspeName.append(dcmInfo.getPatientID());
            StringBuilder des = new StringBuilder("D:\\Temp\\");
            des.append(inspeName);
            des.append("\\");
            des.append(sequence.getSequenceNum());
            FileUtil.copy(typeInfoPath, des.toString() + "\\info.xml");
            des.append("\\MASK");
            FileUtil.copy(maskPath, des.toString());
        }
    }

    //@Test
    public void getList() throws IOException {
        System.load(SavePath.opencvPath);
        List<Sequence> sequenceList = sequenceDao.findByIsSketch(1);
        int index = 0;
        for (Sequence sequence : sequenceList) {
            String binPath = PathUtil.getBinFile(sequence, SavePath.rootPath);
            File binDir = new File(binPath);
            if (binDir.exists()) {
                RestructureMask restructure = new RestructureMask();
                restructure.arraySingle(sequence);
                Inspection inspection = sequence.getInspection();
                inspection.setProcessID(5);
                inspectionDao.updateProcessID(inspection);
                System.out.println(++index);
            }
        }
    }
}