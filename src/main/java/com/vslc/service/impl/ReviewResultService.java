package com.vslc.service.impl;

import com.vslc.dao.IReviewResultDao;
import com.vslc.model.ReviewResult;
import com.vslc.service.IReviewResultService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service(value = "reviewResultService")
public class ReviewResultService implements IReviewResultService {

    @Resource
    private IReviewResultDao reviewResultDao;

    @Override
    public List<ReviewResult> search(Map<String, Object> param) {
        return reviewResultDao.search(param);
    }

    @Override
    public List<ReviewResult> findTypeList(ReviewResult reviewResult) {
        return reviewResultDao.findTypeList(reviewResult);
    }

    @Override
    public ReviewResult findOne(ReviewResult reviewResult) {
        return reviewResultDao.findOne(reviewResult);
    }

    @Override
    public List<ReviewResult> findBySequenceID(Integer sequenceID) {
        return reviewResultDao.findBySequenceID(sequenceID);
    }

    @Override
    public void add(ReviewResult reviewResult) {
        reviewResultDao.add(reviewResult);
    }

    @Override
    public void update(ReviewResult reviewResult) {
        reviewResultDao.update(reviewResult);
    }

    @Override
    public void delete(ReviewResult reviewResult) {
        reviewResultDao.delete(reviewResult);
    }
}
