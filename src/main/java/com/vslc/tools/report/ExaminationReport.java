package com.vslc.tools.report;

import com.vslc.model.Examination;
import com.vslc.model.Patient;
import com.vslc.tools.SavePath;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * 检验报告单
 * Created by chenlele
 * 2018/3/11 10:34
 */
public class ExaminationReport {

    private static final int WIDTH = 1600;

    private static final int HEIGHT = 1000;

    private static final int fontSize = 25;

    private static int x = 0;

    private static int y = 100;

    private static List<List<String>> leftList;

    private static List<List<String>> rightList;

    private static BufferedImage image;

    private static Graphics graphics;

    public static String getReport(Map<String, Object> param) {
        Integer format = (Integer) param.get("format");
        Patient patient = (Patient) param.get("patient");
        Examination examination = (Examination) param.get("examination");
        StringBuilder path = new StringBuilder(SavePath.examinationPath);
        path.append(examination.getPatient().getPatientID());
        File dir = new File(path.toString());
        if (!dir.exists()) dir.mkdirs();
        path.append("\\");
        path.append(format);
        path.append(".jpg");

        if (!new File(path.toString()).exists()) {
            image = new BufferedImage(WIDTH, HEIGHT,
                    BufferedImage.TYPE_INT_RGB);
            graphics = image.getGraphics();
            //画模板
            drawTemplate();
            //画病人信息
            writePatientInfo(patient);
            //报告单有两种格式
            if (format == 1) writeFormat1(examination);
            else if (format == 2) writeFormat2(examination);
            ReportUtil.createImage(path.toString(), image);
        }
        return path.toString();
    }

    private static void writeFormat1(Examination examination) {
        leftList = new ArrayList<>();
        rightList = new ArrayList<>();
        graphics.setFont(new Font("黑体", Font.BOLD, fontSize));

        writeTP(examination.getTP());
        writeALB(examination.getALB());
        writeGLO(examination.getGLO());
        writeAG(examination.getAG());
        writeALT(examination.getALT());
        writeALP(examination.getALP());
        writeCHE(examination.getCHE());
        writeY_GT(examination.getY_GT());
        writeLDH(examination.getLDH());
        writeCRP(examination.getCRP());
        writeBuN(examination.getBuN());
        writeCr(examination.getCr());
        writeUA(examination.getUA());

        writeTG(examination.getTG());
        writeCH(examination.getCH());
        writeADA(examination.getADA());

        writeTableInfo();
    }

    private static void writeFormat2(Examination examination) {
        leftList = new ArrayList<>();
        rightList = new ArrayList<>();
        graphics.setFont(new Font("黑体", Font.BOLD, fontSize));

        writeAPTT(examination.getAPTT());
        writeTT(examination.getTT());
        writeINR(examination.getINR());
        writePT(examination.getPT());
        writeHDL(examination.getHDL());
        writeD_D(examination.getD_D());

        writeCA724(examination.getCA724());
        writeCA242(examination.getCA242());
        writeCYF211(examination.getCYF211());
        writeNSE(examination.getNSE());
        writeAFP(examination.getAFP());
        writeCEA(examination.getCEA());
        writeCA125(examination.getCA125());
        writeCA153(examination.getCA153());
        writeCA199(examination.getCA199());
        writeSCC(examination.getSCC());

        writeTableInfo();
    }

    private static void drawTemplate() {
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        Image logo = new ImageIcon(SavePath.imagePath+"logo.png").getImage();
        graphics.drawImage(logo, 50, 30, 330, 100, null);

        graphics.setColor(Color.black);
        graphics.setFont(new Font("宋体", Font.BOLD, 50));
        graphics.drawString("检验报告单", 670, 100);
        x+=80;

        /**
         * 表格线
         */
        y = 220;
        graphics.drawLine(50, y, 1550, y);
        graphics.drawLine(50, y, 50, y + 710);
        graphics.drawLine(1550, y, 1550, y + 710);
        graphics.drawLine(50, y + 710, 1550, y + 710);
        graphics.drawLine(800, y, 800, y + 710);
        graphics.drawLine(50, y + 70, 1550, y + 70);

        /**
         * 固定字段
         */
        y+=45;
        graphics.setFont(new Font("黑体", Font.BOLD, fontSize));
        graphics.drawString("项目", 150, y);
        graphics.drawString("结果", 405, y);
        graphics.drawString("单位", 510, y);
        graphics.drawString("参考值", 650, y);

        graphics.drawString("项目", 900, y);
        graphics.drawString("结果", 1145, y);
        graphics.drawString("单位", 1260, y);
        graphics.drawString("参考值", 1400, y);
    }

