package com.vslc.tools.dicom;

import com.vslc.tools.BitConverter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class DcmHandler {

    public static String getVF(String VR, byte[] VF) {
        String VFStr = "";
        switch (VR) {
            case "SS":
                if(VF.length<2) {VFStr="";break;}
                VFStr = String.valueOf(BitConverter.toShort(VF));
                break;
            case "US":
                if(VF.length<2) {VFStr="";break;}
                VFStr = String.valueOf(BitConverter.toUShort(VF));
                break;
            case "SL":
                if(VF.length<4) {VFStr="";break;}
                VFStr = String.valueOf(BitConverter.toInt(VF));
                break;
            case "UL":
                if(VF.length<4) {VFStr="";break;}
                VFStr = String.valueOf(BitConverter.toUInt(VF));
                break;
            case "AT":
                if(VF.length<2) {VFStr="";break;}
                VFStr = String.valueOf(BitConverter.toUShort(VF));
                break;
            case "FL":
                if(VF.length<4) {VFStr="";break;}
                VFStr = String.valueOf(BitConverter.toFloat(VF));
                break;
            case "FD":
                if(VF.length<8) {VFStr="";break;}
                VFStr = String.valueOf(BitConverter.toDouble(VF));
                break;
            case "OB":
                try {
                    VFStr = new String(VF,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "OW":
                try {
                    VFStr = new String(VF,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "SQ":
                try {
                    VFStr = new String(VF,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "OF":
                try {
                    VFStr = new String(VF,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "UT":
                try {
                    VFStr = new String(VF,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "UN":
                try {
                    VFStr = new String(VF,"UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                break;
            default:
                try {
                    VFStr = new String(VF,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
        return VFStr;
    }

    public static String getVR(String tag) {
        switch (tag) {
            case "0002,0000"://文件元信息长度
                return "UL";
            case "0002,0010"://传输语法
                return "UI";
            case "0002,0013"://文件生成程序的标题
                return "SH";
            case "0008,0005"://文本编码
                return "CS";
            case "0008,0008":
                return "CS";
            case "0008,1032"://成像时间
                return "SQ";
            case "0008,1111":
                return "SQ";
            case "0008,0020"://检查日期
                return "DA";
            case "0008,0060"://成像仪器
                return "CS";
            case "0008,0070"://成像仪厂商
                return "LO";
            case "0008,0080":
                return "LO";
            case "0010,0010"://病人姓名
                return "PN";
            case "0010,0020"://病人id
                return "LO";
            case "0010,0030"://病人生日
                return "DA";
            case "0018,0060"://电压
                return "DS";
            case "0018,1030"://协议名
                return "LO";
            case "0018,1151":
                return "IS";
            case "0020,0010"://检查ID
                return "SH";
            case "0020,0011"://序列
                return "IS";
            case "0020,0012"://成像编号
                return "IS";
            case "0020,0013"://影像编号
                return "IS";
            case "0020,0032"://position
                return "DS";
            case "0028,0002"://像素采样1为灰度3为彩色
                return "US";
            case "0028,0004"://图像模式MONOCHROME2为灰度
                return "CS";
            case "0028,0010"://row高
                return "US";
            case "0028,0011"://col宽
                return "US";
            case "0028,0100"://单个采样数据长度
                return "US";
            case "0028,0101"://实际长度
                return "US";
            case "0028,0102"://采样最大值
                return "US";
            case "0028,1050"://窗位
                return "DS";
            case "0028,1051"://窗宽
                return "DS";
            case "0028,1052":
                return "DS";
            case "0028,1053":
                return "DS";
            case "0028,0030"://像素大小
                return "DS";
            case "0040,0008"://文件夹标签
                return "SQ";
            case "0040,0260"://文件夹标签
                return "SQ";
            case "0040,0275"://文件夹标签
                return "SQ";
            case "7fe0,0010"://像素数据开始处
                return "OW";
            case "0028,0106":
                return "US";
            case "0028,0107":
                return "US";
            default:
                return "UN";
        }
    }

    public static HashMap<String,Object> undicom(File file, boolean readImg) {
        if(readImg) {
            String tempDirPath = file.getParentFile().getParent() + "\\temp\\";
            File tempDir = new File (tempDirPath);
            if(!tempDir.exists()) tempDir.mkdirs();
            String tempDcmPath = tempDirPath + file.getName();
            File tempDcm  = new File(tempDcmPath);
            Dcm2Dcm.main(new String[] {file.getAbsolutePath(), tempDcmPath});
            HashMap<String,Object> dcmData = handle(tempDcm, readImg);
            tempDcm.delete();
            return dcmData;
        } else {
            return handle(file, readImg);
        }
    }

    public static HashMap<String,Object> handle(File file, boolean readImg) {
        DataInputStream dicomDis;
        HashMap<String,Object> tags = new HashMap<>();
        //文件元信息
        boolean isLittleEndian = true; //是否小字节序（小端在前、大端在前）
        boolean isExplicitVR = true; //有无VR
        try {
            dicomDis = new DataInputStream(new FileInputStream(file));
            //导言
            dicomDis.skip(128);
            //判断是否为dicom文件
            String isDcm = readString(dicomDis,4);
            if(isDcm.equals("DICM")) {
                tags.put("isDcm", true);
            } else {
                tags.put("isDcm", false);
                dicomDis.close();
                return tags;
            }

            //循环读取所有tag
            boolean enDir = false;
            int leve = 0;
            StringBuffer folderData = new StringBuffer();//该死的文件夹标签
            String folderTag = "";
            byte[] byteTag =new byte[4];
            while(dicomDis.read(byteTag) == 4) {
                //无符号byte
                String tag1 =addZero(Integer.toHexString(byteTag[1] & 0xff)) + addZero(Integer.toHexString(byteTag[0] & 0xff));
                String tag2 =addZero(Integer.toHexString(byteTag[3] & 0xff)) + addZero(Integer.toHexString(byteTag[2] & 0xff));

                //读取当前tag
                String tag = tag1 + "," + tag2;

                String VR = "";
                long Len = 0;

                /**
                 * 读取VR跟Len
                 * 对OB OW SQ 要做特殊处理 先置两个字节0 然后4字节值长度
                 * 这些都是在读取VR一步被阻断的情况
                 */

                //文件头特殊情况
                if (tag.substring(0, 4).equals("0002")) {
                    VR = readString(dicomDis, 2);

                    if (VR.equals("OB") || VR.equals("OW") || VR.equals("SQ") || VR.equals("OF") || VR.equals("UT") || VR.equals("UN")) {
                        dicomDis.skip(2);
                        Len = readUInt(dicomDis);
                    } else
                        Len = readUShort(dicomDis);
                }
                //文件夹标签
                else if (tag.equals("fffe,e000")  || tag.equals("fffe,e00d") || tag.equals("fffe,e0dd")) {
                    VR = "**";
                    Len = readUInt(dicomDis);
                }
                //有无VR的情况
                else if (isExplicitVR == true) {
                    VR = readString(dicomDis, 2);

                    if (VR.equals("OB") || VR.equals("OW") || VR.equals("SQ") || VR.equals("OF") || VR.equals("UT") || VR.equals("UN")) {
                        dicomDis.skip(2);
                        Len = readUInt(dicomDis);
                    } else
                        Len = readUShort(dicomDis);
                }
                else if (isExplicitVR == false) {
                    VR = getVR(tag); //无显示VR时根据tag一个一个去找
                    Len = readUInt(dicomDis);
                }

                if(((int)Len) < 0 && !tag.equals("7fe0,0010")) {
                    continue;
                }

                //判断是否应该读取VF 以何种方式读取VF
                //这些都是在读取VF一步被阻断的情况
                byte[] VF = { 0x00 };

                //覆盖数据开始了
                if(tag.equals("6000,3000")) {
                    VR = "UL";
                    VF = BitConverter.toBytes(Len);
                    if(readImg) {
                        byte[] overlaydata = readBytes(dicomDis,(int)Len);
                        tags.put("overlaydata", overlaydata);
                    }
                }
                //图像数据开始了
                else if (tag.equals("7fe0,0010")) {
                    VR = "UL";
                    VF = BitConverter.toBytes(Len);

                    if(readImg) {

                        int colors; //颜色数 RGB为3 黑白为1
                        int cols;
                        int rows;
                        boolean signed;
                        int nHighBit;
                        int dataLen;
                        byte[] overlayData = (byte[]) tags.get("overlaydata");

                        colors = Integer.valueOf((String)tags.get("0028,0002"));
                        cols = Integer.valueOf((String)tags.get("0028,0011"));
                        rows = Integer.valueOf((String)tags.get("0028,0010"));
                        nHighBit = Integer.valueOf((String)tags.get("0028,0102"));
                        dataLen = Integer.valueOf((String)tags.get("0028,0100"));
                        String sSigned = (String)tags.get("0028,0103");
                        if(sSigned == null || sSigned.equals("") || sSigned.equals("0")) {
                            signed = false;
                        } else {
                            signed = true;
                        }

                        //对于RGB图像暂不做处理
                        if(colors == 1) {
                            short[][] imgArr = new short[cols][rows];
                            byte[] allImg = readBytes(dicomDis, dataLen / 8 * colors*cols*rows);
                            int index = 0;
                            int index2 =0;
                            for(int y = 0; y < rows; y++) {
                                for(int x = 0; x < cols; x++) {
                                    short nMask = (short)(0xffff << (nHighBit + 1));
                                    short nSignBit;
                                    byte[] pixData = new byte[]{allImg[index],allImg[index+1]};
                                    short pixValue = BitConverter.toShort(pixData);
                                    index+=2;
                                    //高低位转换和高位截取
                                    if(nHighBit <= 15 && nHighBit > 7) {
                                        if(!isLittleEndian) {
                                            byte tmp0 = pixData[0];
                                            byte tmp1 = pixData[1];
                                            pixData[0] = tmp1;
                                            pixData[1] = tmp0;
                                        }

                                        if(!signed) {
                                            pixValue = (short)((~nMask) & (BitConverter.toShort(pixData)));
                                        } else {
                                            nSignBit = (short)(1 << nHighBit);
                                            if (((BitConverter.toShort(pixData)) & nSignBit) != 0)
                                                pixValue = (short)(BitConverter.toShort(pixData) | nMask);
                                            else
                                                pixValue = (short)((~nMask) & (BitConverter.toShort(pixData)));
                                        }
                                    } else if(nHighBit <= 7) {
                                        // Unsigned integer
                                        if (signed == false) {
                                            nMask = (short)(0xffff << (nHighBit + 1));
                                            pixValue = (short)((~nMask) & (pixData[0]));
                                        }
                                        else {
                                            nMask = (short)(0xffff << (nHighBit + 1));
                                            nSignBit = (short)(1 << nHighBit);
                                            if (((pixData[0]) & nSignBit) != 0)
                                                pixValue = (short)((short)pixData[0] | nMask);
                                            else
                                                pixValue = (short)((~nMask) & (pixData[0]));
                                        }
                                    }

//                  			  //灰度计算
//                              if ((rescaleSlope != 1.0f) || (rescaleIntercept != 0.0f)) {
//                                  float fValue = pixValue * rescaleSlope + rescaleIntercept;
//                                  pixValue = (short)fValue;
//
//                              }

                                    if(overlayData != null) {
                                        if(getBit(overlayData,index2)) {
                                            imgArr[x][y] = 4096;
                                        } else {
                                            imgArr[x][y] = pixValue;
                                        }
                                        index2++;
                                    } else {
                                        imgArr[x][y] =  pixValue;
                                    }
                                }}
                            tags.put("imgArr", imgArr);
                        } else {
                            dicomDis.skip(Len);
                        }
                    } else {
                        dicomDis.skip(Len);
                    }
                }
                //文件夹开始标签
                else if ((VR.equals("SQ" )&& Len == Long.MAX_VALUE) || (tag.equals("fffe,e000") && Len ==  Long.MAX_VALUE)) {
                    if (enDir == false) {
                        enDir = true;
                        folderData.delete(0, folderData.length());
                        folderTag = tag;
                    } else {
                        leve++; //VF不赋值
                    }
                }
                //文件夹结束标签
                else if ((tag.equals("fffe,e00d") && Len == Long.MAX_VALUE) || (tag.equals("fffe,e0dd") && Len == Long.MAX_VALUE)) {
                    if (enDir == true) {
                        enDir = false;
                    } else {
                        leve--;
                    }
                } else {
                    VF = readBytes(dicomDis, (int) Len);
                }

                String VFStr;
                VFStr = getVF(VR, VF);

                //针对特殊的tag的值的处理
                //特别针对文件头信息处理
                if (tag.equals("0002,0000")) {
                    //fileHeadOffset = dicomFile.BaseStream.Position;
                    //传输语法 关系到后面的数据读取
                } else if (tag.equals("0002,0010")) {
                    switch (VFStr) {
                        case "1.2.840.10008.1.2.1\0": //显示little
                            isLittleEndian = true;
                            isExplicitVR = true;
                            break;
                        case "1.2.840.10008.1.2.2\0": //显示big
                            isLittleEndian = false;
                            isExplicitVR = true;
                            break;
                        case "1.2.840.10008.1.2\0": //隐式little
                            isLittleEndian = true;
                            isExplicitVR = false;
                            break;
                        default:
                            break;
                    }
                }

                for (int i = 1; i <= leve; i++)
                    tag = "--" + tag;
                //数据搜集代码

                //文件夹标签代码
                if ((VR.equals("SQ") && Len == Long.MAX_VALUE) || (tag.equals("fffe,e000") && Len == Long.MAX_VALUE) || leve > 0) {
                    folderData.append(tag + "(" + VR + ")：" + VFStr+"\n");
                    //文件夹结束标签
                } else if (((tag.equals("fffe,e00d") && Len == Long.MAX_VALUE) || (tag.equals("fffe,e0dd") && Len == Long.MAX_VALUE)) && leve == 0) {
                    folderData.append(tag + "(" + VR + ")：" + VFStr+"\n");
                    tags.put(folderTag + "SQ", folderData.toString());
                } else {
                    tags.put(tag, VFStr.trim());
                }
            }
            dicomDis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }

    private static String addZero (String a) {
        StringBuffer sb = new StringBuffer(a);
        for(int i=0;i<2-a.length();i++) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    private static String readString(DataInputStream data,int len) throws IOException {
        byte[] by = new byte[len];
        data.read(by);
        String ret = new String(by,"UTF-8");
        return ret ;
    }

    private static long readUInt(DataInputStream data) throws IOException {
        byte[] by = new byte[4];
        data.read(by);
        long ret = BitConverter.toUInt(by);
        return ret ;
    }

    private static int readUShort(DataInputStream data) throws IOException {
        byte[] by = new byte[2];
        data.read(by);
        int ret = BitConverter.toUShort(by);
        return ret ;
    }

    private static byte[] readBytes(DataInputStream data,int len) throws IOException {
        byte[] by = new byte[len];
        data.read(by);
        return by;
    }

    private static boolean getBit(byte[] overlayData, int i) {
        int index1 = i/8;
        int index2 = i%8;
        byte ret = (byte)(overlayData[index1]>>index2 & 0x1);
        return ret==1 ;
    }
}
