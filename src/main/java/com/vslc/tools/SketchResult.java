package com.vslc.tools;

import com.vslc.enums.MaskTypeEnum;
import com.vslc.model.DcmInfo;
import com.vslc.model.Position;
import com.vslc.VO.FileTreeVO;
import com.vslc.model.Sequence;
import com.vslc.tools.xml.InfoXml;
import net.sf.json.JSONObject;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.*;
import java.util.*;

/**
 * 标注结果工具类
 * Created by chenlele
 * 2018/4/20 17:08
 */
public class SketchResult {

    /**
     * 获取所有标注坐标
     * @param maskPath
     * @return
     */
    public static List<Map<String, Object>> getPositionList(String maskPath) {
        System.load(SavePath.opencvPath);
        List<Map<String, Object>> result = new ArrayList<>();
        File maskFile = new File(maskPath);
        String xmlPath = maskFile.getParentFile().getParent() + "\\info.xml";
        File xmlFile = new File(xmlPath);
        Map<String, Integer> imageSize = InfoXml.getImageSize(xmlFile);
        int cols = imageSize.get("x");
        int rows = imageSize.get("y");
        int zSum = imageSize.get("z");
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(maskFile));
            for (int z = 0; z < zSum; z++) {
                Map<String, Object> single = new HashMap<>();
                List<List<Position>> positionList = new ArrayList<>();
                byte[] img = new byte[cols*rows];
                dis.read(img);
                Mat imgMat = new Mat(cols, rows, CvType.CV_8UC1);
                imgMat.put(0, 0, img);
                Mat hierarchy = new Mat();
                List<MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(imgMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
                for (int i = 0; i < contours.size(); i++) {
                    List<Position> positions = new ArrayList<>();
                    for (int j = 0; j < contours.get(i).total(); j++) {
                        Double x = contours.get(i).toList().get(j).x;
                        Double y = contours.get(i).toList().get(j).y;
                        byte num = (byte) imgMat.get(y.intValue(), x.intValue())[0];
                        Position position = new Position(num, x.intValue(), y.intValue());
                        positions.add(position);
                    }
                    positionList.add(positions);
                }
                single.put("positionList", positionList);
                result.add(single);
            }
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取标注结果列表
     * key：fileTree（easyui tree）
     * key：numberList（结节号坐标）
     * @param maskPath
     * @return
     */
    public static Map<String, Object> getSketchList(String maskPath) {
        Map<String, Object> result = new HashMap<>();
        List<FileTreeVO> fileTreeVOList = new ArrayList<>(); //文件树
        List<List<Position>> numberList = null; //结节号显示
        File maskDir = new File(maskPath);
        String dcmPath = maskDir.getParent() + "\\DCM";
        File dcmDir = new File(dcmPath);
        File[] dcms = dcmDir.listFiles();
        for (File file : maskDir.listFiles()) {
            FileTreeVO fileTreeVO = new FileTreeVO();
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
            if (suffix.equals("bin") && !fileName.contains(MaskTypeEnum.MASK_MATRIX.getName())) {
                for (MaskTypeEnum maskType : MaskTypeEnum.values()) {
                    if (fileName.contains(maskType.getName())) {
                        boolean readNumList = false; //结节号展示
                        if (maskType.getParentNode() != null) readNumList = true;
                        Map<String, Object> objNumMap = getObjNumList(file, readNumList);
                        Map<Byte, Integer> obj = (Map<Byte, Integer>) objNumMap.get("objMap"); //mask中所有结节号
                        if (readNumList) numberList = (List<List<Position>>) objNumMap.get("numberList");
                        //有父节点
                        if (maskType.getParentNode() != null) {
                            List<FileTreeVO> childObjectList = new ArrayList<>();

                            //多结节筛选时读法
                            if (fileName.contains("-")) {
                                String index = fileName.substring(fileName.indexOf("-")+1, fileName.indexOf("-")+2);
                                fileTreeVO.setText(maskType.getParentNode() + index);
                                //通常读法
                            } else {
                                fileTreeVO.setText(maskType.getParentNode());
                            }

                            int minPosition = dcms.length; //父节点取最靠前位置
                            for (Map.Entry<Byte, Integer> entry : obj.entrySet()) {
                                FileTreeVO childObject = new FileTreeVO();
                                Map<String, Object> attributes = new HashMap<>();
                                attributes.put("sketchFile", fileName);
                                attributes.put("sketchType", maskType.getType());
                                attributes.put("sketchNum", entry.getKey());
                                int position = entry.getValue();
                                attributes.put("position", position);
                                if (minPosition > position) minPosition = position;
                                childObject.setText(maskType.getNode() + Byte.toString(entry.getKey()));
                                childObject.setAttributes(attributes);
                                childObjectList.add(childObject);
                            }
                            Map<String, Object> attributes = new HashMap<>();
                            attributes.put("sketchFile", fileName);
                            attributes.put("sketchType", maskType.getType());
                            attributes.put("sketchNum", null);
                            attributes.put("position", minPosition);
                            fileTreeVO.setAttributes(attributes);
                            fileTreeVO.setChildren(childObjectList);
                        } else {
                            for (Map.Entry<Byte, Integer> entry : obj.entrySet()) {
                                Map<String, Object> attributes = new HashMap<>();
                                attributes.put("sketchFile", fileName);
                                attributes.put("sketchType", maskType.getType());
                                attributes.put("sketchNum", entry.getKey().intValue());
                                attributes.put("position", dcms.length/2);
                                fileTreeVO.setText(maskType.getNode());
                                fileTreeVO.setAttributes(attributes);
                            }
                        }
                        break;
                    }
                }
                fileTreeVOList.add(fileTreeVO);
            }
        }
        result.put("fileTree", fileTreeVOList);
        if (numberList != null) result.put("numberList", numberList);
        return result;
    }

    /**
     * 保存标注结果
     * @param sequence
     * @param it 前端传来的坐标
     * @param sketchFile mask文件
     * @param sketchNum 结节号
     */
    public static void saveSketch(Sequence sequence, Iterator<Object> it, String sketchFile, Byte sketchNum) {
        System.load(SavePath.opencvPath);
        String maskPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, sketchFile);
        File maskFile = new File(maskPath);
        String xmlPath = PathUtil.getTypeXmlPath(sequence, SavePath.rootPath);
        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists()) {
            String inspePath = SavePath.rootPath + sequence.getInspection().getSavePath();
            InfoXml.ergodicInspe(new File(inspePath));
        }
        Map<String, Integer> imageSize = InfoXml.getImageSize(xmlFile);
        int cols = imageSize.get("x");
        int rows = imageSize.get("y");
        if (!maskFile.exists()) initMask(maskFile, imageSize);
        try {
            RandomAccessFile raf = new RandomAccessFile(maskFile, "rw");
            while (it.hasNext()) {
                JSONObject sketchObject = (JSONObject) it.next();
                List<List<JSONObject>> positionsArray = (List<List<JSONObject>>) sketchObject.get("positionsArray");
                int zIndex = sketchObject.getInt("index");
                Mat imgMat = new Mat(cols, rows, CvType.CV_8UC1);
                byte[] img = new byte[cols*rows];
                raf.seek(zIndex*cols*rows);
                raf.read(img);
                int index = 0;
                for (int x = 0; x < cols; x++) {
                    for (int y = 0; y < rows; y++) {
                        imgMat.put(y, x, 0);
                        if (img[index] == sketchNum) img[index] = 0;
                        index++;
                    }
                }
                System.out.println(zIndex);
                for (List<JSONObject> positionList : positionsArray) {
                    for (JSONObject positionObj : positionList) {
                        int x = positionObj.getInt("x");
                        int y = positionObj.getInt("y");
                        imgMat.put(y, x, 1);
                    }
                }
                Mat hierarchy = new Mat();
                Scalar color =  new Scalar(250, 250, 255);
                List<MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(imgMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
                for (int i = 0; i < contours.size(); i++) {
                    Imgproc.drawContours(imgMat, contours, i, color, 1, 8, hierarchy, 0, new Point());
                }
                if (contours.size() > 0) {
                    Imgcodecs.imwrite("D:\\saveTest\\"+(zIndex+1)+".jpg", imgMat);
                    index = 0;
                    for (int y = 0; y < rows; y++) {
                        for (int x = 0; x < cols; x++) {
                            byte b = (byte) imgMat.get(y, x)[0];
                            if (b != '\0') img[index] = sketchNum;
                            index++;
                        }
                    }
                }
                raf.seek(zIndex*cols*rows);
                raf.write(img);
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除标注结果
     * @param sequence
     * @param sketchFile mask文件
     * @param sketchNum 结节号
     */
    public static void deleteSketch(Sequence sequence, String sketchFile, Byte sketchNum) {
        String maskPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, sketchFile);
        File maskFile = new File(maskPath);
        if (!maskFile.exists()) return;
        for (MaskTypeEnum maskTypeEnum : MaskTypeEnum.values()) {
            if (sketchFile.contains(maskTypeEnum.getName())) {
                if (maskTypeEnum.getParentNode() == null || sketchNum == null) {
                    maskFile.delete();
                    return;
                }
            }
        }
        String xmlPath = maskFile.getParentFile().getParent() + "\\info.xml";
        File xmlFile = new File(xmlPath);
        Map<String, Integer> imageSize = InfoXml.getImageSize(xmlFile);
        int cols = imageSize.get("x");
        int rows = imageSize.get("y");
        int zSum = imageSize.get("z");
        boolean singleObj = true;
        try {
            RandomAccessFile raf = new RandomAccessFile(maskFile, "rw");
            for (int z = 0; z < zSum; z++) {
                byte[] img = new byte[cols*rows];
                raf.seek(z*cols*rows);
                raf.read(img);
                int index = 0;
                for (int y = 0; y < rows; y++) {
                    for (int x = 0; x < cols; x++) {
                        byte b = img[index];
                        if (b != '\0') {
                            if (b == sketchNum) {
                                img[index] = 0;
                            } else {
                                if (!singleObj) singleObj = false;
                            }
                        }
                        index++;
                    }
                }
                raf.seek(z*cols*rows);
                raf.write(img);
            }
            raf.close();
            if (singleObj) maskFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sequence
     * @param it 前端传来的坐标
     * @param zIndex
     * @return
     */
    public static Map<String, Object> D2NoduleInfo(Sequence sequence, Iterator<Object> it, int zIndex) {
        Map<String, Object> noduleInfo = new HashMap<>();
        System.load(SavePath.opencvPath);
        DataInputStream matrixDis;
        String matrixPath = PathUtil.getMaskFile(sequence, SavePath.rootPath
                , "matrix.bin");
        File matrixFile = new File(matrixPath);
        File dcmFile = new File(SavePath.rootPath + sequence.getDcmPath());
        DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
        int cols = dcmInfo.getColumns();
        int rows = dcmInfo.getRows();
        float slope = dcmInfo.getSlope();
        float intercept = dcmInfo.getIntercept();
        double ctSum = 0;
        int pixelSum = 0;
        List<Double> cts = new ArrayList<>();

        try {
            matrixDis = new DataInputStream(new FileInputStream(matrixFile));
            for (int i = 0 ; i < 3; i++) matrixDis.readInt();
            byte[] maskImg = new byte[cols*rows];
            byte[] matrixImg = new byte[cols*rows*2];
            while (it.hasNext()) {
                JSONObject obj = (JSONObject) it.next();
                int x = obj.getInt("x");
                int y = obj.getInt("y");
                maskImg[y*cols+x] = 1;
            }
            matrixDis.skip((zIndex-1)*cols*rows);
            matrixDis.read(matrixImg);
            Mat imgMat = new Mat(cols, rows, CvType.CV_8UC1);
            imgMat.put(0, 0, maskImg);
            Scalar color =  new Scalar(250, 250, 255);
            Mat hierarchy = new Mat();
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(imgMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            for (int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(imgMat, contours, i, color, -1, 8, hierarchy, 0, new Point());
                Imgcodecs.imwrite("D:\\noduleTest\\1.jpg", imgMat);
                int matrixIndex = 0;
                for (int y = 0; y < rows; y++) {
                    for (int x = 0;x < cols; x++) {
                        byte b = (byte) imgMat.get(y, x)[0];
                        if (b != '\0') {
                            short pixelValue = BitConverter.toShort(
                                    new byte[] {matrixImg[matrixIndex], matrixImg[matrixIndex+1]});
                            double ct = pixelValue*slope + intercept;
                            ctSum += ct;
                            pixelSum++;
                            cts.add(ct);
                        }
                        matrixIndex += 2;
                    }
                }
            }
            matrixDis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        noduleInfo.put("avgCT", ctSum/pixelSum);
        noduleInfo.put("maxCT", Collections.max(cts));
        noduleInfo.put("minCT", Collections.min(cts));
        return noduleInfo;
    }

    /**
     * 三维结节信息
     * @param sequence
     * @param sketchNum 结节号
     * @return
     */
    public static Map<String, Object> D3NoduleInfo(Sequence sequence, Byte sketchNum) {
        Map<String, Object> noduleInfo = new HashMap<>();
        System.load(SavePath.opencvPath);
        DataInputStream maskDis;
        DataInputStream matrixDis;
        String maskPath = PathUtil.getMaskFile(sequence, SavePath.rootPath
                , "nodule.bin");
        File maskFile = new File(maskPath);
        String xmlPath = PathUtil.getTypeXmlPath(sequence, SavePath.rootPath);
        File xmlFile = new File(xmlPath);
        String matrixPath = PathUtil.getMaskFile(sequence, SavePath.rootPath
                , "matrix.bin");
        File matrixFile = new File(matrixPath);
        Map<String, Integer> imageSize = InfoXml.getImageSize(xmlFile);
        int cols = imageSize.get("x");
        int rows = imageSize.get("y");
        int zSum = imageSize.get("z");

        File dcmFile = new File(SavePath.rootPath + sequence.getDcmPath());
        DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
        float thickness = dcmInfo.getThickness();
        float intercept = dcmInfo.getIntercept();
        float slope = dcmInfo.getSlope();
        double columnPixelSpacing = dcmInfo.getColumnPixelSpacing();
        double rowPixelSpacing = dcmInfo.getRowPixelSpacing();
        double singleVolume = thickness * columnPixelSpacing * rowPixelSpacing;
        double volumeSum = 0;
        double ctSum = 0;
        int pixelSum = 0;
        String position = null;
        List<Double> diameters = new ArrayList<>();
        List<Double> cts = new ArrayList<>();

        try {
            maskDis = new DataInputStream(new FileInputStream(maskFile));
            matrixDis = new DataInputStream(new FileInputStream(matrixFile));
            for (int i = 0 ; i < 3; i++) matrixDis.readInt();
            double zDiameter = 0;
            for (int z = 0; z < zSum; z++) {
                byte[] maskImg = new byte[cols*rows];
                byte[] matrixImg = new byte[cols*rows*2];
                maskDis.read(maskImg);
                matrixDis.read(matrixImg);
                Mat imgMat = new Mat(cols, rows, CvType.CV_8UC1);
                int index = 0;
                for (int x = 0; x < cols; x++) {
                    for (int y = 0; y < rows; y++) {
                        byte b = maskImg[index++];
                        if (b == sketchNum)
                            imgMat.put(x, y, 1);
                        else
                            imgMat.put(x, y, 0);
                    }
                }
                Scalar color =  new Scalar(250, 250, 255);
                Mat hierarchy = new Mat();
                List<MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(imgMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
                for (int i = 0; i < contours.size(); i++) {
                    zDiameter += thickness;
                    Imgproc.drawContours(imgMat, contours, i, color, -1, 8, hierarchy, 0, new Point());
                    Imgcodecs.imwrite("D:\\noduleTest\\"+z+".jpg", imgMat);
                    int matrixIndex = 0;
                    for (int y = 0; y < rows; y++) {
                        double xDiameter = 0;
                        double yDiameter = 0;
                        for (int x = 0;x < cols; x++) {
                            byte b1 = (byte) imgMat.get(y, x)[0]; //正方向
                            byte b2 = (byte) imgMat.get(x, y)[0];

                            if (b1 != '\0') {
                                if (position == null) {
                                    if (x < cols/2)
                                        position = "右";
                                    else
                                        position = "左";
                                }
                                volumeSum += singleVolume;
                                xDiameter += columnPixelSpacing;
                                short pixelValue = BitConverter.toShort(
                                        new byte[] {matrixImg[matrixIndex], matrixImg[matrixIndex+1]});
                                double ct = pixelValue*slope + intercept;
                                cts.add(ct);
                                ctSum+=ct;
                                pixelSum++;
                            }
                            matrixIndex += 2;

                            if (b2 != '\0') {
                                yDiameter += rowPixelSpacing;
                            }
                        }
                        diameters.add(xDiameter>=yDiameter?xDiameter:yDiameter);
                    }
                }
            }
            diameters.add(zDiameter - thickness);
            maskDis.close();
            matrixDis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        noduleInfo.put("sketchNum", sketchNum);
        noduleInfo.put("position", position);
        noduleInfo.put("diameter", Collections.max(diameters));
        noduleInfo.put("volume", volumeSum);
        noduleInfo.put("avgCT", ctSum/pixelSum);
        noduleInfo.put("maxCT", Collections.max(cts));
        noduleInfo.put("minCT", Collections.min(cts));
        return noduleInfo;
    }

    /**
     * 扫描mask
     * @param maskFile
     * @param readNumList
     * @return
     */
    public static Map<String, Object> getObjNumList(File maskFile, boolean readNumList) {
        Map<String, Object> result = new HashMap<>(); //返回结果
        Map<Byte, Integer> obj = new HashMap<>(); //key结节号 value位置（总结节数）
        List<List<Position>> numberList = new ArrayList<>(); //结节号坐标
        String xmlPath = maskFile.getParentFile().getParent() + "\\info.xml";
        File xmlFile = new File(xmlPath);
        Map<String, Integer> imageSize = InfoXml.getImageSize(xmlFile);
        int cols = imageSize.get("x");
        int rows = imageSize.get("y");
        int zSum = imageSize.get("z");
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(maskFile));
            for (int z = 0; z < zSum; z++) {
                List<Position> positions = new ArrayList<>();
                byte[] img = new byte[cols*rows];
                dis.read(img);
                int index = 0;
                List<Byte> nums = new ArrayList<>();
                for (int y = 0; y < rows; y++) {
                    if (!readNumList) {
                        if (obj.size() > 0) break;
                    }
                    for (int x = 0; x < cols; x++) {
                        byte b = img[index++];
                        if (b != '\0') {
                            boolean objExist = false;
                            for (Map.Entry<Byte, Integer> entry : obj.entrySet()) {
                                if (b == entry.getKey()) {
                                    objExist = true;
                                    break;
                                }
                            }
                            if (!objExist) obj.put(b, z+1);

                            if (readNumList) {
                                boolean numExist = false;
                                for (Byte num : nums) {
                                    if (b == num) {
                                        numExist = true;
                                        break;
                                    }
                                }
                                if (!numExist) {
                                    Position position = new Position(b, x, y);
                                    positions.add(position);
                                    nums.add(b);
                                }
                            }
                        }
                    }
                }
                numberList.add(positions);
            }
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("objMap", obj);
        result.put("numberList", numberList);
        return result;
    }

    /**
     * 保存mask时初始化
     * @param maskFile
     * @param imageSize
     */
    public static void initMask(File maskFile, Map<String, Integer> imageSize) {
        int cols = imageSize.get("x");
        int rows = imageSize.get("y");
        int zSum = imageSize.get("z");
        try {
            DataOutputStream dos = new DataOutputStream((new FileOutputStream(maskFile)));
            if (zSum > 400 || cols > 600) {
                for (int z = 0; z < zSum; z++) {
                    byte[] init = new byte[cols*rows];
                    dos.write(init);
                    dos.flush();
                }
            } else {
                byte[] init = new byte[zSum*cols*rows];
                dos.write(init);
                dos.flush();
            }
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分析是否有标注结果
     * @param sequence
     * @return
     */
    public static boolean isSketch(Sequence sequence) {
        String maskDirPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, null);
        File maskDir = new File(maskDirPath);
        if (!maskDir.exists()) return false;
        File[] masks = maskDir.listFiles();
        int maskCount = 0;
        for (File maskFile : masks) {
            String fileName = maskFile.getName();
            for (MaskTypeEnum type : MaskTypeEnum.values()) {
                //三维矩阵mask不是标注文件
                if (fileName.contains(type.getName())
                        && !fileName.contains(MaskTypeEnum.MASK_MATRIX.getName())) {
                    maskCount++;
                }
            }
        }
        if (maskCount > 0) return true;
        else return false;
    }
}
