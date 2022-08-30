package com.vslc.tools.array;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.vslc.model.DcmInfo;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * 导入数据时文件整理
 */
public class ArrDataHandler {

    public static boolean isDcm(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return (suffix.equals("ima") || suffix.equals("dcm") && !fileName.equals("exported0000.dcm"));
    }

    public static boolean isMask(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return suffix.equals("bin");
    }

    public static void mkdirByDcm(String storagePath, DcmInfo dcmInfo) {
        StringBuilder sb = new StringBuilder(storagePath);
        String hospitalName = dcmInfo.getHospitalName();
        String sdID = dcmInfo.getStudyId();
        String curSeriesNum = dcmInfo.getSeriesNum();
        sb.append("\\");
        sb.append(hospitalName);
        sb.append("\\");
        sb.append(sdID);
        //创建检查级文件夹
        File outRoot = new File(sb.toString());
        if(!outRoot.exists())outRoot.mkdirs();
        //创建非dcm文件夹
        File outNotDcmFolder = new File(sb.toString()+"\\NotDcm");
        if(!outNotDcmFolder.exists())outNotDcmFolder.mkdirs();
        sb.append("\\");
        sb.append(curSeriesNum);
        //DCM文件夹
        File outSeriesFolder = new File(sb.toString()+"\\DCM");
        if(!outSeriesFolder.exists())outSeriesFolder.mkdirs();
        //勾画矩阵文件夹
        outSeriesFolder = new File(sb.toString()+"\\MASK");
        if(!outSeriesFolder.exists())outSeriesFolder.mkdirs();
    }

    public static void findLocalAndWrite(MultipartFile mf, String storagePath, DcmInfo pwdDcmInfo) throws IOException {
        CommonsMultipartFile cf = (CommonsMultipartFile)mf;
        DiskFileItem fi = (DiskFileItem) cf.getFileItem();
        File file = fi.getStoreLocation();
        StringBuilder sb = new StringBuilder(storagePath);
        if(isDcm(mf)) {
            DcmInfo dcmInfo = new DcmInfo(file,false);
            ArrDataHandler.mkdirByDcm(storagePath, dcmInfo);
            sb.append(dcmInfo.getHospitalName());
            sb.append("\\");
            sb.append(dcmInfo.getStudyId());
            sb.append("\\");
            sb.append(dcmInfo.getSeriesNum());
            sb.append("\\DCM\\");
            sb.append(dcmInfo.getImgIndex());
            String fileName = mf.getOriginalFilename();
            sb.append(fileName.substring(fileName.lastIndexOf(".")));
            copyMulFile(mf, sb.toString());
        }
//        else if (isMask(mf)) {
//            sb.append(pwdDcmInfo.getHospitalName());
//            sb.append("\\");
//            sb.append(pwdDcmInfo.getStudyId());
//            sb.append("\\");
//            sb.append(pwdDcmInfo.getSeriesNum());
//            sb.append("\\MASK\\");
//            sb.append(mf.getOriginalFilename());
//            copyMulFile(mf, sb.toString());
//        }
    }

    public static DcmInfo getDcmInfo(List<MultipartFile> files) {
        DcmInfo dcmInfo = null;
        for (MultipartFile mf : files) {
            if(ArrDataHandler.isDcm(mf)) {
                CommonsMultipartFile cf = (CommonsMultipartFile) mf;
                DiskFileItem fi = (DiskFileItem) cf.getFileItem();
                File dcmFile = fi.getStoreLocation();
                dcmInfo = new DcmInfo(dcmFile,false);
                break;
            }
        }
        return dcmInfo;
    }

    public static void copyMulFile(MultipartFile mf, String targetFolder) {
        try {
            //读取文件
            BufferedInputStream bis = new BufferedInputStream(mf.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFolder));
            int len;
            byte[] buffer = new byte[10240];
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            //刷新此缓冲的输出流，保证数据全部都能写出
            bos.flush();
            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
