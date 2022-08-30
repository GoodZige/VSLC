package com.vslc.tools.report;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenlele
 * 2018/5/26 14:31
 */
public class ReportUtil {

    public static void createImage(String path, BufferedImage image) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
            encoder.encode(image);
            bos.close();
//            ImageIO.write(image, "jpg", out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //自动换行  rowSize每行多少字换行
    public static void drawParagraph(int x, int y, int rowSize, int fontSize, int gap, Graphics graphics, String input) {
        int length = input.length();
        int rowSum = length / rowSize;
        int rowMod = length % rowSize;
        for (int i = 0; i < rowSum; i++)
            graphics.drawString(input.substring(i*rowSize, (i+1)*rowSize+(i!=0?2:0))
                    , x - (i!=0?1:0)*(fontSize*2), y + i*gap);
        if (rowMod != 0)
            graphics.drawString(input.substring(rowSum*rowSize+(rowSum>1?2:0), length)
                    , x - (rowSum>1?1:0)*(fontSize*2), y + rowSum*gap);
    }

    public static String getSex(Integer value) {
        if (value == 0) return "女";
        else if (value == 1) return "男";
        else return "";
    }

    public static String getAge(Date date) {
        Calendar thisYear = Calendar.getInstance();
        Calendar birYear = Calendar.getInstance();
        birYear.setTime(date);
        return String.valueOf(thisYear.get(Calendar.YEAR) - birYear.get(Calendar.YEAR));
    }

    public static String transferPathologicalType(Integer value) {
        if (value == 1) return "腺癌";
        else if (value == 2) return "鳞癌";
        else if (value == null) return "";
        else return "其他类型";
    }

    public static String transferIsCls(Integer value) {
        if (value == 1) return "原位癌";
        else if (value == 2) return "浸润癌";
        else return "";
    }

    public static String transferGrowthMode(Integer value) {
        switch (value) {
            case 1: return "腺瘤样增生";
            case 3: return "原位癌";
            case 4: return "微浸润癌";
            case 52: return "腺泡状为主型";
            case 53: return "乳头状为主型";
            case 54: return "微乳头状为主型";
            case 55: return "实性为主型";
            default: return null;
        }
    }

    public static String transferVTS(Integer value) {
        if (value == 0) return "无";
        else if (value == 1) return "有";
        else return "";
    }
}
