package com.vslc.service;

import com.vslc.model.Patient;

import java.util.List;
import java.util.Map;

public interface IPatientService {

    List<Patient> search(Map<String, Object> param);

    Patient findByPatientID(String patientID);

    Integer getCount(Map<String, Object> param);

    void add(Patient patient);
}
