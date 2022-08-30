package com.vslc.service.impl;

import com.vslc.dao.ISequenceDao;
import com.vslc.model.Matrix;
import com.vslc.model.Sequence;
import com.vslc.service.ISequenceService;
import com.vslc.tools.PathUtil;
import com.vslc.tools.SavePath;
import com.vslc.tools.dicom.ImgMatrixHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.Map;

@Service(value = "sequenceService")
public class SequenceService implements ISequenceService {

    @Resource
    private ISequenceDao sequenceDao;

    @Override
    public List<Sequence> findByInspectionID(String inspectionID) {
        return sequenceDao.findByInspectionID(inspectionID);
    }

    @Override
    public List<Sequence> findBySequenceIDs(List<Integer> sequenceIDs) {
        return sequenceDao.findBySequenceIDs(sequenceIDs);
    }

    @Override
    public Sequence findBySequenceID(Integer sequenceID) {
        return sequenceDao.findBySequenceID(sequenceID);
    }

    @Override
    public Sequence findOne(Map<String, Object> param) {
        return sequenceDao.findOne(param);
    }

    @Override
    public void updateIsSketch(Sequence sequence) {
        sequenceDao.updateIsSketch(sequence);
    }
}
