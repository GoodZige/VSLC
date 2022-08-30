package com.vslc.dao;

import com.vslc.model.Hospital;

import java.util.List;
import java.util.Map;

public interface IHospitalDao {

    List<Hospital> search(Map<String, Object> param);

    Hospital findByEnglishName(String hospitalEnglishName);

    Hospital findByHospitalID(String hospitalID);

    List<Hospital> find();

    Integer getCount(String hospitalName);

    Integer add(Hospital hospital);

    void update(Hospital hospital);

    void delete(String hospitalID);
}
