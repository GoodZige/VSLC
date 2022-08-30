package com.vslc.dao;

import com.vslc.model.Patient;

import java.util.List;
import java.util.Map;

public interface IPatientDao {

    List<Patient> search(Map<String, Object> param);

    Patient findByPatientID(String patientID);

    Patient findByAdmissionNum(String admissionNum);

    Integer getCount(Map<String, Object> param);

    void add(Patient patient);

    void update(Patient patient);
}
