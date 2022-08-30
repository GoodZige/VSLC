package com.vslc.tools.dicom;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import com.vslc.model.DcmInfo;
import com.vslc.model.Matrix;
import com.vslc.model.Sequence;
import com.vslc.tools.BitConverter;
import com.vslc.tools.FileUtil;
import com.vslc.tools.PathUtil;
import com.vslc.tools.SavePath;

import javax.imageio.ImageIO;

/**
 * dicom图像矩阵处理
 */
public class ImgMatrixHandler {

    public static Map<String, Object> getMatrix(Sequence sequence, int type, int matrixIndex, Integer dcmIndex) {
        Map<String, Object> result = new HashMap<>();
        String matrixPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, "matrix.bin");
        File matrixFile = new File(matrixPath);
        String sqPath = sequence.getDcmPath();
        String suffix = sqPath.substring(sqPath.lastIndexOf("."));
        File dcmFile;
        if (dcmIndex != null)
            dcmFile = new File(PathUtil.getDcmFile(sequence, SavePath.rootPath, dcmIndex + suffix));
        else
            dcmFile = new File(SavePath.rootPath + sqPath);
        DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
        int fileNum = sequence.getFileNum();
        int cols = dcmInfo.getColumns();
        int rows = dcmInfo.getRows();
        long actualSize = matrixFile.length(); //文件实际大小
        long enableSize = cols*rows*fileNum*2; //理想执行大小

        short[][] imgArr = null;
        Matrix matrix = null;

        if(actualSize >= enableSize) {
            if (type == 0) {
                imgArr = D3MatrixToD2MatrixForZ(matrixFile, matrixIndex);
            } else if (type == 1) {
                imgArr = D3MatrixToD2MatrixForY(matrixFile, matrixIndex);
            } else if (type == 2) {
                imgArr = D3MatrixToD2MatrixForX(matrixFile, matrixIndex);
            }
//            Float thickness = dcmInfo.getThickness();
//            if (thickness != null) {
//                if (thickness > 1) {
//                    imgArr = fillMatrix(imgArr, (int) Math.ceil(thickness / 0.7));
//                }
//            }
            if (imgArr != null) matrix = D2MatrixToBase64(imgArr);
        } else {
            String transferId = dcmInfo.getTransferId();
            if (transferId.equals("1.2.840.10008.1.2") ||
                    transferId.equals("1.2.840.10008.1.2.1") ||
                    transferId.equals("1.2.840.10008.1.2.2")) {
                dcmInfo = new DcmInfo(DcmHandler.handle(dcmFile, true));
            } else {
                dcmInfo = new DcmInfo(DcmHandler.undicom(dcmFile, true));
            }
            if (type == 0) imgArr = dcmInfo.getImgArr();
            if (imgArr != null) matrix = D2MatrixToBase64(imgArr);
        }

