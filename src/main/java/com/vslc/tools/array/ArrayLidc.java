package com.vslc.tools.array;

import com.vslc.model.Position;
import com.vslc.tools.BitConverter;
import com.vslc.tools.FileUtil;
import com.vslc.tools.SavePath;
import com.vslc.tools.xml.XmlUtil;
import com.vslc.tools.dicom.DcmHandler;
import com.vslc.tools.dicom.DcmInfoReader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.io.*;
import java.util.*;

/**
 * 整理LIDC-IDRI数据集（全部整理完毕 没用）
 * Created by chenlele
 * 2018/7/12 14:23
 */
public class ArrayLidc {

    private byte objNum;

    private int initialIndex;

    public void maskMultiple(String srcPath) {
        File src = new File(srcPath);
        for (File lidcDir : src.listFiles())
            maskSingle(lidcDir);
    }

    public void arrayMultiple(String srcPath) {
        File src = new File(srcPath);
        for (File lidcDir : src.listFiles())
            arraySingle(lidcDir);
    }

    public void systemMultiple(String srcPath) {
        File src = new File(srcPath);
        for (File lidcDir : src.listFiles())
            systemSingle(lidcDir);
    }

    public void systemSingle(File lidcDir) {
        String lidcName = lidcDir.getName();
        for (File file : lidcDir.listFiles()) {
            if (file.isFile()) {
                String fileName = file.getName();
                String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
                if (suffix.equals("dcm")) {
                    HashMap<String, Object> dcmData = DcmHandler.handle(file, false);
                    String seNum = DcmInfoReader.readSeriesNum(dcmData);
                    String dcmDirPath = SavePath.srcPath+"LIDC-IDRI\\"+lidcName+"\\"+seNum+"\\DCM\\";
                    String jpgDirPath = SavePath.srcPath+"LIDC-IDRI\\"+lidcName+"\\"+seNum+"\\JPG\\";
                    String matrixDirPath = SavePath.srcPath+"LIDC-IDRI\\"+lidcName+"\\"+seNum+"\\BINX\\";
                    File dcmDir = new File(dcmDirPath);
                    File jpgDir = new File(jpgDirPath);
                    File matrixDir = new File(matrixDirPath);
                    if (!dcmDir.exists()) dcmDir.mkdirs();
                    if (!jpgDir.exists()) jpgDir.mkdirs();
                    if (!matrixDir.exists()) matrixDir.mkdirs();
                    int index = DcmInfoReader.readImgIndex(dcmData);
                    FileUtil.copy(file.getAbsolutePath(), dcmDirPath+index+".dcm");
                    file.delete();
                } if (suffix.equals("xml")) file.delete();
            }
        }
    }

    public void arraySingle(File lidcDir) {
        File originDir = new File(lidcDir + "\\origin");
        File maskDir = new File(lidcDir + "\\mask");
        File xmlDir = new File(lidcDir + "\\xml");
        if (!originDir.exists()) originDir.mkdirs();
        if (!maskDir.exists()) maskDir.mkdirs();
        if (!xmlDir.exists()) xmlDir.mkdirs();
        for (File file : lidcDir.listFiles()) {
            if (file.isFile()) {
                String fileName = file.getName();
                String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
                if (suffix.equals("dcm")) {
                    HashMap<String, Object> dcmData = DcmHandler.handle(file, false);
                    int index = DcmInfoReader.readImgIndex(dcmData);
                    FileUtil.copy(file.getAbsolutePath(), originDir+"\\"+index+".dcm");
                    file.delete();
                } else if (suffix.equals("xml")) {
                    FileUtil.copy(file.getAbsolutePath(), xmlDir+"\\"+file.getName());
                    file.delete();
                }
            }
        }
    }

