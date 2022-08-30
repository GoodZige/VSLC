package com.vslc.tools.array;

import com.vslc.dao.*;
import com.vslc.model.*;
import com.vslc.tools.IniUtil;
import com.vslc.tools.SavePath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenlele
 * 2018/4/27 15:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class ArrMaskHandlerTest {

    private String hospName;

    private Patient patient;

    private Inspection inspection;

    private Sequence sequence;

    @Autowired
    private IInspectionDao inspectionDao;

    @Autowired
    private IPatientDao patientDao;

    @Autowired
    private ISequenceDao sequenceDao;

    @Autowired
    private IHospitalDao hospitalDao;

    @Autowired
    private IModeDao modeDao;

    //@Test
    public void arrMany() {
        ArrMaskHandler arrMask = new ArrMaskHandler();
        List<DcmInfo> dcmInfoList = arrMask.arrMany("D:\\mask\\20180312_杨已审核数据\\方杰审核", "D:\\LungCancer");
        for (DcmInfo dcmInfo : dcmInfoList) {
            Hospital hospital = IniUtil.getHospital(dcmInfo.getHospitalName());
            hospName = hospital.getHospitalShortName();
            if (hospName == null) {
                System.out.println("医院未知");
                break;
            }
            addInfo(dcmInfo);
        }
        System.out.println("save --> Finished");
    }

    //@Test
    public void arrSingle() {
        ArrMaskHandler arrMask = new ArrMaskHandler();
        DcmInfo dcmInfo = arrMask.arrSingle("", "D:/test");
        Hospital hospital = IniUtil.getHospital(dcmInfo.getHospitalName());
        hospName = hospital.getHospitalShortName();
        if (hospName != null) {
            addInfo(dcmInfo);
            System.out.println("save --> Finished");
        } else {
            System.out.println("医院未知");
        }
    }

    private void addInfo(DcmInfo dcmInfo) {
        if (patientDao.findByPatientID(dcmInfo.getPatientID()) == null)
            addPatientInfo(dcmInfo);
        if (inspectionDao.findByInspectionID(dcmInfo.getStudyId()) == null)
            addInspectionInfo(dcmInfo);

        Map<String, Object> param = new HashMap<>();
        param.put("inspectionID", dcmInfo.getStudyId());
        param.put("sequenceNum", dcmInfo.getSeriesNum());
        if (sequenceDao.findOne(param) == null)
            addSequenceInfo(dcmInfo);
        else {
            sequence = sequenceDao.findOne(param);
            sequence.setFileNum(dcmInfo.getFileNum());
            if (haveSketch(SavePath.rootPath +
                    dcmInfo.getDcmPath().substring(0,
                            dcmInfo.getDcmPath().lastIndexOf("\\") - 3)
                    + "BIN")) {
                sequence.setIsSketch(1);
                Inspection upInspe = new Inspection();
                upInspe.setInspectionID(sequence.getInspection().getInspectionID());
                upInspe.setProcessID(2);
            }
            else sequence.setIsSketch(0);
            sequenceDao.update(sequence);
        }
    }

    private void addPatientInfo(DcmInfo dcmInfo) {
        patient = new Patient();
        patient.setPatientID(dcmInfo.getPatientID());
        patient.setEnglishName(dcmInfo.getEnglishName());
        patient.setBirday(dcmInfo.getPatientBirthday());
        patient.setPatientSex(dcmInfo.getPatientSex());

        if(patientDao.findByPatientID(dcmInfo.getPatientID()) == null) {
            patientDao.add(patient);
        }
    }

    private void addInspectionInfo(DcmInfo dcmInfo) {
        inspection = new Inspection();
        inspection.setInspectionID(dcmInfo.getStudyId());
        inspection.setCTNumber(dcmInfo.getCTNum());
        inspection.setInspectTime(dcmInfo.getInspectTime());
        inspection.setSavePath(dcmInfo.getSavePath());
        inspection.setPatient(patient);
        inspection.setUploader(1);
        Mode mode = modeDao.findByModeName(dcmInfo.getModality());
        if (mode != null) {
            inspection.setMode(mode);
        }
        Hospital hospital = hospitalDao.findByEnglishName(hospName);
        if (hospital != null) {
            inspection.setHospital(hospital);
        }

        if(inspectionDao.findByInspectionID(dcmInfo.getStudyId()) == null) {
            inspectionDao.add(inspection);
        }
    }

    private void addSequenceInfo(DcmInfo dcmInfo) {
        sequence = new Sequence();
        sequence.setSequenceName(dcmInfo.getSeriesName());
        sequence.setSequenceNum(dcmInfo.getSeriesNum());
        sequence.setInspection(inspection);
        sequence.setThickness(dcmInfo.getThickness());
        sequence.setFileNum(dcmInfo.getFileNum());
        sequence.setDcmPath(dcmInfo.getDcmPath());
        if (haveSketch(SavePath.rootPath +
                dcmInfo.getDcmPath().substring(0,
                        dcmInfo.getDcmPath().lastIndexOf("\\") - 3)
                + "BIN")) {
            sequence.setIsSketch(1);
            Inspection upInspe = new Inspection();
            upInspe.setInspectionID(sequence.getInspection().getInspectionID());
            upInspe.setProcessID(2);
        }
        sequenceDao.add(sequence);
    }

    private static boolean haveSketch(String binPath) {
        File binDir = new File(binPath);
        File[] files = binDir.listFiles();
        if (files.length == 0) return false;
        else return true;
    }
}