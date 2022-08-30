package com.vslc.service.impl;

import com.vslc.dao.IHospitalDao;
import com.vslc.model.Hospital;
import com.vslc.service.IHospitalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service(value = "hospitalService")
public class HospitalService implements IHospitalService {

    @Resource
    private IHospitalDao hospitalDao;

    @Override
    public List<Hospital> search(Map<String, Object> param) {
        return hospitalDao.search(param);
    }

    @Override
    public List<Hospital> find() {
        return hospitalDao.find();
    }

    @Override
    public Integer getCount(String hospitalName) {
        return hospitalDao.getCount(hospitalName);
    }

    @Override
    public Integer add(Hospital hospital) {
        return hospitalDao.add(hospital);
    }

    @Override
    public void update(Hospital hospital) {
        hospitalDao.update(hospital);
    }

    @Override
    public void delete(String hospitalID) {
        hospitalDao.delete(hospitalID);
    }
}