    public void maskSingle(File lidcDir) {
        List<File> dcmFileList = new ArrayList<>();
        File maskDir = new File(lidcDir + "\\mask");
        if (!maskDir.exists()) {
            maskDir.mkdirs();
            for (File type : lidcDir.listFiles()) {
                String fileName = type.getName();
                if (fileName.equals("origin")) {
                    for (File file : type.listFiles())
                        dcmFileList.add(file);

                    dcmFileList.sort((file1, file2) -> {
                        String fileName1 = file1.getName();
                        String fileName2 = file2.getName();
                        int index1 = Integer.valueOf(fileName1.substring(0, fileName1.lastIndexOf(".")));
                        int index2 = Integer.valueOf(fileName2.substring(0, fileName2.lastIndexOf(".")));
                        return index1 - index2;
                    });
                    toMatrix(type.getParent(), dcmFileList);
                }
            }
            String initialName = dcmFileList.get(0).getName();
            initialIndex = Integer.valueOf(initialName.substring(0, initialName.indexOf(".")));
            System.out.println(initialIndex);
            for (File type : lidcDir.listFiles()) {
                String fileName = type.getName();
                if (fileName.equals("xml")) {
                    for (File xml : type.listFiles()) {
                        parseXml(xml);
                    }
                }
            }
        }
    }

    private void toMatrix(String lidcPath, List<File> dcmFileList) {
        File matrixFile = new File(lidcPath+"\\mask\\matrix.bin");
        HashMap<String, Object> headerDcmData = null;
        for (File file : dcmFileList) {
            headerDcmData = DcmHandler.handle(file, false);
            break;
        }
        int zSum = dcmFileList.size();
        int cols = DcmInfoReader.readColumns(headerDcmData);
        int rows = DcmInfoReader.readRows(headerDcmData);
        double columnPixelSpacing = DcmInfoReader.readColumnPixelSpacing(headerDcmData);
        double rowPixelSpacing = DcmInfoReader.readRowPixelSpacing(headerDcmData);
        double thickness = DcmInfoReader.readThickness(headerDcmData);
        int slope = (int) DcmInfoReader.readSlope(headerDcmData);
        int intercept = (int) DcmInfoReader.readIntercept(headerDcmData);
        byte[] zSumBytes = BitConverter.toBytes(zSum);
        byte[] colsBytes = BitConverter.toBytes(cols);
        byte[] rowsBytes = BitConverter.toBytes(rows);
        byte[] colsPixelBytes = BitConverter.toBytes(columnPixelSpacing);
        byte[] rowsPixelBytes = BitConverter.toBytes(rowPixelSpacing);
        byte[] thicknessBytes = BitConverter.toBytes(thickness);

        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(matrixFile));
            dos.write(zSumBytes);
            dos.write(colsBytes);
            dos.write(rowsBytes);
            dos.write(colsPixelBytes);
            dos.write(rowsPixelBytes);
            dos.write(thicknessBytes);
            Iterator<File> it = dcmFileList.iterator();
            while(it.hasNext()) {
                File dcmFile = it.next();
                if(dcmFile.exists()) {
                    HashMap<String, Object> dcmData = DcmHandler.handle(dcmFile, true);
                    short[][] curImgArr = DcmInfoReader.readImgArr(dcmData);
                    byte[] img = new byte[cols*rows*2];
                    int index = 0;
                    for(int y = 0; y < rows; y++) {
                        for(int x = 0; x < cols; x++) {
                            byte[] curBytes = BitConverter.toBytes((short) (curImgArr[x][y]*slope+intercept));
                            img[index]=curBytes[0];
                            img[index+1]=curBytes[1];
                            index += 2;
                        }
                    }
                    dos.write(img);
                }
            }
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseXml(File file) {
        System.load(SavePath.opencvPath);
        Document document = XmlUtil.getDocument(file);
        Element rootElement = document.getRootElement();
        Iterator<Element> it = rootElement.elementIterator();
        int readingIndex = 0;
        while (it.hasNext()) {
            Element e = it.next();
            if (e.getName().equals("readingSession")) {
                readingIndex++;
                StringBuilder nodulePath = new StringBuilder();
                nodulePath.append(file.getParentFile().getParent());
                nodulePath.append("\\mask");
                nodulePath.append("\\nodule-");
                nodulePath.append(readingIndex);
                nodulePath.append(".bin");
                readingSession(e, nodulePath.toString());
            }
        }
    }

    private void readingSession(Element parent, String nodulePath) {
        Iterator<Element> it = parent.elementIterator();
        objNum = 1;
        while (it.hasNext()) {
            Element e = it.next();
            if (e.getName().equals("unblindedReadNodule")) {
                unblindedReadNodule(e, nodulePath);
            }
        }
    }