    private static void writePatientInfo(Patient patient) {
        y = 180;
        graphics.setFont(new Font("黑体", Font.BOLD, fontSize));
        graphics.drawString("姓名", 85, y);
        graphics.drawString("性别", 520, y);
        graphics.drawString("年龄", 940, y);
        graphics.drawString("住院号", 1300, y);
        graphics.setFont(new Font("黑体", Font.PLAIN, fontSize));
        graphics.drawString(patient.getChineseName(), 85 + 70, y);
        graphics.drawString(ReportUtil.getSex(patient.getPatientSex()), 520 + 70, y);
        graphics.drawString(ReportUtil.getAge(patient.getBirday()), 940 + 70, y);
        graphics.drawString(patient.getAdmissionNum(), 1300 + 100, y);
    }

    private static void writeTableInfo() {
        int[] len = {270, 110, 130, 0};

        y = 270;
        int leftIndex = 1;
        for(List<String> list : leftList){
            y += 50;
            x = 80;
            graphics.drawString(Integer.toString(leftIndex), x, y);
            x+=50;
            int index = 0;
            for (String value : list) {
                graphics.drawString(value, x, y);
                x += len[index];
                index++;
            }
            leftIndex ++;
        }

        y = 270;
        int rightIndex = leftList.size() + 1;
        for(List<String> list : rightList){
            y += 50;
            x = 830;
            graphics.drawString(Integer.toString(rightIndex), x, y);
            x+=50;
            int index = 0;
            for (String value : list) {
                graphics.drawString(value, x, y);
                x += len[index];
                index++;
            }
            rightIndex++;
        }
    }

    private static void writeTP(Float value) {
        List<String> TP = new ArrayList<>();
        TP.add("总蛋白");
        if (value != null) {
            if (value >= 85.0)
                TP.add(Float.toString(value) + "↑");
            else if (value <= 65.0)
                TP.add(Float.toString(value) + "↓");
            else
                TP.add(Float.toString(value));
        }
        else TP.add("   ");
        TP.add("g/L");
        TP.add("65.0-85.0");
        leftList.add(TP);
    }

    private static void writeALB(Float value) {
        List<String> ALB = new ArrayList<>();
        ALB.add("白蛋白");
        if (value != null) {
            if (value >= 55.0)
                ALB.add(Float.toString(value) + "↑");
            else if (value <= 40.0)
                ALB.add(Float.toString(value) + "↓");
            else
                ALB.add(Float.toString(value));
        }
        else ALB.add("   ");
        ALB.add("g/L");
        ALB.add("40.0-55.0");
        leftList.add(ALB);
    }

    private static void writeGLO(Float value) {
        List<String> GLO = new ArrayList<>();
        GLO.add("球蛋白");
        if (value != null) {
            if (value >= 40.0)
                GLO.add(Float.toString(value) + "↑");
            else if (value <= 20.0)
                GLO.add(Float.toString(value) + "↓");
            else
                GLO.add(Float.toString(value));
        }
        else GLO.add("   ");
        GLO.add("g/L");
        GLO.add("20.0-40.0");
        leftList.add(GLO);
    }

    private static void writeAG(Float value) {
        List<String> AG = new ArrayList<>();
        AG.add("白球比值");
        if (value != null) {
            if (value >= 2.40)
                AG.add(Float.toString(value) + "↑");
            else if (value <= 1.24)
                AG.add(Float.toString(value) + "↓");
            else
                AG.add(Float.toString(value));
        }
        else AG.add("   ");
        AG.add("   ");
        AG.add("1.24-2.40");
        leftList.add(AG);
    }

    private static void writeALT(Integer value) {
        List<String> ALT = new ArrayList<>();
        ALT.add("丙氨酸氨基转移酶");
        if (value != null) {
            if (value >= 50)
                ALT.add(Integer.toString(value) + "↑");
            else if (value <= 9)
                ALT.add(Integer.toString(value) + "↓");
            else
                ALT.add(Integer.toString(value));
        }
        else ALT.add("   ");
        ALT.add("U/L");
        ALT.add("9-50");
        leftList.add(ALT);
    }

