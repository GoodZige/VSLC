package com.vslc.tools.xml;

import com.vslc.enums.MaskTypeEnum;
import com.vslc.model.DcmInfo;
import com.vslc.model.Sequence;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chenlele
 * 2018/7/31 17:23
 */
public class InfoXml {

    /**
     * 遍历医院路径 加载xml配置文件（好耗时）
     * @param hospPath
     */
    public static void ergodicHosp(String hospPath) {
        File hospDir = new File(hospPath);
        for (File inspeDir : hospDir.listFiles()) {
            ergodicInspe(inspeDir);
        }
    }

    /**
     * 选择一次检查生成xml配置文件（常用）
     * @param inspeDir
     */
    public static void ergodicInspe(File inspeDir) {
        File[] types = inspeDir.listFiles();
        Map<String, Object> inspeParam = new HashMap<>();
        List<Sequence> sequenceList = new ArrayList<>();
        //遍历检查下的序列
        for (File seDir : types) {
            String seName = seDir.getName();
            if (!seName.equals("NotDcm") && !seName.equals("info.xml")) {
                Map<String, Object> seParam = new HashMap<>();
                String sePath = seDir.getAbsolutePath();
                File dcmDir = new File(sePath+"\\DCM");
                File maskDir = new File(sePath+"\\MASK");
                File[] dcms = dcmDir.listFiles();
                File[] masks = maskDir.listFiles();
                List<String> fileNames = new ArrayList<>();
                List<MaskTypeEnum> maskTypes = new ArrayList<>();
                DcmInfo dcmInfo = null;
                for (File dcm : dcms) {
                    if (dcmInfo == null) dcmInfo = new DcmInfo(dcm, false);
                    fileNames.add(dcm.getName());
                }
                if (maskDir.exists() && masks.length > 0) {
                    for (File mask : masks) {
                        String fileName = mask.getName();
                        for (MaskTypeEnum maskTypeEnum : MaskTypeEnum.values()) {
                            if (maskTypeEnum.getName().equals(fileName.substring(0, fileName.indexOf(".")))) {
                                maskTypes.add(maskTypeEnum);
                            }
                        }
                    }
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                StringBuilder patientTag = new StringBuilder();
                patientTag.append(dcmInfo.getEnglishName());
                patientTag.append("_");
                patientTag.append(dcmInfo.getCTNum());
                if (dcmInfo.getPatientBirthday() != null) {
                    patientTag.append(".");
                    patientTag.append(dateFormat.format(dcmInfo.getPatientBirthday()));
                }
                patientTag.append(".");
                patientTag.append(dcmInfo.getPatientSex()==0?"F":"M");
                seParam.put("files", fileNames);
                seParam.put("maskTypes", maskTypes);
                seParam.put("patientTag", patientTag.toString());
                seParam.put("serialId", dcmInfo.getSeriesId());
                Sequence sequence = new Sequence();
                sequence.setSequenceName(dcmInfo.getSeriesId());
                sequence.setSequenceNum(dcmInfo.getSeriesNum());
                sequenceList.add(sequence);
                inspeParam.put("patientTag", patientTag.toString());

                seParam.put("z", Integer.toString(dcms.length));
                seParam.put("x", Integer.toString(dcmInfo.getColumns()));
                seParam.put("y", Integer.toString(dcmInfo.getRows()));
                InfoXml.createSequence(new File(sePath+"\\info.xml"), seParam);
            }
        }
        String inspePath = inspeDir.getAbsolutePath();
        inspeParam.put("path", inspePath);
        inspeParam.put("sequenceList", sequenceList);
        InfoXml.createInspection(new File(inspePath+"\\info.xml"), inspeParam);
    }

    /**
     * 检查级别配置文件
     * @param dest
     * @param param
     */
    public static void createInspection(File dest, Map<String, Object> param) {
        //创建Document对象
        Document document = DocumentHelper.createDocument();

        //创建patient根节点
        Element patientEle = document.addElement("patient");
        patientEle.addAttribute("Tag", (String) param.get("patientTag"));
        patientEle.addAttribute("path", (String) param.get("path"));

        List<Sequence> sequenceList = (List<Sequence>) param.get("sequenceList");
        //创建serials子节点
        Element serialsEle = patientEle.addElement("serials");
        serialsEle.addAttribute("size", Integer.toString(sequenceList.size()));

        //创建serial子节点
        for (Sequence sequence : sequenceList) {
            Element serialEle = serialsEle.addElement("serial");
            serialEle.addAttribute("id", sequence.getSequenceNum());
        }
        XmlUtil.writeXML(dest, document);
    }

    /**
     * 序列级别配置文件
     * @param dest
     * @param param
     */
    public static void createSequence(File dest, Map<String, Object> param) {
        Document document = DocumentHelper.createDocument();

        Element serialEle = document.addElement("serial");
        serialEle.addAttribute("id", (String) param.get("serialId"));
        serialEle.addAttribute("patientTag", (String) param.get("patientTag"));
        serialEle.addAttribute("path", dest.getParent());

        Element originEle = serialEle.addElement("origin");
        List<String> files = (List<String>) param.get("files");
        files.sort((fileName1, fileName2) -> {
            int index1 = Integer.valueOf(fileName1.substring(0, fileName1.indexOf(".")));
            int index2 = Integer.valueOf(fileName2.substring(0, fileName2.indexOf(".")));
            return index1 - index2;
        });
        originEle.addAttribute("size", Integer.toString(files.size()));

        for (String fileName : files) {
            Element originFileEle = originEle.addElement("file");
            originFileEle.addAttribute("path", "DCM\\" + fileName);
        }

        Element maskEle = serialEle.addElement("mask");
        maskEle.addAttribute("size", Integer.toString(MaskTypeEnum.values().length));
        maskEle.addAttribute("x", (String) param.get("x"));
        maskEle.addAttribute("y", (String) param.get("y"));
        maskEle.addAttribute("z", (String) param.get("z"));

        List<MaskTypeEnum> maskTypeEnumList = (List<MaskTypeEnum>) param.get("maskTypes");
        for (MaskTypeEnum maskTypeEnum : MaskTypeEnum.values()) {
            Element maskFileEle = maskEle.addElement("file");
            maskFileEle.addAttribute("type", maskTypeEnum.getName());
            maskFileEle.addAttribute("visible", "0");
            boolean exist = false;
            for (MaskTypeEnum m : maskTypeEnumList) {
                if (m.getName().equals(maskTypeEnum.getName())) exist = true;
            }
            if (exist) maskFileEle.addAttribute("size", (String) param.get("z"));
            else maskFileEle.addAttribute("size", "0");
            maskFileEle.addAttribute("path", "MASK\\"+maskTypeEnum.getName()+".bin");
        }
        XmlUtil.writeXML(dest, document);
    }

    /**
     * 获取图像三维尺寸
     * @param xmlFile
     * @return
     */
    public static Map<String, Integer> getImageSize(File xmlFile) {
        Map<String, Integer> result = new HashMap<>();
        Document document = XmlUtil.getDocument(xmlFile);
        Element serialEle = document.getRootElement();
        Element maskEle = serialEle.element("mask");
        result.put("x", Integer.valueOf(maskEle.attributeValue("x")));
        result.put("y", Integer.valueOf(maskEle.attributeValue("y")));
        result.put("z", Integer.valueOf(maskEle.attributeValue("z")));
        return result;
    }
}