    private void unblindedReadNodule(Element parent, String nodulePath) {
        Element characteristics = parent.element("characteristics");
        if (characteristics != null) {
            File noduleFile = new File(nodulePath);
            if (!noduleFile.exists()) initMask(noduleFile);
            Iterator<Element> it = parent.elementIterator();
            while (it.hasNext()) {
                Element e = it.next();
                if (e.getName().equals("roi")) {
                    roi(e, noduleFile, objNum);
                }
            }
            objNum++;
        }
    }

    private void roi(Element parent, File noduleFile, byte objNum) {
        List<Position> positions = new ArrayList<>();
        Iterator<Element> it = parent.elementIterator();
        int zIndex = 0; //imgIndex下标1开始
        while (it.hasNext()) {
            Element e = it.next();
            if (zIndex == 0) {
                Element imageZposition = parent.element("imageZposition");
                double zPosition = Double.valueOf(imageZposition.getText());
                String dcmDirPath = noduleFile.getParentFile().getParent()+"\\origin";
                File dcmDir = new File(dcmDirPath);
                for (File file : dcmDir.listFiles()) {
                    HashMap<String, Object> dcmData = DcmHandler.handle(file, false);
                    if (zPosition == DcmInfoReader.readZPosition(dcmData)) {
                        zIndex = DcmInfoReader.readImgIndex(dcmData);
                        break;
                    }
                }
            }
            if (e.getName().equals("edgeMap")) {
                Element xCoord = e.element("xCoord");
                Element yCoord = e.element("yCoord");
                int x = Integer.valueOf(xCoord.getText());
                int y = Integer.valueOf(yCoord.getText());
                Position position = new Position(x, y);
                positions.add(position);
            }
        }
        appendMask(noduleFile, positions, zIndex, objNum);
    }

    public void appendMask(File noduleFile, List<Position> positions, int zIndex, byte objNum) {
        int[] info = getImageSize(noduleFile);
        int zSum = info[0];
        int cols = info[1];
        int rows = info[2];
        try {
            RandomAccessFile raf = new RandomAccessFile(noduleFile, "rw");
            byte[] img = new byte[cols*rows];
            raf.seek((zIndex-initialIndex)*cols*rows);
            raf.read(img);
            for (Position position : positions) {
                int x = position.getX();
                int y = position.getY();
                img[y*cols+x] = objNum;
            }

            Mat imgMat = new Mat(cols, rows, CvType.CV_8UC1);
            int index =0;
            for(int x = 0; x < cols; x++) {
                for (int y = 0; y < rows; y++) {
                    byte b = img[index++];
                    if (b == objNum)
                        imgMat.put(x, y, 1);
                    else
                        imgMat.put(x, y, 0);
                }
            }
            Mat hierarchy = new Mat();
            Scalar color =  new Scalar(250, 250, 255);
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(imgMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            for (int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(imgMat, contours, i, color, 1, 8, hierarchy, 0, new Point());
            }

            raf.seek((zIndex-initialIndex)*cols*rows);
            raf.write(img);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMask(File noduleFile) {
        int[] info = getImageSize(noduleFile);
        int zSum = info[0];
        int cols = info[1];
        int rows = info[2];
        try {
            DataOutputStream dos = new DataOutputStream((new FileOutputStream(noduleFile)));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] getImageSize(File noduleFile) {
        int zSum = 0;
        int cols = 0;
        int rows = 0;
        String dcmDirPath = noduleFile.getParentFile().getParent()+"\\origin";
        File dcmDir = new File(dcmDirPath);
        for (File file : dcmDir.listFiles()) {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
            if (suffix.equals("dcm")) zSum++;
        }
        for (File file : dcmDir.listFiles()) {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
            if (suffix.equals("dcm")) {
                HashMap<String, Object> dcmData = DcmHandler.handle(file, false);
                cols = DcmInfoReader.readColumns(dcmData);
                rows = DcmInfoReader.readRows(dcmData);
                break;
            }
        }
        int[] info = new int[3];
        info[0] = zSum;
        info[1] = cols;
        info[2] = rows;
        return info;
    }
}
