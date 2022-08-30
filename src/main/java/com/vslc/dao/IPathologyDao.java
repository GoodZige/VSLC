package com.vslc.dao;

import com.vslc.model.Pathology;

import java.util.List;

/**
 * Created by chenlele
 * 2018/5/26 10:16
 */
public interface IPathologyDao {

    Pathology findByPathologyID(Integer pathologyID);

    List<Pathology> findByPatientID(String patientID);

    List<Pathology> findByGrowthMode(int growthMode);

    List<Pathology> findByAdmissionNum(String admissionNum);

    void add(Pathology pathology);

    void update(Pathology pathology);

    void delete(Integer pathologyID);
}
