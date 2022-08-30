package com.vslc.service;

import com.vslc.model.Sequence;

import java.util.List;
import java.util.Map;

public interface ISequenceService {

    List<Sequence> findByInspectionID(String inspectionID);

    List<Sequence> findBySequenceIDs(List<Integer> sequenceIDs);

    Sequence findBySequenceID(Integer sequenceID);

    Sequence findOne(Map<String, Object> param);

    void updateIsSketch(Sequence sequence);
}