    private static void writeALP(Integer value) {
        List<String> ALP = new ArrayList<>();
        ALP.add("碱性磷酸酶");
        if (value != null) {
            if (value >= 125)
                ALP.add(Integer.toString(value) + "↑");
            else if (value <= 45)
                ALP.add(Integer.toString(value) + "↓");
            else
                ALP.add(Integer.toString(value));
        }
        else ALP.add("   ");
        ALP.add("U/L");
        ALP.add("45-125");
        leftList.add(ALP);
    }

    private static void writeCHE(Integer value) {
        List<String> CHE = new ArrayList<>();
        CHE.add("胆碱脂酶");
        if (value != null) {
            if (value >= 12000)
                CHE.add(Integer.toString(value) + "↑");
            else if (value <= 4500)
                CHE.add(Integer.toString(value) + "↓");
            else
                CHE.add(Integer.toString(value));
        }
        else CHE.add("   ");
        CHE.add("U/L");
        CHE.add("4500-12000");
        leftList.add(CHE);
    }

    private static void writeY_GT(Integer value) {
        List<String> Y_GT = new ArrayList<>();
        Y_GT.add("L-r-谷氨酰转移酶");
        if (value != null) {
            if (value >= 60)
                Y_GT.add(Integer.toString(value) + "↑");
            else if (value <= 10)
                Y_GT.add(Integer.toString(value) + "↓");
            else
                Y_GT.add(Integer.toString(value));
        }
        else Y_GT.add("   ");
        Y_GT.add("U/L");
        Y_GT.add("10-60");
        leftList.add(Y_GT);
    }

    private static void writeLDH(Integer value) {
        List<String> LDH = new ArrayList<>();
        LDH.add("乳酸脱氢酶");
        if (value != null) {
            if (value >= 240)
                LDH.add(Integer.toString(value) + "↑");
            else if (value <= 0)
                LDH.add(Integer.toString(value) + "↓");
            else
                LDH.add(Integer.toString(value));
        }
        else LDH.add("   ");
        LDH.add("U/L");
        LDH.add("0-240");
        leftList.add(LDH);
    }

    private static void writeCRP(Float value) {
        List<String> CRP = new ArrayList<>();
        CRP.add("超敏C反应蛋白");
        if (value != null) {
            if ((value >= 10.00))
                CRP.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                CRP.add(Float.toString(value) + "↓");
            else
                CRP.add(Float.toString(value));
        }
        else CRP.add("   ");
        CRP.add("mg/L");
        CRP.add("0.00-10.00");
        leftList.add(CRP);
    }

    private static void writeBuN(Float value) {
        List<String> BuN = new ArrayList<>();
        BuN.add("尿素氮");
        if (value != null) {
            if (value >= 7.80)
                BuN.add(Float.toString(value) + "↑");
            else if (value <= 2.20)
                BuN.add(Float.toString(value) + "↓");
            else
                BuN.add(Float.toString(value));
        }
        else BuN.add("   ");
        BuN.add("mmol/L");
        BuN.add("2.20-7.80");
        leftList.add(BuN);
    }

    private static void writeCr(Float value) {
        List<String> Cr = new ArrayList<>();
        Cr.add("肌酐");
        if (value != null) {
            if (value >= 104.0)
                Cr.add(Float.toString(value) + "↑");
            else if (value <= 59.0)
                Cr.add(Float.toString(value) + "↓");
            else
                Cr.add(Float.toString(value));
        }
        else Cr.add("   ");
        Cr.add("μmol/L");
        Cr.add("59.0-104.0");
        leftList.add(Cr);
    }

    private static void writeUA(Integer value) {
        List<String> UA = new ArrayList<>();
        UA.add("尿酸");
        if (value != null) {
            if (value >= 428)
                UA.add(Float.toString(value) + "↑");
            else if (value <= 90)
                UA.add(Float.toString(value) + "↓");
            else
                UA.add(Float.toString(value));
        }
        else UA.add("   ");
        UA.add("μmol/L");
        UA.add("90-428");
        leftList.add(UA);
    }

    private static void writeTG(Float value) {
        List<String> TG = new ArrayList<>();
        TG.add("甘油三脂");
        if (value != null) {
            if (value >= 1.60)
                TG.add(Float.toString(value) + "↑");
            else if (value <= 0.60)
                TG.add(Float.toString(value) + "↓");
            else
                TG.add(Float.toString(value));
        }
        else TG.add("   ");
        TG.add("mmol/L");
        TG.add("0.60-1.60");
        rightList.add(TG);
    }

