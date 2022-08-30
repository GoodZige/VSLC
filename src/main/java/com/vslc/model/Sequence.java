package com.vslc.model;

public class Sequence {

    private Integer sequenceID;

    private String SequenceName;

    private String sequenceNum;

    private Float thickness;

    private Integer fileNum;

    private int isSketch;

    private Inspection inspection;

    private String dcmPath;

    private Integer width;

    private Integer height;

    public Integer getSequenceID() {
        return sequenceID;
    }

    public void setSequenceID(Integer sequenceID) {
        this.sequenceID = sequenceID;
    }

    public String getSequenceName() {
        return SequenceName;
    }

    public void setSequenceName(String sequenceName) {
        SequenceName = sequenceName;
    }

    public String getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(String sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public Float getThickness() {
        return thickness;
    }

    public void setThickness(Float thickness) {
        this.thickness = thickness;
    }

    public Integer getFileNum() {
        return fileNum;
    }

    public void setFileNum(Integer fileNum) {
        this.fileNum = fileNum;
    }

    public int getIsSketch() {
        return isSketch;
    }

    public void setIsSketch(int isSketch) {
        this.isSketch = isSketch;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public void setInspection(Inspection inspection) {
        this.inspection = inspection;
    }

    public String getDcmPath() {
        return dcmPath;
    }

    public void setDcmPath(String dcmPath) {
        this.dcmPath = dcmPath;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
