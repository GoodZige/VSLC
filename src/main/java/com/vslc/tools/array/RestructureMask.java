package com.vslc.tools.array;

import com.vslc.enums.MaskTypeEnum;
import com.vslc.model.Sequence;
import com.vslc.tools.BitConverter;
import com.vslc.tools.FileUtil;
import com.vslc.tools.PathUtil;
import com.vslc.tools.SavePath;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 旧标注结果转新标注结果（全部转完 没用）
 * Created by chenlele
 * 2018/6/7 13:12
 */
public class RestructureMask {

    private RandomAccessFile raf;

    private DataInputStream newDis;

    private DataInputStream oldDis;

    private int zSum;

    private int cols;

    private int rows;

    public void arrayMultiple(List<Sequence> sequenceList) {
        System.load(SavePath.opencvPath);
        for (Sequence sequence : sequenceList) arraySingle(sequence);
    }

    public void arraySingle(Sequence sequence) {
        String binPath = PathUtil.getBinFile(sequence, SavePath.rootPath);
        String maskPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, null);

        File binDir = new File(binPath);
        File maskDir = new File(maskPath);
        if (!maskDir.exists()) maskDir.mkdirs();

        String lungPath = PathUtil.getMaskFile(sequence, SavePath.rootPath
                , MaskTypeEnum.MASK_LUNG.getName() + ".bin");
        String nodulePath = PathUtil.getMaskFile(sequence, SavePath.rootPath
                , MaskTypeEnum.MASK_NODULE.getName() + ".bin");
        File lungMask = new File(lungPath);
        File noduleMask = new File(nodulePath);

        for (File oldMask : binDir.listFiles()) {
            String fileName = oldMask.getName();
            boolean isMask = !fileName.contains(".");
            String suffix = "";
            if (!isMask) suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

            if (suffix.contains("xls")) {
                FileUtil.copy(oldMask.getAbsolutePath()
                        , oldMask.getAbsolutePath().replaceAll("BIN", "MASK"));
            }
            if (isMask) {
                if (fileName.contains("LUNG")) handleMask(oldMask, lungMask);
                else if (fileName.contains("OBJECT")) handleMask(oldMask, noduleMask);
            }
        }
    }

    public void handleMask(File oldMask, File newMask) {
        try {
            byte objNum = (byte) getObjectNum(oldMask);

            if (!newMask.exists()) initMask(oldMask, newMask);

            raf = new RandomAccessFile(newMask, "rw");
            newDis = new DataInputStream(new FileInputStream(newMask));
            oldDis = new DataInputStream(new FileInputStream(oldMask));

            //读头文件信息
            int[] info = getOldMaskInfo(oldDis);
            zSum = info[0];
            cols = info[1];
            rows = info[2];

            restructure(objNum);

            oldDis.close();
            newDis.close();
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restructure(byte objNum) throws IOException {
        byte[] oldImg;
        byte[] contours;
        byte[] newImg;
        for (int z = 0; z < zSum; z++) {
            oldImg = new byte[cols*rows];
            newImg = new byte[cols*rows];
            oldDis.read(oldImg);
            newDis.read(newImg);
            contours = getContours(oldImg, objNum, z);
            if (contours != null) {
                int index = 0;
                for(int y = 0; y < rows; y++) {
                    for (int x = 0; x < cols; x++) {
                       if (contours[index] != '\0') {
                           newImg[index] = objNum;
                       }
                       index++;
                    }
                }
                raf.seek(z*cols*rows);
                raf.write(newImg);
            }
        }
    }

    public byte[] getContours(byte[] img, byte objNum, int z) {
        Mat srcMat = new Mat(cols, rows, CvType.CV_8UC1);
        int index = 0;
        for(int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                byte b = img[index++];
                if (b == '\0')
                    srcMat.put(x, y, 0);
                else
                    srcMat.put(x, y, 1);
            }
        }
        Mat hierarchy = new Mat();
        Scalar color =  new Scalar(250, 250, 255);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(srcMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        if (contours.size() == 0) return null;
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(srcMat, contours, i, color, 1, 8, hierarchy, 0, new Point());
        }

        byte[] out = new byte[cols*rows];
        int outIndex = 0;
        for (int x = 0;x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                byte b = (byte) srcMat.get(x, y)[0];
                if (b == '\0') {
                    out[outIndex] = 0;
                } else {
                    out[outIndex] = objNum;
                }
                outIndex++;
            }
        }
        return out;
    }

    public void initMask(File oldMask, File newMask) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(oldMask));
        int[] info = getOldMaskInfo(dis);
        int zSum = info[0];
        int cols = info[1];
        int rows = info[2];
        dis.close();

        newMask.createNewFile();
        DataOutputStream dos = new DataOutputStream((new FileOutputStream(newMask)));
        if (zSum > 400 || cols > 600) {
            for (int z = 0; z < zSum; z++) {
                byte[] init = new byte[cols*rows];
                for (int i = 0; i < cols*rows; i++) init[i] = 0;
                dos.write(init);
            }
        } else {
            byte[] init = new byte[zSum*cols*rows];
            for (int i = 0; i < zSum*cols*rows; i++) init[i] = 0;
            dos.write(init);
        }
        dos.close();
    }

    public int getObjectNum(File file) {
        String fileName = file.getName();
        String last3 = fileName.substring(fileName.length()-3);
        String last2 = fileName.substring(fileName.length()-2);
        String last1 = fileName.substring(fileName.length()-1);
        if (isInteger(last3)) return Integer.valueOf(last3);
        else if (isInteger(last2)) return Integer.valueOf(last2);
        else if (isInteger(last1)) return Integer.valueOf(last1);
        else return 1;
    }

    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public int[] getOldMaskInfo(DataInputStream dis) {
        int[] info = new int[3];
        try {
            for (int i = 0; i < 3; i++) {
                byte[] bytes = new byte[4];
                for (int j = 0; j < 4; j++)
                    bytes[j] = dis.readByte();
                info[i] = BitConverter.toInt(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }
}
