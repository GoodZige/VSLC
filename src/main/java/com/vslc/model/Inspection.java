package com.vslc.model;

import java.util.Date;

public class Inspection {

    private String inspectionID;

    private User editor;

    private String remark;

    private Patient patient;

    private Integer processID;

    private Date inspectTime;

    private Float thickness;

    private Integer imageMethod;

    private Integer isAbdomen;

    private Integer isAbdomenCE;

    private Float PNSize;

    private Integer PNNum;

    private Integer PNSign;

    private Hospital hospital;

    private Disease disease;

    private Mode mode;

    private String CTNumber;

    private String savePath;

    private Integer uploader;

    private Integer drawer;

    private Integer drawExaminer;

    private Integer signer;

    private Integer signExaminer;

    public String getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(String inspectionID) {
        this.inspectionID = inspectionID;
    }

    public User getEditor() {
        return editor;
    }

    public void setEditor(User editor) {
        this.editor = editor;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Integer getProcessID() {
        return processID;
    }

    public void setProcessID(Integer processID) {
        this.processID = processID;
    }

    public Date getInspectTime() {
        return inspectTime;
    }

    public void setInspectTime(Date inspectTime) {
        this.inspectTime = inspectTime;
    }

    public Float getThickness() {
        return thickness;
    }

    public void setThickness(Float thickness) {
        this.thickness = thickness;
    }

    public Integer getImageMethod() {
        return imageMethod;
    }

    public void setImageMethod(Integer imageMethod) {
        this.imageMethod = imageMethod;
    }

    public Integer getIsAbdomen() {
        return isAbdomen;
    }

    public void setIsAbdomen(Integer isAbdomen) {
        this.isAbdomen = isAbdomen;
    }

    public Integer getIsAbdomenCE() {
        return isAbdomenCE;
    }

    public void setIsAbdomenCE(Integer isAbdomenCE) {
        this.isAbdomenCE = isAbdomenCE;
    }

    public Float getPNSize() {
        return PNSize;
    }

    public void setPNSize(Float PNSize) {
        this.PNSize = PNSize;
    }

    public Integer getPNNum() {
        return PNNum;
    }

    public void setPNNum(Integer PNNum) {
        this.PNNum = PNNum;
    }

    public Integer getPNSign() {
        return PNSign;
    }

    public void setPNSign(Integer PNSign) {
        this.PNSign = PNSign;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getCTNumber() {
        return CTNumber;
    }

    public void setCTNumber(String CTNumber) {
        this.CTNumber = CTNumber;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public Integer getUploader() {
        return uploader;
    }

    public void setUploader(Integer uploader) {
        this.uploader = uploader;
    }

    public Integer getDrawer() {
        return drawer;
    }

    public void setDrawer(Integer drawer) {
        this.drawer = drawer;
    }

    public Integer getDrawExaminer() {
        return drawExaminer;
    }

    public void setDrawExaminer(Integer drawExaminer) {
        this.drawExaminer = drawExaminer;
    }

    public Integer getSigner() {
        return signer;
    }

    public void setSigner(Integer signer) {
        this.signer = signer;
    }

    public Integer getSignExaminer() {
        return signExaminer;
    }

    public void setSignExaminer(Integer signExaminer) {
        this.signExaminer = signExaminer;
    }
}
