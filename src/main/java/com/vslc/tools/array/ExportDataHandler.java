package com.vslc.tools.array;

import com.vslc.model.DcmInfo;
import com.vslc.model.Hospital;
import com.vslc.model.Sequence;
import com.vslc.tools.FileUtil;
import com.vslc.tools.IniUtil;
import com.vslc.tools.SavePath;

import java.io.*;
import java.util.List;

public class ExportDataHandler {

    public static void handle(String desPath, List<Sequence> sequenceList) {
        for (Sequence sequence : sequenceList) {
            StringBuilder srcPath = new StringBuilder(SavePath.rootPath);
            srcPath.append(sequence.getInspection().getSavePath());
            srcPath.append("\\");
            srcPath.append(sequence.getSequenceNum());

            File dcmFile = new File(SavePath.rootPath+sequence.getDcmPath());
            DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
            Hospital hospital = IniUtil.getHospital(dcmInfo.getHospitalName());
            StringBuilder inspePath = new StringBuilder();
            if (sequence.getSequenceName().equals("Casereport"))
                inspePath.append("ZJUFAH");
            else if (hospital == null) break;
            else inspePath.append(hospital.getHospitalShortName());
            inspePath.append("_");
            inspePath.append(dcmInfo.getEnglishName());
            inspePath.append("_");
            inspePath.append(dcmInfo.getCTNum());
            inspePath.append("_");
            inspePath.append(dcmInfo.getPatientID());
            inspePath.append("\\");
            inspePath.append(sequence.getSequenceNum());
            String src = srcPath.toString();
            String des = desPath + inspePath;
            copy(src, des);
        }
    }

    //复制方法
    private static void copy(String srcPath, String desPath) {
        System.out.println(srcPath);
        System.out.println(desPath);
        File src = new File(srcPath);
        File des = new File(desPath);
        if(!des.exists()) des.mkdirs();
        for (File file : src.listFiles()) {
            String fileName = file.getName();
            if(file.isFile() && !fileName.equals("matrix.bin") && !fileName.equals("info.xml")){
                FileUtil.copy(file.getAbsolutePath(),desPath+"\\"+fileName); //调用文件拷贝的方法
            } else if(file.isDirectory()) {
                copy(file.getAbsolutePath(),desPath+"\\"+fileName);//继续调用复制方法
            }
        }
    }
}
