package com.vslc.tools.dicom;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DcmInfoReader {

    /**
     * 读取斜率值
     * @param dcmData
     * @return
     */
    public static float readSlope(HashMap<String,Object> dcmData) {
        String Val = (String)dcmData.get("0028,1053");
        if(Val ==null || Val.equals(""))return 1;
        return Float.valueOf(Val);
    }

    /**
     * 读取截距值
     * @param dcmData
     * @return
     */
    public static float readIntercept(HashMap<String,Object> dcmData) {
        String Val = (String)dcmData.get("0028,1052");
        if(Val ==null || Val.equals(""))return 0;
        return Float.valueOf(Val);
    }

    /**
     * 读取最小像素值
     * @param dcmData
     * @return
     */
    public static int readMinVal(HashMap<String,Object> dcmData) {
        String minVal = (String)dcmData.get("0028,0106");
        if(minVal ==null || minVal.equals(""))return 0;
        return Integer.valueOf(minVal);
    }

    /**
     * 读取最大像素值
     * @param dcmData
     * @return
     */
    public static int readMaxVal(HashMap<String,Object> dcmData) {
        String maxVal = (String) dcmData.get("0028,0107");
        if(maxVal ==null || maxVal.equals("")) return 4096;
        return Integer.valueOf(maxVal);
    }

    /**
     * 读取医院名字
     * @param dcmData
     * @return
     */
    public static String readHospitalName(HashMap<String,Object> dcmData) {
        String hospital = (String)dcmData.get("0008,0080");
        if(hospital==null || hospital.equals(""))hospital = "unknow";
        return hospital.replaceAll(" ", "_");
    }

    /**
     * 读取病人名字
     * @param dcmData
     * @return
     */
    public static String readPaitentName(HashMap<String,Object> dcmData) {
        String tmp =  (String)dcmData.get("0010,0010");
        if (tmp==null || tmp.equals("") || tmp.length()<2)return "unknow";
        int isP2= tmp.indexOf("/");	//是否为第二种情况
        int isP1=tmp.toUpperCase().indexOf("CT");
        if (isP2==-1 && isP1!=-1) { //第一种情况
            String [] tmps = tmp.split(" ");
            String endTmp = tmps[tmps.length-1];
            int i = tmps.length;
            if(endTmp.length()<2) return "unknow";
            String last = "";
            if (endTmp.length() > 1) last = endTmp.substring(0, 2);
            if(last.toUpperCase().equals("CT")) {	//判断最后一段是不是CT开头
                i--;
            }
            StringBuffer sb = new StringBuffer();
            for(int j=0;j<i;j++) {
                if(j==0) {
                    sb.append(tmps[j]);
                } else {
                    sb.append("_");
                    sb.append(tmps[j]);
                }
            }
            return sb.toString();
        } else if(isP2!=-1 && isP1==-1) {
            String [] tmps = tmp.split("/");
            return tmps[1].replaceAll(" ", "_");
        } else {
            return tmp.replaceAll(" ", "_");
        }
    }

    /**
     * 读取CT号
     * @param dcmData
     * @return
     */
    public static String readCTNum(HashMap<String,Object> dcmData) {
        String name =  (String)dcmData.get("0010,0010");
        if(name==null || name.equals("")) return "unknow";
        String [] tmps = name.split(" ");
        String endTmp = tmps[tmps.length-1];
        String last = "";
        if (endTmp.length() > 1)
            last = endTmp.substring(0, 2);
        if(last.toUpperCase().equals("CT")) { //判断最后一段是不是CT开头
            return endTmp;
        } else {
            String ac =  (String)dcmData.get("0008,0050");
            if (ac != "" && ac != null) {
                int isP1 = ac.toUpperCase().indexOf("CT");
                if(isP1!=-1) {
                    return ac;
                } else {
                    return "CT"+ac;
                }
            } else {
                return null;
            }
        }
    }

    public static Date readInspectTime(HashMap<String,Object> dcmData) throws ParseException {
        SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String contentDate = (String) dcmData.get("0008,0023");
        String contentTime = (String) dcmData.get("0008,0033");
        String studyDate = (String) dcmData.get("0008,0020");
        String studyTime = (String) dcmData.get("0008,0030");
        StringBuilder tempTime;
        if (contentDate != null && contentTime != null && !contentDate.equals("") && !contentTime.equals("")) {
            tempTime = new StringBuilder(contentDate + contentTime);
            if (contentDate.length() == 8) {
                tempTime.insert(4, "-");
                tempTime.insert(7, "-");
                tempTime.insert(10, " ");
                tempTime.insert(13, ":");
                tempTime.insert(16, ":");
            } else {
                tempTime.insert(10, " ");
                tempTime.insert(13, ":");
                tempTime.insert(16, ":");
            }
        } else if (studyDate != null && studyTime != null && !studyDate.equals("") && !studyTime.equals("")) {
            tempTime = new StringBuilder(studyDate + studyTime);
            tempTime.insert(4, "-");
            tempTime.insert(7, "-");
            tempTime.insert(10, " ");
            tempTime.insert(13, ":");
            tempTime.insert(16, ":");
        } else {
            tempTime = new StringBuilder(contentDate);
            tempTime.insert(4, "-");
            tempTime.insert(7, "-");
            tempTime.append(" 00:00:00");
        }
        Date inspectTime = timeSdf.parse(tempTime.toString());
        return inspectTime;
    }

    public static Date readPatientBirthday(HashMap<String,Object> dcmData) throws ParseException {
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = (String) dcmData.get("0010,0030");
        if (date.equals("")) {
            return null;
        } else {
            StringBuilder tempDate = new StringBuilder(date);
            tempDate.insert(4, "-");
            tempDate.insert(7, "-");
            return dateSdf.parse(tempDate.toString());
        }
    }

    public static int readPatientSex(HashMap<String,Object> dcmData) {
        String readSex = (String) dcmData.get("0010,0040");
        int patientSex;
        if (readSex!= null && readSex.equals("F")) {
            patientSex = 0;
        } else {
            patientSex = 1;
        }
        return patientSex;
    }

    public static String readModality(HashMap<String, Object> dcmData) {
        return (String) dcmData.get("0008,0060");
    }

    /**
     * 读取病人ID
     * @param dcmData
     * @return
     */
    public static String readPaitentID(HashMap<String,Object> dcmData) {
        String ret = (String)dcmData.get("0010,0020");
        if(ret==null || ret.equals("") )return "FFFFFFF";
        String fir = ret.substring(0, 1).toUpperCase();
        if(fir.equals("P") || fir.equals("T")) {
            return ret;
        } else {
            return "P"+ret;
        }
    }

    /**
     * 读取序列索引
     * @param dcmData
     * @return
     */
    public static String readSeriesNum(HashMap<String,Object> dcmData) {
        String seriesNum = (String)dcmData.get("0020,0011");
        if (seriesNum != null && !seriesNum.equals("")) return seriesNum;
        return "0";
    }

    /**
     * 读取序列名称
     * @param dcmData
     * @return
     */
    public static String readSeriesName(HashMap<String,Object> dcmData) {
        String ret = (String)dcmData.get("0008,103e");
        if(ret==null || ret.equals(""))return "";
        return ret.replaceAll(" ", "_");
    }

    /**
     * 判断是否为Dcm文件
     * @param dcmData
     * @return
     */
    public static boolean readIsDcm(HashMap<String,Object> dcmData) {
        return (boolean)dcmData.get("isDcm");
    }

    /**
     * 读取图像矩阵
     * @param dcmData
     * @return
     */
    public static short[][] readImgArr(HashMap<String,Object> dcmData){
        return (short[][])dcmData.get("imgArr");
    }

    /**
     * 读取图像序号
     * @param dcmData
     * @return
     */
    public static int readImgIndex(HashMap<String,Object> dcmData) {
        String sdata = (String) dcmData.get("0020,0013");
        if(sdata==null || sdata.equals("")) return 0;
        else return Integer.valueOf(sdata);
    }

    /**
     * 读取层厚
     * @param dcmData
     * @return
     */
    public static Float readThickness(HashMap<String,Object> dcmData) {
        String val = (String) dcmData.get("0018,0050");
        if(val==null || val.equals("")) {
            return null;
        } else {
            Float f= Float.valueOf(val);
            BigDecimal b = new BigDecimal(f);
            Float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            return f1;
        }
    }

    public static String readSdID(HashMap<String,Object> dcmData) {
        String val = (String)dcmData.get("0020,000d");
        if(val==null || val =="") return "unknow";
        return val;
    }


    public static String readSrID(HashMap<String,Object> dcmData) {
        String val = (String)dcmData.get("0020,000e");
        if(val==null || val =="") return "unknow";
        return val;
    }

    /**
     * 读取行数
     * @param dcmData
     * @return
     */
    public static int readRows(HashMap<String,Object> dcmData) {
        String rows  = (String) dcmData.get("0028,0010");
        if(rows==null || rows.equals("")) return 0;
        return Integer.valueOf(rows);
    }

    /**
     * 读取列数
     * @param dcmData
     * @return
     */
    public static int readColumns(HashMap<String,Object> dcmData) {
        String cols  = (String) dcmData.get("0028,0011");
        if(cols==null || cols.equals("")) return 0;
        return Integer.valueOf(cols);
    }

    /**
     * 读取肺窗窗位
     * @param dcmData
     * @return
     */
    public static Integer readLungWindowCenter(HashMap<String,Object> dcmData) {
        String sWindowCenter = (String) dcmData.get("0028,1050");
        if(sWindowCenter==null || sWindowCenter.equals(""))
            return -650;
        sWindowCenter = sWindowCenter.replaceAll("\\\\", " ");
        String [] sp =  sWindowCenter.split(" ");
        if(sp.length==1) {
            return (int) Math.floor(Double.valueOf(sp[0]));
        } else {
            int fir = (int) Math.floor(Double.valueOf(sp[0]));
            int sec = (int) Math.floor(Double.valueOf(sp[1]));
            return (fir < sec ? fir : sec);
        }
    }

    /**
     * 读取肺窗窗宽
     * @param dcmData
     * @return
     */
    public static Integer readLungWindowWidth(HashMap<String,Object> dcmData) {
        String sWindowWidth = (String) dcmData.get("0028,1051");
        if(sWindowWidth==null || sWindowWidth.equals(""))
            return 1000;
        sWindowWidth = sWindowWidth.replaceAll("\\\\", " ");
        String [] sp =  sWindowWidth.split(" ");
        if(sp.length==1) {
            return (int) Math.floor(Double.valueOf(sp[0]));
        } else {
            int fir = (int) Math.floor(Double.valueOf(sp[0]));
            int sec = (int) Math.floor(Double.valueOf(sp[1]));
            return (fir > sec ? fir : sec);
        }
    }

    public static String readTransferId(HashMap<String,Object> dcmData) {
        return (String) dcmData.get("0002,0010");
    }

    public static double readColumnPixelSpacing(HashMap<String,Object> dcmData) {
        String pixelSpacing = (String) dcmData.get("0028,0030");
        if(pixelSpacing != null && !pixelSpacing.equals("")) {
            String[] values = pixelSpacing.split("\\\\");
            double result = Double.valueOf(values[0]);
            return result;
        } else return 0;
    }

    public static double readRowPixelSpacing(HashMap<String,Object> dcmData) {
        String pixelSpacing = (String) dcmData.get("0028,0030");
        if(pixelSpacing != null && !pixelSpacing.equals("")) {
            String[] values = pixelSpacing.split("\\\\");
            double result = Double.valueOf(values[1]);
            return result;
        } else return 0;
    }

    public static double readXPosition(HashMap<String,Object> dcmData) {
        String position = (String) dcmData.get("0020,0032");
        if(position != null && !position.equals("")) {
            String[] values = position.split("\\\\");
            double result = Double.valueOf(values[0]);
            return result;
        } else return 0;
    }

    public static double readYPosition(HashMap<String,Object> dcmData) {
        String position = (String) dcmData.get("0020,0032");
        if(position != null && !position.equals("")) {
            String[] values = position.split("\\\\");
            double result = Double.valueOf(values[1]);
            return result;
        } else return 0;
    }

    public static double readZPosition(HashMap<String,Object> dcmData) {
        String position = (String) dcmData.get("0020,0032");
        if(position != null && !position.equals("")) {
            String[] values = position.split("\\\\");
            double result = Double.valueOf(values[2]);
            return result;
        } else return 0;
    }

    public static int readBitsStored(HashMap<String,Object> dcmData) {
        String bitsStored = (String) dcmData.get("0028,0101");
        if(bitsStored != null && !bitsStored.equals(""))
            return Integer.valueOf(bitsStored);
        else
            return 0;
    }
}
