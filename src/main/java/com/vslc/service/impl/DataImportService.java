package com.vslc.service.impl;

import com.vslc.dao.*;
import com.vslc.model.*;
import com.vslc.service.IDataImportService;
import com.vslc.tools.IniUtil;
import com.vslc.tools.SavePath;
import com.vslc.tools.array.ArrDataHandler;
import com.vslc.tools.dicom.DcmGetInfoUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("filesUploadService")
public class DataImportService implements IDataImportService {

    @Resource
    private IInspectionDao inspectionDao;

    @Resource
    private IPatientDao patientDao;

    @Resource
    private ISequenceDao sequenceDao;

    @Resource
    private IHospitalDao hospitalDao;

    @Resource
    private IModeDao modeDao;

    private List<DcmInfo> dcmInfoList;

    private Patient patient;

    private Inspection inspection;

    private Sequence sequence;

    @Override
    public Map<String, Object> upload(List<MultipartFile> files) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        String pwdTime = timeFormat.format(new Date()) + "\\";
        String storagePath = SavePath.uploadPath;
        storagePath += pwdTime;
        Map<String, Object> result = new HashMap<>();
        List<Sequence> sequenceList = new ArrayList<>();
        //判断存储的文件夹是否存在
        File file = new File(storagePath);
        if (!file.exists()) file.mkdirs();

        try {
            DcmInfo uploadDcmInfo = ArrDataHandler.getDcmInfo(files);
            String hospFullName = uploadDcmInfo.getHospitalName();
            Hospital hospital = IniUtil.getHospital(hospFullName);
            if (hospital != null)
                hospital = hospitalDao.findByHospitalID(hospital.getHospitalID());
            else {
                hospital = new Hospital();
                hospital.setHospitalName(hospFullName);
            }
            result.put("hospital", hospital);
            for (MultipartFile mf : files) {
                if (!mf.isEmpty()) {
                    ArrDataHandler.findLocalAndWrite(mf, storagePath, uploadDcmInfo);
                }
            }

            String uploadPath = pwdTime + uploadDcmInfo.getHospitalName() + "\\" + uploadDcmInfo.getStudyId();
            File uploadInspe = new File(storagePath + uploadDcmInfo.getHospitalName() + "\\" + uploadDcmInfo.getStudyId());
            File[] seFiles = uploadInspe.listFiles();
            for (File seDir : seFiles) {
                if (!seDir.getName().equals("NotDcm"))
                    sequenceList.add(ergodicSeries(seDir));
            }
            StringBuilder sb = new StringBuilder();
            sb.append(uploadDcmInfo.getEnglishName());
            sb.append("_");
            sb.append(uploadDcmInfo.getCTNum());
            sb.append("_");
            sb.append(uploadDcmInfo.getPatientID());
            result.put("inspection", sb.toString());
            result.put("inspectionID", uploadDcmInfo.getStudyId());
            result.put("sequence", sequenceList);
            result.put("uploadPath", uploadPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String add(String inspeDir, String hospitalID, Integer userID) {
        Hospital hospital = hospitalDao.findByHospitalID(hospitalID);
        if (hospital != null) {
            IniUtil.updateHospital(hospital);
            String shortName = hospital.getHospitalShortName();
            DcmGetInfoUtil getInfoUtil = new DcmGetInfoUtil();
            dcmInfoList = getInfoUtil.ergodicInspection(new File(inspeDir), shortName);
            for (DcmInfo dcmInfo : dcmInfoList)
                addInfo(dcmInfo, shortName, userID);
            return "success";
        } else {
            return "fail";
        }
    }

    public void addInfo(DcmInfo dcmInfo, String shortName, Integer userID) {
        addPatientInfo(dcmInfo);
        addInspectionInfo(dcmInfo, shortName, userID);
        addSequenceInfo(dcmInfo);
    }

    private void addPatientInfo(DcmInfo dcmInfo) {
        patient = new Patient();
        patient.setPatientID(dcmInfo.getPatientID());
        patient.setEnglishName(dcmInfo.getEnglishName());
        patient.setBirday(dcmInfo.getPatientBirthday());
        patient.setPatientSex(dcmInfo.getPatientSex());

        if(patientDao.findByPatientID(dcmInfo.getPatientID()) == null)
            patientDao.add(patient);
    }

    private void addInspectionInfo(DcmInfo dcmInfo, String shortName, Integer userID) {
        inspection = new Inspection();
        inspection.setInspectionID(dcmInfo.getStudyId());
        inspection.setCTNumber(dcmInfo.getCTNum());
        inspection.setInspectTime(dcmInfo.getInspectTime());
        inspection.setSavePath(dcmInfo.getSavePath());
        inspection.setPatient(patient);
        inspection.setUploader(userID);

        Mode mode = modeDao.findByModeName(dcmInfo.getModality());
        if (mode != null)
            inspection.setMode(mode);

        Hospital hospital = hospitalDao.findByEnglishName(shortName);
        if (hospital != null)
            inspection.setHospital(hospital);

        if(inspectionDao.findByInspectionID(dcmInfo.getStudyId()) == null)
            inspectionDao.add(inspection);
    }

    private void addSequenceInfo(DcmInfo dcmInfo) {
        sequence = new Sequence();
        sequence.setSequenceName(dcmInfo.getSeriesName());
        sequence.setSequenceNum(dcmInfo.getSeriesNum());
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

    private Sequence ergodicSeries(File seDir) {
        Sequence sequence = new Sequence();
        String sePath = seDir.getAbsolutePath();
        File dcmDir = new File(sePath+"\\DCM");
        File maskDir = new File(sePath+"\\MASK");
        File[] dcms = dcmDir.listFiles();
        File[] masks = maskDir.listFiles();
        if (masks.length > 0) sequence.setIsSketch(1);
        else sequence.setIsSketch(0);
        for (File file : dcms) {
            DcmInfo dcmInfo = new DcmInfo(file, false);
            sequence.setSequenceName(dcmInfo.getSeriesName());
            sequence.setSequenceNum(dcmInfo.getSeriesNum());
            Inspection inspection = new Inspection();
            inspection.setInspectionID(dcmInfo.getStudyId());
            sequence.setInspection(inspection);
            sequence.setFileNum(dcms.length);
            break;
        }
        return sequence;
    }
}
