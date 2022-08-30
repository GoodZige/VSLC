package com.vslc.controller;

import com.vslc.model.Inspection;
import com.vslc.model.ReviewResult;
import com.vslc.model.Sequence;
import com.vslc.model.User;
import com.vslc.service.IInspectionService;
import com.vslc.service.IReviewResultService;
import com.vslc.service.ISequenceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping(value = "/reviewResult")
public class ReviewResultController {

    @Resource
    private IReviewResultService reviewResultService;

    @Resource
    private IInspectionService inspectionService;

    @Resource
    private ISequenceService sequenceService;

    @RequestMapping(value="/getGrade")
    @ResponseBody
    public List<ReviewResult> getReviewResultList(@RequestParam(value="sequenceID") Integer sequenceID
            ,@RequestParam(value="sketchType") Integer sketchType
            ,@RequestParam(value="sketchNum") Integer sketchNum) {
        List<ReviewResult> reviewResultList = new ArrayList<>();
        ReviewResult reviewResult = new ReviewResult();
        Sequence sequence = new Sequence();
        sequence.setSequenceID(sequenceID);
        reviewResult.setSequence(sequence);
        reviewResult.setSketchType(sketchType);
        if (sketchNum != null) {
            reviewResult.setSketchNum(sketchNum);
            ReviewResult one = reviewResultService.findOne(reviewResult);
            if (one != null) reviewResultList.add(one);
        } else {
            reviewResultList = reviewResultService.findTypeList(reviewResult);
        }
        return reviewResultList;
    }

    /**
     * 新增审核意见
     * @param sketchSum 总标注结果
     * @param sketchScore 审核分数
     * @param reviewAdvice 审核意见
     * @param sketchFile 标注文件
     * @param sketchType 标注类型
     * @param sketchNum 结节号
     * @param sequenceID
     * @param examiner 审核者
     * @return
     */
    @RequestMapping(value="/add")
    @ResponseBody
    public String add(@RequestParam(value="sketchSum") Integer sketchSum
            ,@RequestParam(value="sketchScore") Integer sketchScore
            ,@RequestParam(value="reviewAdvice") String reviewAdvice
            ,@RequestParam(value="sketchFile") String sketchFile
            ,@RequestParam(value="sketchType") Integer sketchType
            ,@RequestParam(value="sketchNum") Integer sketchNum
            ,@RequestParam(value="sequenceID") Integer sequenceID
            ,@RequestParam(value="examiner") Integer examiner) {
        ReviewResult reviewResult = new ReviewResult();
        Sequence sequence =  sequenceService.findBySequenceID(sequenceID);
        User user = new User();
        user.setUserID(examiner);
        reviewResult.setSketchScore(sketchScore);
        reviewResult.setReviewAdvice(reviewAdvice);
        reviewResult.setSequence(sequence);
        reviewResult.setExaminer(user);
        reviewResult.setSketchFile(sketchFile);
        reviewResult.setSketchType(sketchType);
        reviewResult.setSketchNum(sketchNum);
        if (reviewResultService.findOne(reviewResult) == null)
            reviewResultService.add(reviewResult);
        else
            reviewResultService.update(reviewResult);

        boolean reviewed = true;
        List<ReviewResult> reviewList = reviewResultService.findBySequenceID(sequenceID);
        //全部审核完才可以算通过审核
        if (reviewList.size() < sketchSum) reviewed = false;
        else {
            for (ReviewResult review : reviewList) {
                //80分合格
                if (review.getSketchScore() < 80) {
                    reviewed = false;
                    break;
                }
            }
        }
        if (reviewed) {
            Inspection inspection = sequence.getInspection();
            inspection.setProcessID(3);
            inspectionService.updateProcessID(inspection);
        }
        return "success";
    }

    @RequestMapping(value="/delete")
    @ResponseBody
    public String delete(@RequestParam(value="sketchFile") String sketchFile
            ,@RequestParam(value="sequenceID") Integer sequenceID) {
        ReviewResult reviewResult = new ReviewResult();
        Sequence sequence = new Sequence();
        sequence.setSequenceID(sequenceID);
        reviewResult.setSketchFile(sketchFile);
        reviewResult.setSequence(sequence);
        if (reviewResultService.findOne(reviewResult) != null)
            reviewResultService.delete(reviewResult);
        return "success";
    }
}
