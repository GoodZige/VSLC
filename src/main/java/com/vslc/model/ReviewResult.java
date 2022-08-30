package com.vslc.model;

public class ReviewResult {

    private Integer reviewID;

    private Integer sketchScore;

    private String reviewAdvice;

    private String sketchFile;

    private Integer sketchType;

    private Integer sketchNum;

    private Sequence sequence;

    private User examiner;

    public Integer getReviewID() {
        return reviewID;
    }

    public void setReviewID(Integer reviewID) {
        this.reviewID = reviewID;
    }

    public Integer getSketchScore() {
        return sketchScore;
    }

    public void setSketchScore(Integer sketchScore) {
        this.sketchScore = sketchScore;
    }

    public String getReviewAdvice() {
        return reviewAdvice;
    }

    public void setReviewAdvice(String reviewAdvice) {
        this.reviewAdvice = reviewAdvice;
    }

    public String getSketchFile() {
        return sketchFile;
    }

    public void setSketchFile(String sketchFile) {
        this.sketchFile = sketchFile;
    }

    public Integer getSketchType() {
        return sketchType;
    }

    public void setSketchType(Integer sketchType) {
        this.sketchType = sketchType;
    }

    public Integer getSketchNum() {
        return sketchNum;
    }

    public void setSketchNum(Integer sketchNum) {
        this.sketchNum = sketchNum;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public User getExaminer() {
        return examiner;
    }

    public void setExaminer(User examiner) {
        this.examiner = examiner;
    }
}
