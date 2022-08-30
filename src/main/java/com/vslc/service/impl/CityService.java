package com.vslc.service.impl;

import com.vslc.dao.ICityDao;
import com.vslc.model.City;
import com.vslc.service.ICityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service(value = "cityService")
public class CityService implements ICityService {

    @Resource
    private ICityDao cityDao;

    @Override
    public List<City> findByProvinceID(String provinceID) {
        return cityDao.findByProvinceID(provinceID);
    }
}
