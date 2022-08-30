package com.vslc.dao;

import com.vslc.model.ReviewResult;

import java.util.List;
import java.util.Map;

public interface IReviewResultDao {

    //结节类型、结节编号、序列id
    ReviewResult findOne(ReviewResult reviewResult);

    //结节类型、序列id
    List<ReviewResult> findTypeList(ReviewResult reviewResult);

    List<ReviewResult> findBySequenceID(Integer sequenceID);

    List<ReviewResult> search(Map<String, Object> param);

    void add(ReviewResult reviewResult);

    void update(ReviewResult reviewResult);

    void delete(ReviewResult reviewResult);
}
