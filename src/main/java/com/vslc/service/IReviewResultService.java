package com.vslc.service;

import com.vslc.model.ReviewResult;

import java.util.List;
import java.util.Map;

public interface IReviewResultService {

    List<ReviewResult> search(Map<String, Object> param);

    List<ReviewResult> findTypeList(ReviewResult reviewResult);

    ReviewResult findOne(ReviewResult reviewResult);

    List<ReviewResult> findBySequenceID(Integer sequenceID);

    void add(ReviewResult reviewResult);

    void update(ReviewResult reviewResult);

    void delete(ReviewResult reviewResult);
}
