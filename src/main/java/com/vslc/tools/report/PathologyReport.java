package com.vslc.tools.report;

import com.vslc.model.Pathology;
import com.vslc.model.Patient;
import com.vslc.tools.SavePath;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * 病理报告单
 * Created by chenlele
 * 2018/5/26 11:40
 */
public class PathologyReport {

    private static final int WIDTH = 850;

    private static final int HEIGHT = 1000;

    private static final int fontSize = 25;

    private static int x = 0;

    private static int y = 100;

    private static BufferedImage image;

    private static Graphics graphics;

    public static String getReport(List<Pathology> pathologyList) {
        Patient patient = null;
        for (Pathology pathology : pathologyList) {
            patient = pathology.getPatient();
            break;
        }

        StringBuilder path = new StringBuilder(SavePath.pathologyPath);
        path.append(patient.getPatientID());
        File dir = new File(path.toString());
        if (!dir.exists()) dir.mkdirs();
        path.append("\\");
        path.append(patient.getAdmissionNum());
        path.append(".jpg");

        if (!new File(path.toString()).exists()) {
            image = new BufferedImage(WIDTH, HEIGHT,
                    BufferedImage.TYPE_INT_RGB);
            graphics = image.getGraphics();
            drawTemplate();
            writePatientInfo(patient);
            writeClinicalDiagnosis(pathologyList);
            writePathologicalDiagnosis(pathologyList);
            ReportUtil.createImage(path.toString(), image);
        }
        return path.toString();
    }

    private static void drawTemplate() {
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        Image logo = new ImageIcon(SavePath.imagePath+"logo.png").getImage();
        graphics.drawImage(logo, 50, 25, 210, 70, null);

        graphics.setColor(Color.black);
        graphics.setFont(new Font("宋体", Font.BOLD, 35));
        graphics.drawString("病理报告单", 335, 75);
        x+=80;

        y = 120;
        graphics.drawLine(50, y, 800, y);
        graphics.drawLine(50, y + 220, 800, y + 220);
        graphics.drawLine(50, y + 830, 800, y + 830);
    }

    private static void writePatientInfo(Patient patient) {
        y = 170;
        graphics.setFont(new Font("黑体", Font.BOLD, fontSize));
        graphics.drawString("姓名", 50, y);
        graphics.drawString("性别", 260, y);
        graphics.drawString("年龄", 420, y);
        graphics.drawString("住院号", 580, y);
        graphics.setFont(new Font("黑体", Font.PLAIN, fontSize));;
        graphics.drawString(patient.getChineseName(), 50 + 70, y);
        graphics.drawString(ReportUtil.getSex(patient.getPatientSex()), 260 + 70, y);
        graphics.drawString(ReportUtil.getAge(patient.getBirday()), 420 + 70, y);
        graphics.drawString(patient.getAdmissionNum(), 580 + 100, y);
    }

    private static void writeClinicalDiagnosis(List<Pathology> pathologyList) {
//        StringBuilder diagnosis = new StringBuilder();

        x = 50;
        y = 220;
        graphics.setFont(new Font("黑体", Font.BOLD, fontSize));
        graphics.drawString("临床诊断", x, y);

//        x += 50;
//        y += 40;
//        int gap = 30;
//        int rowSize = 28;
//        graphics.setFont(new Font("黑体", Font.PLAIN, fontSize));
//        ReportUtil.drawParagraph(x, y, rowSize, fontSize, gap, graphics, diagnosis.toString());
    }

    private static void writePathologicalDiagnosis(List<Pathology> pathologyList) {
        StringBuilder diagnosis = new StringBuilder();
        for (Pathology pathology : pathologyList) {
            if (pathology.getPosition() != null) {
                diagnosis.append("（");
                diagnosis.append(pathology.getPosition());
                diagnosis.append("）");
            }
            if (pathology.getIsCIS() != null) {
                diagnosis.append(ReportUtil.transferIsCls(pathology.getIsCIS()));
            }
            if (pathology.getPathologicalType() != null) {
                diagnosis.append(pathology.getPathologicalType().getPathologicalTypeName());
            }
            if (pathology.getSize() != null) {
                diagnosis.append("（瘤体");
                diagnosis.append(pathology.getSize().replaceAll("\\*", "×"));
                diagnosis.append("）");
            }
            if (pathology.getGrowthMode() != null) {
                diagnosis.append(" 生长类型：");
                diagnosis.append(ReportUtil.transferGrowthMode(pathology.getGrowthMode()));
                diagnosis.append(" ");
            }
            if (pathology.getVTS() != null) {
                diagnosis.append("（");
                diagnosis.append(ReportUtil.transferVTS(pathology.getVTS()));
                diagnosis.append("脉管瘤栓）");
            }
            if (pathology.getLymphonodus2() != null) {
                diagnosis.append("（第二组）第");
                diagnosis.append(pathology.getLymphonodus2());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus3() != null) {
                diagnosis.append("（第三组）第");
                diagnosis.append(pathology.getLymphonodus3());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus4() != null) {
                diagnosis.append("（第四组）第");
                diagnosis.append(pathology.getLymphonodus4());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus5() != null) {
                diagnosis.append("（第五组）第");
                diagnosis.append(pathology.getLymphonodus5());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus6() != null) {
                diagnosis.append("（第六组）第");
                diagnosis.append(pathology.getLymphonodus6());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus7() != null) {
                diagnosis.append("（第七组）第");
                diagnosis.append(pathology.getLymphonodus7());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus8() != null) {
                diagnosis.append("（第八组）第");
                diagnosis.append(pathology.getLymphonodus8());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus9() != null) {
                diagnosis.append("（第九组）第");
                diagnosis.append(pathology.getLymphonodus9());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus10() != null) {
                diagnosis.append("（第十组）第");
                diagnosis.append(pathology.getLymphonodus10());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus11() != null) {
                diagnosis.append("（第十一组）第");
                diagnosis.append(pathology.getLymphonodus11());
                diagnosis.append("只 ");
            }
            if (pathology.getLymphonodus12() != null) {
                diagnosis.append("（第十二组）第");
                diagnosis.append(pathology.getLymphonodus12());
                diagnosis.append("只 ");
            }
        }

        x = 50;
        y = 390;
        graphics.setFont(new Font("黑体", Font.BOLD, fontSize));
        graphics.drawString("病理诊断", x, y);

        x += 50;
        y += 50;
        int gap = fontSize * 2;
        int rowSize = 28;
        graphics.setFont(new Font("黑体", Font.PLAIN, fontSize));
        ReportUtil.drawParagraph(x, y, rowSize, fontSize, gap, graphics, diagnosis.toString());
    }
}
