package com.vslc.tools;

import com.vslc.model.Hospital;
import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 配置文件工具类
 * Created by chenlele
 * 2018/6/8 13:47
 */
public class IniUtil {
    //逻辑表达式
    private static final String LOGICAL_INI = SavePath.iniPath + "logical.ini";
    //医院
    private static final String HOSPITAL_INI = SavePath.iniPath + "hospital.ini";

    /**
     * 逻辑表达式转义
     * @param logical
     * @return
     */
    public static String logicalTransfer(String logical) {
        try {
            Wini ini = new Wini(new File(LOGICAL_INI));
            Set<String> set = ini.keySet();
            Map<String, String> map;
            for (String section : set) {
                map = ini.get(section);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (logical.contains(entry.getKey()))
                        logical = logical.replaceAll(entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logical;
    }

    /**
     * 根据dicom读取的医院名获取数据库中的医院标识
     * @param hospitalName 医院名（dicom读取）
     * @return
     */
    public static Hospital getHospital(String hospitalName) {
        try {
            Wini ini = new Wini(new File(HOSPITAL_INI));
            Set<String> set = ini.keySet();
            for (String section : set) {
                Map<String, String> map = ini.get(section);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (hospitalName.equals(entry.getKey())) {
                        Hospital hospital = new Hospital();
                        hospital.setHospitalID(entry.getValue());
                        hospital.setHospitalShortName(section);
                        return hospital;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 如果配置文件里没有该医院则写入(用户填入 前端保证)
     * @param hospital
     */
    public static void updateHospital(Hospital hospital) {
        try {
            Wini ini = new Wini(new File(HOSPITAL_INI));
            String hospShortName = hospital.getHospitalShortName();
            String hospName = hospital.getHospitalName();
            String hospId = hospital.getHospitalID();
            Ini.Section section = ini.get(hospital.getHospitalShortName());
            if (section.get(hospital.getHospitalName()) == null) {
                ini.put(hospShortName, hospName, hospId);
                ini.store();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
