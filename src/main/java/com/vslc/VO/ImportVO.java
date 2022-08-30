package com.vslc.VO;

import java.util.List;

/**
 * 导入数据传输对象
 * Created by chenlele
 * 2018/6/9 10:04
 */
public class ImportVO {
    //检查ID
    private String inspectionID;
    //序列号
    private String sequenceNum;
    //医院ID
    private String hospitalID;
    //上传路径
    private String uploadPath;
    //导入类型，i检查，m mask
    private String uploadType;
    //用户选择的导入列表
    private List<ImportVO> selections;

    public String getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(String inspectionID) {
        this.inspectionID = inspectionID;
    }

    public String getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(String sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public List<ImportVO> getSelections() {
        return selections;
    }

    public void setSelections(List<ImportVO> selections) {
        this.selections = selections;
    }
}
