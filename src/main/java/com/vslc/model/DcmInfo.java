package com.vslc.model;

import com.vslc.tools.dicom.DcmHandler;
import com.vslc.tools.dicom.DcmInfoReader;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class DcmInfo {
    //是否dicom文件
    private boolean isDcm;
    //医院全名
    private String hospitalName;
    //病人ID
    private String patientID;
    //病人姓名
    private String englishName;
    //CT号
    private String CTNum;
    //序列号
    private String seriesNum;
    //序列名
    private String seriesName;
    //图像下标
    private int imgIndex;
    //dicom二维矩阵
    private short[][] imgArr;
    //层厚
    private Float thickness;
    //列数 宽
    private int columns;
    //行数 高
    private int rows;
    //窗宽
    private int windowWidth;
    //窗位
    private int windowCenter;
    //最小像素值
    private int minVal;
    //最大像素值
    private int maxVal;
    //斜率
    private float slope;
    //截距
    private float intercept;
    //检查时间
    private Date inspectTime;
    //病人出生日期
    private Date patientBirthday;
    //病人性别
    private Integer patientSex;
    //检查模态
    private String modality;
    private String studyId;
    private String seriesId;
    private int bitsStored;
    //像素尺 单位毫米
    private double columnPixelSpacing;
    private double rowPixelSpacing;
    //传输方式
    private String transferId;
    //非dicom文件信息
    private int fileNum;
    private String savePath;
    private String dcmPath;

    public DcmInfo(HashMap<String, Object> dcmData) {
        init(dcmData);
    }

    public DcmInfo(File file, boolean readImgArr) {
        HashMap<String, Object> dcmData = DcmHandler.handle(file, false);
        init(dcmData);
        if (readImgArr) {
            if (transferId.equals("1.2.840.10008.1.2") ||
                    transferId.equals("1.2.840.10008.1.2.1") ||
                    transferId.equals("1.2.840.10008.1.2.2")) {
                dcmData = DcmHandler.handle(file, readImgArr);
            } else {
                dcmData = DcmHandler.undicom(file, readImgArr);
            }
            init(dcmData);
        }
    }

    private void init(HashMap<String, Object> dcmData) {
        isDcm = DcmInfoReader.readIsDcm(dcmData);
        hospitalName = DcmInfoReader.readHospitalName(dcmData);
        patientID = DcmInfoReader.readPaitentID(dcmData);
        englishName = DcmInfoReader.readPaitentName(dcmData);
        patientSex = DcmInfoReader.readPatientSex(dcmData);
        CTNum = DcmInfoReader.readCTNum(dcmData);
        seriesNum = DcmInfoReader.readSeriesNum(dcmData);
        seriesName = DcmInfoReader.readSeriesName(dcmData);
        imgArr = DcmInfoReader.readImgArr(dcmData);
        imgIndex = DcmInfoReader.readImgIndex(dcmData);
        thickness = DcmInfoReader.readThickness(dcmData);
        columns = DcmInfoReader.readColumns(dcmData);
        rows = DcmInfoReader.readRows(dcmData);
        windowWidth = DcmInfoReader.readLungWindowWidth(dcmData);
        windowCenter = DcmInfoReader.readLungWindowCenter(dcmData);
        modality = DcmInfoReader.readModality(dcmData);
        minVal = DcmInfoReader.readMinVal(dcmData);
        maxVal = DcmInfoReader.readMaxVal(dcmData);
        slope = DcmInfoReader.readSlope(dcmData);
        intercept = DcmInfoReader.readIntercept(dcmData);
        studyId = DcmInfoReader.readSdID(dcmData);
        seriesId = DcmInfoReader.readSrID(dcmData);
        columnPixelSpacing = DcmInfoReader.readColumnPixelSpacing(dcmData);
        rowPixelSpacing = DcmInfoReader.readRowPixelSpacing(dcmData);
        transferId = DcmInfoReader.readTransferId(dcmData);
        bitsStored = DcmInfoReader.readBitsStored(dcmData);
        try {
            patientBirthday = DcmInfoReader.readPatientBirthday(dcmData);
            inspectTime = DcmInfoReader.readInspectTime(dcmData);
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
    }

    public boolean isDcm() {
        return isDcm;
    }

    public void setDcm(boolean dcm) {
        isDcm = dcm;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getCTNum() {
        return CTNum;
    }

    public void setCTNum(String CTNum) {
        this.CTNum = CTNum;
    }

    public String getSeriesNum() {
        return seriesNum;
    }

    public void setSeriesNum(String seriesNum) {
        this.seriesNum = seriesNum;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public int getImgIndex() {
        return imgIndex;
    }

    public void setImgIndex(int imgIndex) {
        this.imgIndex = imgIndex;
    }

    public short[][] getImgArr() {
        return imgArr;
    }

    public void setImgArr(short[][] imgArr) {
        this.imgArr = imgArr;
    }

    public Float getThickness() {
        return thickness;
    }

    public void setThickness(Float thickness) {
        this.thickness = thickness;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowCenter() {
        return windowCenter;
    }

    public void setWindowCenter(int windowCenter) {
        this.windowCenter = windowCenter;
    }

    public int getMinVal() {
        return minVal;
    }

    public void setMinVal(int minVal) {
        this.minVal = minVal;
    }

    public int getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(int maxVal) {
        this.maxVal = maxVal;
    }

    public float getSlope() {
        return slope;
    }

    public void setSlope(float slope) {
        this.slope = slope;
    }

    public float getIntercept() {
        return intercept;
    }

    public void setIntercept(float intercept) {
        this.intercept = intercept;
    }

    public Date getInspectTime() {
        return inspectTime;
    }

    public void setInspectTime(Date inspectTime) {
        this.inspectTime = inspectTime;
    }

    public Date getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(Date patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public Integer getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(Integer patientSex) {
        this.patientSex = patientSex;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getDcmPath() {
        return dcmPath;
    }

    public void setDcmPath(String dcmPath) {
        this.dcmPath = dcmPath;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public double getColumnPixelSpacing() {
        return columnPixelSpacing;
    }

    public void setColumnPixelSpacing(double columnPixelSpacing) {
        this.columnPixelSpacing = columnPixelSpacing;
    }

    public double getRowPixelSpacing() {
        return rowPixelSpacing;
    }

    public void setRowPixelSpacing(double rowPixelSpacing) {
        this.rowPixelSpacing = rowPixelSpacing;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public int getBitsStored() {
        return bitsStored;
    }

    public void setBitsStored(int bitsStored) {
        this.bitsStored = bitsStored;
    }
}
