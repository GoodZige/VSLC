package com.vslc.service;

import com.vslc.model.Hospital;

import java.util.List;
import java.util.Map;

public interface IHospitalService {

    List<Hospital> search(Map<String, Object> param);

    List<Hospital> find();

    Integer getCount(String hospitalName);

    Integer add(Hospital hospital);

    void update(Hospital hospital);

    void delete(String hospitalID);
}
