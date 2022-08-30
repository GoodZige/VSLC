package com.vslc.service;

import com.vslc.model.Examination;

import java.util.List;

public interface IExaminationService {

    Examination findByExaminationID(Integer examinationID);

    List<Examination> findByPatientID(String patientID);
}
