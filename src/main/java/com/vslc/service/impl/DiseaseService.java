package com.vslc.service.impl;

import com.vslc.dao.IDiseaseDao;
import com.vslc.model.Disease;
import com.vslc.service.IDiseaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service(value = "diseaseService")
public class DiseaseService implements IDiseaseService {

    @Resource
    private IDiseaseDao diseaseDao;

    @Override
    public List<Disease> search(Map<String, Object> param) {
        return diseaseDao.search(param);
    }

    @Override
    public List<Disease> find() {
        return diseaseDao.find();
    }

    @Override
    public Integer getCount(String diseaseName) {
        return diseaseDao.getCount(diseaseName);
    }

    @Override
    public Integer add(Disease disease) {
        return diseaseDao.add(disease);
    }

    @Override
    public void update(Disease disease) {
        diseaseDao.update(disease);
    }

    @Override
    public void delete(String diseaseID) {
        diseaseDao.delete(diseaseID);
    }
}
