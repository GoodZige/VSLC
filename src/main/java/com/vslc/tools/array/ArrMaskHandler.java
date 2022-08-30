package com.vslc.tools.array;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.vslc.model.DcmInfo;
import com.vslc.tools.FileUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * 整理旧mask（全部整理完毕 没用）
 * Created by chenlele
 * 2018/4/27 15:17
 */
public class ArrMaskHandler {

    private DcmInfo pwdDcmInfo;

    private List<DcmInfo> dcmInfoList;

    private static boolean isDcm(File file) {
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return (suffix.equals("ima") || suffix.equals("dcm") || suffix.equals("sr"));
    }

    public List<DcmInfo> arrMany(String inputPath, String outputPath) {
        File[] files = new File(inputPath).listFiles();
        dcmInfoList = new ArrayList<>();
        int index = 0;
        for (File file : files) {
            String dirPath = file.getAbsolutePath();
            String dirName = dirPath.substring(dirPath.lastIndexOf("\\") + 1, dirPath.length());
            System.out.println(dirName);
            DcmInfo dcmInfo = arrSingle(file.getAbsolutePath(), outputPath);
            dcmInfoList.add(dcmInfo);
            System.out.println(++index);
        }
        return dcmInfoList;
    }

    public DcmInfo arrSingle(String inputPath, String outputPath) {
        File[] files = new File(inputPath).listFiles();
        for (File file : files) {
            if (isDcm(file)) {
                pwdDcmInfo = new DcmInfo(file, false);
                break;
            }
        }
        ArrDataHandler.mkdirByDcm(outputPath, pwdDcmInfo);
        StringBuilder sb;
        int dcmSum = 0;
        for (File file : files) {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            sb = new StringBuilder(outputPath);
            sb.append("\\");
            sb.append(pwdDcmInfo.getHospitalName());
            sb.append("\\");
            sb.append(pwdDcmInfo.getStudyId());
            if (dcmSum == 0)
                pwdDcmInfo.setSavePath(sb.toString().substring(3));
            sb.append("\\");
            sb.append(pwdDcmInfo.getSeriesNum());

            if (isDcm(file)) {
                sb.append("\\DCM\\");
                sb.append(new DcmInfo(file, false).getImgIndex());
                sb.append(".");
                if (suffix.toUpperCase().equals("IMA")) {
                    File dFile = new File(sb.toString()+"dcm");
                    if (dFile.exists()) dFile.delete();
                }
                sb.append(suffix);
                if (dcmSum == 0)
                    pwdDcmInfo.setDcmPath(sb.toString().substring(3));
                dcmSum++;
            } else {
                sb.append("\\BIN\\");
                sb.append(file.getName());
            }
            FileUtil.copy(file.getAbsolutePath(), sb.toString());
        }
        pwdDcmInfo.setFileNum(dcmSum);
        return pwdDcmInfo;
    }

    private static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