    private static void writeCH(Float value) {
        List<String> CH = new ArrayList<>();
        CH.add("总胆固醇");
        if (value != null) {
            if (value >= 6.10)
                CH.add(Float.toString(value) + "↑");
            else if (value <= 3.60)
                CH.add(Float.toString(value) + "↓");
            else
                CH.add(Float.toString(value));
        }
        else CH.add("   ");
        CH.add("mmol/L");
        CH.add("3.60-6.10");
        rightList.add(CH);
    }

    private static void writeADA(Float value) {
        List<String> ADA = new ArrayList<>();
        ADA.add("腺苷脱氨酶");
        if (value != null) {
            if (value >= 25.0)
                ADA.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                ADA.add(Float.toString(value) + "↓");
            else
                ADA.add(Float.toString(value));
        }
        else ADA.add("   ");
        ADA.add("U/L");
        ADA.add("0.0-25.0");
        rightList.add(ADA);
    }

    private static void writeAPTT(Float value) {
        List<String> APTT = new ArrayList<>();
        APTT.add("部分凝血活酶时间");
        if (value != null) {
            if (value >= 36.5)
                APTT.add(Float.toString(value) + "↑");
            else if (value <= 25.1)
                APTT.add(Float.toString(value) + "↓");
            else
                APTT.add(Float.toString(value));
        }
        else APTT.add("   ");
        APTT.add("秒");
        APTT.add("25.1-36.5");
        leftList.add(APTT);
    }

    private static void writeTT(Float value) {
        List<String> TT = new ArrayList<>();
        TT.add("凝血酶时间");
        if (value != null) {
            if (value >= 16.6)
                TT.add(Float.toString(value) + "↑");
            else if (value <= 10.3)
                TT.add(Float.toString(value) + "↓");
            else
                TT.add(Float.toString(value));
        }
        else TT.add("   ");
        TT.add("秒");
        TT.add("10.3-16.6");
        leftList.add(TT);
    }

    private static void writeINR(Float value) {
        List<String> INR = new ArrayList<>();
        INR.add("凝血酶原国际化比率");
        if (value != null) {
            if (value >= 1.20)
                INR.add(Float.toString(value) + "↑");
            else if (value <= 0.80)
                INR.add(Float.toString(value) + "↓");
            else
                INR.add(Float.toString(value));
        }
        else INR.add("   ");
        INR.add("INR");
        INR.add("0.80-1.20");
        leftList.add(INR);
    }

    private static void writePT(Float value) {
        List<String> PT = new ArrayList<>();
        PT.add("凝血酶原时间");
        if (value != null) {
            if (value >= 12.5)
                PT.add(Float.toString(value) + "↑");
            else if (value <= 9.4)
                PT.add(Float.toString(value) + "↓");
            else
                PT.add(Float.toString(value));
        }
        else PT.add("   ");
        PT.add("秒");
        PT.add("9.4-12.5");
        leftList.add(PT);
    }

    private static void writeHDL(Float value) {
        List<String> HDL = new ArrayList<>();
        HDL.add("高密度脂蛋白");
        if (value != null) {
            if (value >= 2.00)
                HDL.add(Float.toString(value) + "↑");
            else if (value <= 0.70)
                HDL.add(Float.toString(value) + "↓");
            else
                HDL.add(Float.toString(value));
        }
        else HDL.add("   ");
        HDL.add("mmol/L");
        HDL.add("0.70-2.00");
        leftList.add(HDL);
    }

    private static void writeD_D(Float value) {
        List<String> D_D = new ArrayList<>();
        D_D.add("D二聚体");
        if (value != null) {
            if (value >= 232.00)
                D_D.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                D_D.add(Float.toString(value) + "↓");
            else
                D_D.add(Float.toString(value));
        }
        else D_D.add("   ");
        D_D.add("ng/ml");
        D_D.add("0.00-232.00");
        leftList.add(D_D);
    }

    private static void writeCA724(Float value) {
        List<String> CA724 = new ArrayList<>();
        CA724.add("糖类抗原724");
        if (value != null) {
            if (value >= 6.90)
                CA724.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                CA724.add(Float.toString(value) + "↓");
            else
                CA724.add(Float.toString(value));
        }
        else CA724.add("   ");
        CA724.add("U/L");
        CA724.add("0.00-6.90");
        rightList.add(CA724);
    }

