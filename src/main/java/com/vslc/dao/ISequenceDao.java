package com.vslc.dao;

import com.vslc.model.Sequence;

import java.util.List;
import java.util.Map;

public interface ISequenceDao {

    List<Sequence> findByInspectionID(String inspectionID);

    List<Sequence> findByIsSketch(Integer isSketch);

    List<Sequence> findBySequenceIDs(List<Integer> sequenceIDs);

    Sequence findBySequenceID(Integer sequenceID);

    Sequence findOne(Map<String, Object> param);

    void add(Sequence sequence);

    void update(Sequence sequence);

    void updateIsSketch(Sequence sequence);
}
