package com.vslc.service;

import com.vslc.model.Inspection;

import java.util.List;
import java.util.Map;

public interface IInspectionService {

    Integer getCount(Map<String, Object> param);

    List<Inspection> search(Map<String, Object> param);

    List<Inspection> findByPatientID(String patientID);

    Inspection findByInspectionID(String inspectionID);

    void add(Inspection inspection);

    void update(Map<String, Object> param);

    void updateProcessID(Inspection inspection);

    void updateWorker(Map<String, Object> param);

    void delete(List<String> inspectionIDs);
}