    private static void writeCA242(Float value) {
        List<String> CA242 = new ArrayList<>();
        CA242.add("糖类抗原242");
        if (value != null) {
            if (value >= 20.0)
                CA242.add(Float.toString(value) + "↑");
            else if (value <= 0.0)
                CA242.add(Float.toString(value) + "↓");
            else
                CA242.add(Float.toString(value));
        }
        else CA242.add("   ");
        CA242.add("U/L");
        CA242.add("0.0-20.0");
        rightList.add(CA242);
    }

    private static void writeCYF211(Float value) {
        List<String> CYF211 = new ArrayList<>();
        CYF211.add("细胞角蛋白19");
        if (value != null) {
            if (value >= 3.30)
                CYF211.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                CYF211.add(Float.toString(value) + "↓");
            else
                CYF211.add(Float.toString(value));
        }
        else CYF211.add("   ");
        CYF211.add("ng/ml");
        CYF211.add("0.00-3.30");
        rightList.add(CYF211);
    }

    private static void writeNSE(Float value) {
        List<String> NSE = new ArrayList<>();
        NSE.add("神经元特异性烯醇化酶");
        if (value != null) {
            if (value >= 17.0)
                NSE.add(Float.toString(value) + "↑");
            else if (value <= 0.0)
                NSE.add(Float.toString(value) + "↓");
            else
                NSE.add(Float.toString(value));
        }
        else NSE.add("   ");
        NSE.add("ng/ml");
        NSE.add("0.0-17.0");
        rightList.add(NSE);
    }

    private static void writeAFP(Float value) {
        List<String> AFP = new ArrayList<>();
        AFP.add("甲胎蛋白");
        if (value != null) {
            if (value >= 10.00)
                AFP.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                AFP.add(Float.toString(value) + "↓");
            else
                AFP.add(Float.toString(value));
        }
        else AFP.add("   ");
        AFP.add("ng/ml");
        AFP.add("0.00-10.00");
        rightList.add(AFP);
    }

    private static void writeCEA(Float value) {
        List<String> CEA = new ArrayList<>();
        CEA.add("癌胚抗原");
        if (value != null) {
            if (value >= 5.00)
                CEA.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                CEA.add(Float.toString(value) + "↓");
            else
                CEA.add(Float.toString(value));
        }
        else CEA.add("   ");
        CEA.add("ng/ml");
        CEA.add("0.00-5.00");
        rightList.add(CEA);
    }

    private static void writeCA125(Float value) {
        List<String> CA125 = new ArrayList<>();
        CA125.add("糖类抗原125");
        if (value != null) {
            if (value >= 35.00)
                CA125.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                CA125.add(Float.toString(value) + "↓");
            else
                CA125.add(Float.toString(value));
        }
        else CA125.add("   ");
        CA125.add("ug/L");
        CA125.add("0.00-35.00");
        rightList.add(CA125);
    }

    private static void writeCA153(Float value) {
        List<String> CA153 = new ArrayList<>();
        CA153.add("糖类抗原153");
        if (value != null) {
            if (value >= 28.00)
                CA153.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                CA153.add(Float.toString(value) + "↓");
            else
                CA153.add(Float.toString(value));
        }
        else CA153.add("   ");
        CA153.add("U/ml");
        CA153.add("0.00-28.00");
        rightList.add(CA153);
    }

    private static void writeCA199(Float value) {
        List<String> CA199 = new ArrayList<>();
        CA199.add("糖类抗原199");
        if (value != null) {
            if (value >= 40.00)
                CA199.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                CA199.add(Float.toString(value) + "↓");
            else
                CA199.add(Float.toString(value));
        }
        else CA199.add("   ");
        CA199.add("U/ml");
        CA199.add("0.00-40.00");
        rightList.add(CA199);
    }

    private static void writeSCC(Float value) {
        List<String> SCC = new ArrayList<>();
        SCC.add("鳞状上皮细胞抗原");
        if (value != null) {
            if (value >= 1.50)
                SCC.add(Float.toString(value) + "↑");
            else if (value <= 0.00)
                SCC.add(Float.toString(value) + "↓");
            else
                SCC.add(Float.toString(value));
        }
        else SCC.add("   ");
        SCC.add("ug/L");
        SCC.add("0.00-1.50");
        rightList.add(SCC);
    }
}
