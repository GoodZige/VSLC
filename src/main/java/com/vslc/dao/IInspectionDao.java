package com.vslc.dao;

import com.vslc.model.Inspection;

import java.util.List;
import java.util.Map;

public interface IInspectionDao {

    List<Inspection> search(Map<String, Object> param);

    List<Inspection> findByPatientID(String patientID);

    Inspection findByInspectionID(String inspectionID);

    Integer getCount(Map<String, Object> param);

    void add(Inspection inspection);

    void update(Map<String, Object> param);

    void updateProcessID(Inspection inspection);

    void updateWorker(Map<String, Object> param);

    void delete(List<String> inspectionIDs);
}
