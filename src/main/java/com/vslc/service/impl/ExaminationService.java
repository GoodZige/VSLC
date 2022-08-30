package com.vslc.service.impl;

import com.vslc.dao.IExaminationDao;
import com.vslc.model.Examination;
import com.vslc.service.IExaminationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service(value = "examinationService")
public class ExaminationService implements IExaminationService {

    @Resource
    private IExaminationDao examinationDao;

    @Override
    public Examination findByExaminationID(Integer examinationID) {
        return examinationDao.findByExaminationID(examinationID);
    }

    @Override
    public List<Examination> findByPatientID(String patientID) {
        return examinationDao.findByPatientID(patientID);
    }
}
