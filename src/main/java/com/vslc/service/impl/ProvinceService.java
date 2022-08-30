package com.vslc.service.impl;

import com.vslc.dao.IProvinceDao;
import com.vslc.model.Province;
import com.vslc.service.IProvinceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service(value = "provinceService")
public class ProvinceService implements IProvinceService {

    @Resource
    private IProvinceDao provinceDao;

    @Override
    public List<Province> find() {
        return provinceDao.find();
    }
}
