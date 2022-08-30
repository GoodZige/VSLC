package com.vslc.model;

import com.vslc.tools.dicom.DcmHandler;
import com.vslc.tools.dicom.DcmInfoReader;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class Patient {

    private String patientID;

    private String admissionNum;

    private String englishName;

    private String chineseName;

    private Integer patientSex;

    private Date birday;

    private String IDNumber;

    private String nativePlace;

    private String contacts;

    private String tel;

    private Date admissionDate;

    private Date dischargeDate;

    private String surgeon;

    private String dischargeDiagnosis;

    private Date operationDate;

    private String operationName;

    public Patient() {

    }

    public Patient(File file, boolean readImgArr) {
        HashMap<String, Object> dcmData = DcmHandler.handle(file, readImgArr);
        patientID = DcmInfoReader.readPaitentID(dcmData);
        englishName = DcmInfoReader.readPaitentName(dcmData);
        patientSex = DcmInfoReader.readPatientSex(dcmData);
        try {
            birday = DcmInfoReader.readPatientBirthday(dcmData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getAdmissionNum() {
        return admissionNum;
    }

    public void setAdmissionNum(String admissionNum) {
        this.admissionNum = admissionNum;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public Integer getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(Integer patientSex) {
        this.patientSex = patientSex;
    }

    public Date getBirday() {
        return birday;
    }

    public void setBirday(Date birday) {
        this.birday = birday;
    }

    public String getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber) {
        this.IDNumber = IDNumber;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getSurgeon() {
        return surgeon;
    }

    public void setSurgeon(String surgeon) {
        this.surgeon = surgeon;
    }

    public String getDischargeDiagnosis() {
        return dischargeDiagnosis;
    }

    public void setDischargeDiagnosis(String dischargeDiagnosis) {
        this.dischargeDiagnosis = dischargeDiagnosis;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
}
