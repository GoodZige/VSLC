package com.vslc.service.impl;

import com.vslc.dao.IInspectionDao;
import com.vslc.model.Inspection;
import com.vslc.service.IInspectionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service(value = "inspectionService")
public class InspectionService implements IInspectionService {

    @Resource
    private IInspectionDao inspectionDao;

    @Override
    public List<Inspection> search(Map<String, Object> param) {
        return inspectionDao.search(param);
    }

    @Override
    public List<Inspection> findByPatientID(String patientID) {
        return inspectionDao.findByPatientID(patientID);
    }

    @Override
    public Inspection findByInspectionID(String inspectionID) {
        return inspectionDao.findByInspectionID(inspectionID);
    }

    @Override
    public Integer getCount(Map<String, Object> param) {
        return inspectionDao.getCount(param);
    }

    @Override
    public void add(Inspection inspection) {
        inspectionDao.add(inspection);
    }

    @Override
    public void update(Map<String, Object> param) {
        inspectionDao.update(param);
    }

    @Override
    public void updateProcessID(Inspection inspection) {
        inspectionDao.updateProcessID(inspection);
    }

    @Override
    public void updateWorker(Map<String, Object> param) {
        inspectionDao.updateWorker(param);
    }

    @Override
    public void delete(List<String> inspectionIDs) {
        inspectionDao.delete(inspectionIDs);
    }
}
