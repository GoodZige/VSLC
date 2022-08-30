package com.vslc.service;

import com.vslc.model.Pathology;

import java.util.List;

public interface IPathologyService {

    Pathology findByPathologyID(Integer pathologyID);

    List<Pathology> findByPatientID(String patientID);

    List<Pathology> findByAdmissionNum(String admissionNum);
}
