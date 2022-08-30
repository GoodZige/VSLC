package com.vslc.dao;

import com.vslc.model.Examination;

import java.util.List;

public interface IExaminationDao {

    Examination findByExaminationID(Integer examinationID);

    List<Examination> findByPatientID(String patientID);

    void add(Examination examination);

    void update(Examination examination);

    void delete(Integer examinationID);
}
