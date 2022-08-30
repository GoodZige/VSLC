package com.vslc.model;

public class PermissionGroup {

    private Integer permissionGroupID;

    private String permissionGroupName;

    private Integer baseMod;

    private Integer taskMod;

    private Integer uploadMod;

    private Integer dataMod;

    private Integer drawMod;

    private Integer drawVerifyMod;

    private Integer signMod;

    private Integer signVerifyMod;

    public Integer getPermissionGroupID() {
        return permissionGroupID;
    }

    public void setPermissionGroupID(Integer permissionGroupID) {
        this.permissionGroupID = permissionGroupID;
    }

    public String getPermissionGroupName() {
        return permissionGroupName;
    }

    public void setPermissionGroupName(String permissionGroupName) {
        this.permissionGroupName = permissionGroupName;
    }

    public Integer getBaseMod() {
        return baseMod;
    }

    public void setBaseMod(Integer baseMod) {
        this.baseMod = baseMod;
    }

    public Integer getTaskMod() {
        return taskMod;
    }

    public void setTaskMod(Integer taskMod) {
        this.taskMod = taskMod;
    }

    public Integer getUploadMod() {
        return uploadMod;
    }

    public void setUploadMod(Integer uploadMod) {
        this.uploadMod = uploadMod;
    }

    public Integer getDataMod() {
        return dataMod;
    }

    public void setDataMod(Integer dataMod) {
        this.dataMod = dataMod;
    }

    public Integer getDrawMod() {
        return drawMod;
    }

    public void setDrawMod(Integer drawMod) {
        this.drawMod = drawMod;
    }

    public Integer getDrawVerifyMod() {
        return drawVerifyMod;
    }

    public void setDrawVerifyMod(Integer drawVerifyMod) {
        this.drawVerifyMod = drawVerifyMod;
    }

    public Integer getSignMod() {
        return signMod;
    }

    public void setSignMod(Integer signMod) {
        this.signMod = signMod;
    }

    public Integer getSignVerifyMod() {
        return signVerifyMod;
    }

    public void setSignVerifyMod(Integer signVerifyMod) {
        this.signVerifyMod = signVerifyMod;
    }
}
