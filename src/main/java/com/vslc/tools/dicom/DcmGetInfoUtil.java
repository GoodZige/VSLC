package com.vslc.tools.dicom;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.vslc.model.DcmInfo;

/**
 * 遍历目录使用
 */
public class DcmGetInfoUtil {

    private List<DcmInfo> dcmInfoList = new ArrayList<>();

    private boolean isCasereport = false; //报告单

    private boolean isLKDS = false; //是否是天池数据

    private DcmInfo lastDcmInfo;

    public List<DcmInfo> readByHosp(String hospPath, String hospName) {
        File hospDir = new File(hospPath);
        System.out.println("reading: " + hospDir.getName());
        if (hospName.equals("LKDS") || hospName.equals("LIDC-IDRI")) {
            isLKDS = true;
        }
        //遍历医院目录下所有检查信息
        int index = 1;
        for (File childDir : hospDir.listFiles()) {
            System.out.println("reading: "+childDir.getAbsolutePath());
            ergodicInspection(childDir, hospName);
            System.out.println("已读: " + index);
            index++;
        }
        isLKDS = false;
        System.out.println(hospDir.getName() + " --> Finished");
        return dcmInfoList;
    }

    public List<DcmInfo> ergodicInspection(File inspeDir, String hospName) {
        File[] files = inspeDir.listFiles();
        File file0 = new File(inspeDir.getAbsoluteFile() + "\\0");
        if (file0.exists())
            if (hospName.equals("ZJUFAH"))
                isCasereport = true;
        int index = 0;
        for (File childDir : files) {
            String fileName  = childDir.getName();
            if (!fileName.equals("NotDcm")&&!fileName.equals("null")&&!fileName.equals("1")&&!fileName.equals("info.xml")) {
                if (hospName.equals("ZJUFAH")) {
                    if (!fileName.equals("0")) {
                        ergodicSeries(childDir);
                        index++;
                    }
                } else {
                    ergodicSeries(childDir);
                    index++;
                }
            }
        }
        if (index > 0) {
            if (isCasereport) {
                dcmInfoList.add(lastDcmInfo);
                isCasereport = false;
            }
        }
        File file1 = new File(inspeDir.getAbsoluteFile() + "\\1");
        if (file1.exists()) ergodicSeries(file1);
        return dcmInfoList;
    }

    private void ergodicSeries(File sChildDir) {
        File[] files = sChildDir.listFiles();
        for (File childDir : files) {
            String fileName  = childDir.getName();
            //读取DCM路径下的dicom文件
            if (fileName.equals("DCM")) {
                if (isLKDS)
                    readLKDS(childDir);
                else
                    readDcm(childDir);
            }
        }
    }

    private void readLKDS(File childDir) {
        String lkdsName = childDir.getParentFile().getParentFile().getName();
        File[] files = childDir.listFiles();
        List<File> fileList = new ArrayList<>();
        for (File dcmFile : files) {
            fileList.add(dcmFile);
        }
        fileList.sort(new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                String fileName1 = file1.getName();
                String fileName2 = file2.getName();
                int index1 = Integer.valueOf(fileName1.substring(0, fileName1.lastIndexOf(".")));
                int index2 = Integer.valueOf(fileName2.substring(0, fileName2.lastIndexOf(".")));
                return index1 - index2;
            }
        });
        for (File dcmFile : fileList) {
            String path = childDir.getParentFile().getParentFile().getAbsolutePath();
            String dcmPath = dcmFile.getAbsolutePath();
            DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
            dcmInfo.setStudyId(lkdsName);
            dcmInfo.setCTNum(lkdsName);
            dcmInfo.setFileNum(files.length);
            dcmInfo.setSavePath(path.substring(3));
            dcmInfo.setDcmPath(dcmPath.substring(3));
            dcmInfo.setEnglishName(lkdsName);
            dcmInfo.setPatientID(lkdsName);
            dcmInfo.setPatientSex(null);
            dcmInfo.setPatientBirthday(null);
            dcmInfoList.add(dcmInfo);
            break;
        }
    }

    private void readDcm(File childDir) {
        File[] files = childDir.listFiles();
        List<File> fileList = new ArrayList<>();
        for (File dcmFile : files) {
            fileList.add(dcmFile);
        }
        fileList.sort(new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                String fileName1 = file1.getName();
                String fileName2 = file2.getName();
                int index1 = Integer.valueOf(fileName1.substring(0, fileName1.lastIndexOf(".")));
                int index2 = Integer.valueOf(fileName2.substring(0, fileName2.lastIndexOf(".")));
                return index1 - index2;
            }
        });
        for (File dcmFile : fileList) {
            String path = childDir.getParentFile().getParentFile().getAbsolutePath();
            String dcmPath = dcmFile.getAbsolutePath();
            DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
            dcmInfo.setFileNum(files.length);
            dcmInfo.setSavePath(path.substring(3));
            dcmInfo.setDcmPath(dcmPath.substring(3));
            dcmInfoList.add(dcmInfo);
            if (isCasereport) {
                //手动添加检查单序列
                lastDcmInfo = new DcmInfo(dcmFile, false);
                lastDcmInfo.setFileNum(1);
                lastDcmInfo.setSeriesName("Casereport");
                lastDcmInfo.setThickness(null);
                StringBuilder sb = new StringBuilder(dcmPath.substring(3));
                lastDcmInfo.setDcmPath(sb.replace(dcmPath.length()-11, dcmPath.length()-10, "0").toString());
                lastDcmInfo.setSeriesNum("0");
            }
            break;
        }
    }
}
