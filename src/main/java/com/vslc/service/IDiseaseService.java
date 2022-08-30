package com.vslc.service;

import com.vslc.model.Disease;

import java.util.List;
import java.util.Map;

public interface IDiseaseService {

    List<Disease> search(Map<String, Object> param);

    List<Disease> find();

    Integer getCount(String diseaseName);

    Integer add(Disease disease);

    void update(Disease disease);

    void delete(String diseaseID);
}
