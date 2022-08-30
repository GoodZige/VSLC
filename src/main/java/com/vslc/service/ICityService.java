package com.vslc.service;

import com.vslc.model.City;

import java.util.List;

public interface ICityService {

    List<City> findByProvinceID(String provinceID);
}
