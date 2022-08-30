package com.vslc.dao;

import com.vslc.model.City;

import java.util.List;

public interface ICityDao {

    List<City> findByProvinceID(String provinceID);
}
