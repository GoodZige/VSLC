package com.vslc.service.impl;

import com.vslc.dao.IPatientDao;
import com.vslc.model.Patient;
import com.vslc.service.IPatientService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service(value = "patientService")
public class PatientService implements IPatientService {

    @Resource
    private IPatientDao patientDao;

    @Override
    public List<Patient> search(Map<String, Object> param) {
        return patientDao.search(param);
    }

    @Override
    public Patient findByPatientID(String patientID) {
        return patientDao.findByPatientID(patientID);
    }

    @Override
    public Integer getCount(Map<String, Object> param) {
        return patientDao.getCount(param);
    }

    @Override
    public void add(Patient patient) {
        patientDao.add(patient);
    }
}
