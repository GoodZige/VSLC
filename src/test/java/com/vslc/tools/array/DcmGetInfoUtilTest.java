package com.vslc.tools.array;

import com.vslc.dao.*;
import com.vslc.model.*;
import com.vslc.tools.dicom.DcmGetInfoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-mybatis.xml"})
public class DcmGetInfoUtilTest {

    public static final String ZJUFAH = "ZJUFAH";      //浙一医院
    public static final String ZJUSRRS = "ZJUSRRS";    //邵逸夫医院
    public static final String ZJC = "ZJC";            //浙江省肿瘤医院
    public static final String ZJUH = "ZJUH";          //浙大校医院
    public static final String ZJUSAH = "ZJUSAH";      //浙二医院
    public static final String LKDS = "LKDS";          //天池数据
    public static final String LIDC = "LIDC-IDRI";     //LIDC-IDRI数据集

    private static final String hospPath = "D:\\LungCancer\\LIDC-IDRI";

    private static final String hospName = LIDC;

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

    @Test
    public void addByHosp() {
        DcmGetInfoUtil getInfoUtil = new DcmGetInfoUtil();
        List<DcmInfo> dcmInfoList = getInfoUtil.readByHosp(hospPath, hospName);
        for (DcmInfo dcmInfo : dcmInfoList) {
            addInfo(dcmInfo);
        }
        System.out.println("save --> Finished");
    }

    //@Test
    public void addByInspection() {
        String inspaPath = "";
        File inspeDir = new File(inspaPath);
        DcmGetInfoUtil getInfoUtil = new DcmGetInfoUtil();
        List<DcmInfo> dcmInfoList = getInfoUtil.ergodicInspection(inspeDir, hospName);
        for (DcmInfo dcmInfo : dcmInfoList) {
            addInfo(dcmInfo);
        }
        System.out.println("save --> Finished");
    }

    public void addInfo(DcmInfo dcmInfo) {
        addPatientInfo(dcmInfo);
        addInspectionInfo(dcmInfo);
        addSequenceInfo(dcmInfo);
    }

    private void addPatientInfo(DcmInfo dcmInfo) {
        Patient patient = new Patient();
        patient.setPatientID(dcmInfo.getPatientID());
        patient.setEnglishName(dcmInfo.getEnglishName());
        patient.setBirday(dcmInfo.getPatientBirthday());
        patient.setPatientSex(dcmInfo.getPatientSex());

        if(patientDao.findByPatientID(dcmInfo.getPatientID()) == null)
            patientDao.add(patient);
    }

    private void addInspectionInfo(DcmInfo dcmInfo) {
        Inspection inspection = new Inspection();
        inspection.setInspectionID(dcmInfo.getStudyId());
        inspection.setCTNumber(dcmInfo.getCTNum());
        inspection.setInspectTime(dcmInfo.getInspectTime());
        inspection.setSavePath(dcmInfo.getSavePath());
        Patient patient = new Patient();
        patient.setPatientID(dcmInfo.getPatientID());
        inspection.setPatient(patient);
        inspection.setUploader(1);

        Mode mode = modeDao.findByModeName(dcmInfo.getModality());
        if (mode != null)
            inspection.setMode(mode);

        Hospital hospital = hospitalDao.findByEnglishName(hospName);
        if (hospital != null)
            inspection.setHospital(hospital);

        if(inspectionDao.findByInspectionID(dcmInfo.getStudyId()) == null)
            inspectionDao.add(inspection);
    }

    private void addSequenceInfo(DcmInfo dcmInfo) {
        Sequence sequence = new Sequence();
        sequence.setSequenceName(dcmInfo.getSeriesName());
        sequence.setSequenceNum(dcmInfo.getSeriesNum());
        Inspection inspection = new Inspection();
        inspection.setInspectionID(dcmInfo.getStudyId());
        sequence.setInspection(inspection);
        sequence.setThickness(dcmInfo.getThickness());
        sequence.setFileNum(dcmInfo.getFileNum());
        sequence.setDcmPath(dcmInfo.getDcmPath());

        Map<String, Object> param = new HashMap<>();
        param.put("inspectionID", dcmInfo.getStudyId());
        param.put("sequenceNum", dcmInfo.getSeriesNum());
        if (sequenceDao.findOne(param) == null)
            sequenceDao.add(sequence);
    }
}