        result.put("imgMatrix", matrix.getImgBase64());
        result.put("imgWidth", matrix.getWidth());
        result.put("imgHeight", matrix.getHeight());
        result.put("imgSlope", dcmInfo.getSlope());
        result.put("imgIntercept", dcmInfo.getIntercept());
        result.put("imgBitsStored", dcmInfo.getBitsStored());
        result.put("imgWindowCenter", dcmInfo.getWindowCenter());
        result.put("imgWindowWidth", dcmInfo.getWindowWidth());
        result.put("imgMinPixelValue", dcmInfo.getMinVal());
        result.put("imgMaxPixelValue", dcmInfo.getMaxVal());
        result.put("imgColumnPixelSpacing", dcmInfo.getColumnPixelSpacing());
        result.put("imgRowPixelSpacing", dcmInfo.getRowPixelSpacing());
        return result;
    }

    /**
     * X轴不变读取矩阵
     * 失状位
     * @param matrixFile
     * @param x
     * @return
     */
    public static short[][] D3MatrixToD2MatrixForX(File matrixFile, int x) {
        short[][] result = null;
        if(matrixFile.exists()) {
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(matrixFile));
                int zSum = dis.readInt();
                int cols = dis.readInt();
                int rows = dis.readInt();
                result = new short[rows][zSum];
                byte[] img = new byte[cols*rows*2];
                for(int z = 0; z < zSum; z++) {
                    dis.read(img);
                    int index = 2*(x-1);
                    for(int y = 0; y < rows; y++) {
                        result[y][z] = BitConverter.toShort(new byte[] {img[index],img[index+1]});
                        index += cols*2;
                    }
                }
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Y轴不变读取数据
     * 冠状位
     * @param matrixFile
     * @param y
     * @return
     */
    public static short[][] D3MatrixToD2MatrixForY(File matrixFile, int y) {
        short[][] result = null;
        if(matrixFile.exists()) {
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(matrixFile));
                int zSum = dis.readInt();
                int cols = dis.readInt();
                int rows = dis.readInt();
                result = new short[cols][zSum];
                byte[] img = new byte[cols*2];
                for(int z = 0; z < zSum; z++) {
                    dis.skip((y-1)*cols*2);
                    dis.read(img);
                    int index = 0;
                    for(int x = 0; x < cols; x++) {
                        result[x][z] = BitConverter.toShort(new byte[] {img[index], img[index+1]});
                        index += 2;
                    }
                    dis.skip((rows-y)*cols*2);
                }
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Z轴不变读取数据
     * 横断位
     * @param matrixFile
     * @param z
     * @return
     */
    public static short[][] D3MatrixToD2MatrixForZ(File matrixFile, int z) {
        short[][] result = null;
        if(matrixFile.exists()) {
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(matrixFile));
                int zSum = dis.readInt();
                int cols = dis.readInt();
                int rows = dis.readInt();
                result = new short[cols][rows];
                byte[] img = new byte[cols*rows*2];
                dis.skip(((z-1)*rows*cols)*2);
                dis.read(img);
                int index = 0;
                for(int y = 0; y < rows; y++) {
                    for(int x = 0; x < cols; x++) {
                        result[x][y] = BitConverter.toShort(new byte[] {img[index], img[index+1]});
                        index+=2;
                    }
                }
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void seriesToD3Matrix(String seriesPath) {
        File dcmDir = new File(seriesPath + "\\DCM");
        File matrixDir = new File(seriesPath + "\\MASK");
        File matrixFile = new File(matrixDir + "\\matrix.bin");
        File tempDir = new File(seriesPath + "\\mulTemp");

        try {
            if (matrixFile.exists()) return;
            if (!dcmDir.exists()) return;
            if (!matrixDir.exists()) matrixDir.mkdirs();
            if (tempDir.exists()) FileUtil.delete(tempDir);

            File[] dcms = dcmDir.listFiles();
            DcmInfo dcmInfo = new DcmInfo(dcms[0], false);

            //如果传输语法不是无损压缩就解压dicom
            String transferId = dcmInfo.getTransferId();
            if (!transferId.equals("1.2.840.10008.1.2") &&
                    !transferId.equals("1.2.840.10008.1.2.1") &&
                    !transferId.equals("1.2.840.10008.1.2.2")) {
                tempDir.mkdirs();
                Dcm2Dcm.main(new String[] {dcmDir.getAbsolutePath(), tempDir.getAbsolutePath()});
            }

            List<File> dcmFileList;
            if (tempDir.exists())
                dcmFileList = Arrays.asList(tempDir.listFiles());
            else
                dcmFileList = Arrays.asList(dcms);

            dcmFileList.sort((file1, file2) -> {
                String fileName1 = file1.getName();
                String fileName2 = file2.getName();
                int index1 = Integer.valueOf(fileName1.substring(0, fileName1.lastIndexOf(".")));
                int index2 = Integer.valueOf(fileName2.substring(0, fileName2.lastIndexOf(".")));
                return index1 - index2;
            });

            DataOutputStream dos = new DataOutputStream(new FileOutputStream(matrixFile));

            int zSum = dcmFileList.size();
            int cols = dcmInfo.getColumns();
            int rows = dcmInfo.getRows();
            dos.writeInt(zSum);
            dos.writeInt(cols);
            dos.writeInt(rows);
            dos.flush();

            for (File dcmFile : dcmFileList) {
                HashMap<String, Object> dcmData = DcmHandler.handle(dcmFile, true);
                short[][] imgArr = DcmInfoReader.readImgArr(dcmData);
                byte[] val = new byte[cols*rows*2];
                int index=0;
                for(int y = 0; y < rows; y++) {
                    for(int x = 0; x < cols; x++) {
                        byte[] curBytes = BitConverter.toBytes(imgArr[x][y]);
                        val[index] = curBytes[0];
                        val[index+1] = curBytes[1];
                        index+=2;
                    }
                }
                dos.write(val);
                dos.flush();
            }
            dos.close();
            if (tempDir.exists()) FileUtil.delete(tempDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static short[][] fillMatrix(short[][] src, int beishu){
        if(src == null) return null;
        int width = src.length;
        int height = src[0].length;
        if(width > height) {
            short[][] ret = new short[width][(height-1)*beishu];
            for(int y = 0; y < height -1 ; y++) {
                short[] chazhi = new short[width];
                for(int x = 0; x < width; x++) {
                    chazhi[x] = (short) ((short) (src[x][y]-src[x][y+1])/beishu);
                }

                for(int i = 0; i < beishu; i++) {
                    for(int x = 0; x < width; x++) {
                        ret[x][y*beishu+i] = (short) (src[x][y]-chazhi[x]*i);
                    }
                }
            }
            return ret;
        }
        return src;
    }

    public static short[][] fillMatrix2(short[][] src, int beishu){
        if(src == null) return null;
        int width = src.length;
        int height = src[0].length;
        if(width > height) {
            short[][] ret = new short[width][height*beishu];
            for(int y=0;y<height;y++) {
                for(int i=0;i<beishu;i++) {
                    for(int x=0;x<width;x++) {
                        ret[x][y*beishu+i] = src[x][y];
                    }
                }
            }
            return ret;
        }
        return src;
    }

    public static float handlerPixel(int oldPixel, int windowWidth, int windowCenter, float slope, float intercept) {
        float newPixel = oldPixel * slope + intercept;
        float fSlope;
        float fShift;
        float fValue;

        fShift = windowCenter - windowWidth / 2.0f;
        fSlope = 255.0f / windowWidth;

        fValue = ((newPixel) - fShift) * fSlope;
        if (fValue < 0)
            fValue = 0;
        else if (fValue > 255)
            fValue = 255;
        return fValue;
    }

    public static Matrix D2MatrixToBase64(short[][] imgArr) {
        Base64.Encoder encoder = Base64.getEncoder();
        int col = imgArr.length;
        int row = 0;
        if(col>0) row = imgArr[0].length;

        byte[] data = new byte[row*col*2];
        int index=0;
        for(int y = 0; y < row; y++) {
            for(int x = 0; x < col; x++) {
                byte[] bytes = BitConverter.toBytes(imgArr[x][y]);
                data[index] = bytes[0];
                data[index+1] = bytes[1];
                index += 2;
            }
        }
        return new Matrix(encoder.encodeToString(data), col, row);
    }

    public static void transferJpg(File dcmFile, File jpgFile, Integer winWidth, Integer winCenter) {
        DcmInfo dcmInfo = new DcmInfo(dcmFile, true);
        short[][] sltArr =  dcmInfo.getImgArr();
        if (sltArr != null) {
            ImgMatrixHandler.D2MatrixToJPG(dcmInfo, jpgFile, winWidth, winCenter);
        } else {
            int rows = dcmInfo.getRows();
            int columns = dcmInfo.getColumns();
            ImgMatrixHandler.generalToJpg(rows, columns, dcmFile, jpgFile);
        }
    }

    public static void D2MatrixToJPG(DcmInfo dcmInfo, File jpgFile, Integer winWidth, Integer winCenter) {
        short[][] imgArr = dcmInfo.getImgArr();
        int cols = dcmInfo.getColumns();
        int rows = dcmInfo.getRows();
        if (winWidth == null) winWidth = dcmInfo.getWindowWidth();
        if (winCenter == null) winCenter = dcmInfo.getWindowCenter();
        float slope = dcmInfo.getSlope();
        float intercept = dcmInfo.getIntercept();
        BufferedImage bi = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        for(int y = 0;y < rows; y++) {
            for(int x = 0; x < cols; x++) {
                int curColor = (int) handlerPixel(imgArr[x][y], winWidth, winCenter, slope, intercept);
                bi.setRGB(x, y, new Color(curColor, curColor, curColor).getRGB());
            }
        }
        createImage(jpgFile, bi);
    }

    public static void generalToJpg(int rows, int columns, File dcmFile, File jpgFile) {
        try {
            BufferedImage artworkBuffered = ImageIO.read(dcmFile);
            BufferedImage ThumbnailsBuffered = new BufferedImage(columns, rows,
                    BufferedImage.TYPE_INT_RGB);
            ThumbnailsBuffered.getGraphics().drawImage(artworkBuffered, 0, 0,
                    columns, rows, null);
            ImageIO.write(ThumbnailsBuffered, "jpg", jpgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createImage(File jpgFile, BufferedImage bi) {
        try {
//            FileOutputStream fos = new FileOutputStream(jpgFile);
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
//            encoder.encode(bi);
//            bos.close();
            ImageIO.write(bi, "jpg", new FileOutputStream(jpgFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
