package com.vslc.service.impl;

import com.vslc.dao.IPathologyDao;
import com.vslc.model.Pathology;
import com.vslc.service.IPathologyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by chenlele
 * 2018/5/27 17:10
 */
@Service(value = "pathologyService")
public class PathologyService implements IPathologyService {

    @Resource
    private IPathologyDao pathologyDao;

    @Override
    public Pathology findByPathologyID(Integer pathologyID) {
        return pathologyDao.findByPathologyID(pathologyID);
    }

    @Override
    public List<Pathology> findByPatientID(String patientID) {
        return pathologyDao.findByPatientID(patientID);
    }

    @Override
    public List<Pathology> findByAdmissionNum(String admissionNum) {
        return pathologyDao.findByAdmissionNum(admissionNum);
    }
}
