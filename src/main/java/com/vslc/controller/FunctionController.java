package com.vslc.controller;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.vslc.VO.ImportVO;
import com.vslc.enums.TransferMethodEnum;
import com.vslc.enums.TransferStatusEnum;
import com.vslc.model.*;
import com.vslc.service.IDataImportService;
import com.vslc.service.IInspectionService;
import com.vslc.service.ISequenceService;
import com.vslc.tools.*;
import com.vslc.tools.array.ExportDataHandler;
import com.vslc.tools.dicom.DcmHandler;
import com.vslc.tools.dicom.DcmInfoReader;
import com.vslc.tools.dicom.ImgMatrixHandler;
import com.vslc.tools.xml.InfoXml;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Controller
@RequestMapping(value = "/function")
public class FunctionController {

    @Resource
    private IInspectionService inspectionService;

    @Resource
    private ISequenceService sequenceService;

    @Resource
    private IDataImportService dataImportService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * 解析dicom并以流的方式返回jpg图像
     * 此处会检测三维矩阵是否存在并满足要求，不存在则开启线程生成三维矩阵mask
     * @param sequenceID
     * @param matrixIndex 三维矩阵下标（无则取1）
     * @param dcmIndex dicom下标（无则默认取首个dicom文件）
     * @param winWidth 窗宽
     * @param winCenter 窗位
     * @param response response.setContentType("image/jpeg");
     * @throws IOException
     */
    @RequestMapping(value="/displayDcm")
    public void displayDcm(@RequestParam(value="sequenceID") int sequenceID
            ,@RequestParam(value="matrixIndex", required = false) Integer matrixIndex
            ,@RequestParam(value="dcmIndex", required = false) Integer dcmIndex
            ,@RequestParam(value="winWidth", required = false) Integer winWidth
            ,@RequestParam(value="winCenter", required = false) Integer winCenter
            ,HttpServletResponse response) throws IOException {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        if (sequence.getSequenceNum().equals("997")) return;
        String sqPath = sequence.getDcmPath();
        String dcmSuffix = sqPath.substring(sqPath.lastIndexOf("."));

        File dcmFile;
        if (dcmIndex != null)
            dcmFile = new File(PathUtil.getDcmFile(sequence, SavePath.rootPath, dcmIndex + dcmSuffix));
        else
            dcmFile = new File(SavePath.rootPath + sqPath);

        DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
        String matrixPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, "matrix.bin");
        File matrixFile = new File(matrixPath);

        //不同图像可能斜率截距不同
        int fileNum = sequence.getFileNum();
        int cols = dcmInfo.getColumns();
        int rows = dcmInfo.getRows();
        long actualSize = matrixFile.length(); //文件实际大小
        long enableSize = cols*rows*fileNum*2; //理想执行大小
        if (winWidth == null) winWidth = dcmInfo.getWindowWidth();
        if (winCenter == null) winCenter = dcmInfo.getWindowCenter();
        float slope = dcmInfo.getSlope();
        float intercept = dcmInfo.getIntercept();
        short[][] imgArr;

        BufferedImage bi = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);

        int index = 0;
        if (matrixFile.exists() && actualSize >= enableSize) {
            DataInputStream dis = new DataInputStream(new FileInputStream(matrixFile));
            for (int i = 0; i < 3; i++) dis.readInt();
            byte[] img = new byte[cols*rows*2];
            if (matrixIndex != null) dis.skip((matrixIndex*rows*cols)*2);
            dis.read(img);
            dis.close();

            imgArr = new short[cols][rows];
            for(int y = 0; y < rows; y++) {
                for(int x = 0; x < cols; x++) {
                    imgArr[x][y] = BitConverter.toShort(new byte[] {img[index], img[index+1]});
                    int curColor = (int) ImgMatrixHandler.handlerPixel(imgArr[x][y], winWidth, winCenter, slope, intercept);
                    bi.setRGB(x, y, new Color(curColor, curColor, curColor).getRGB());
                    index += 2;
                }
            }
        } else {
            if (!matrixFile.exists() && fileNum > 10) {
                String sequencePath = PathUtil.getSeriesDir(sequence, SavePath.rootPath);
                new Thread(() -> ImgMatrixHandler.seriesToD3Matrix(sequencePath)).start();
            }

            HashMap<String, Object> dcmData;
            String transferId = dcmInfo.getTransferId();
            if (transferId.equals("1.2.840.10008.1.2") ||
                    transferId.equals("1.2.840.10008.1.2.1") ||
                    transferId.equals("1.2.840.10008.1.2.2")) {
                dcmData = DcmHandler.handle(dcmFile, true);
            } else {
                dcmData = DcmHandler.undicom(dcmFile, true);
            }
            imgArr = DcmInfoReader.readImgArr(dcmData);

            if (imgArr != null) {
                for(int y = 0; y < rows; y++) {
                    for(int x = 0; x < cols; x++) {
                        int curColor = (int) ImgMatrixHandler.handlerPixel(imgArr[x][y], winWidth, winCenter, slope, intercept);
                        bi.setRGB(x, y, new Color(curColor, curColor, curColor).getRGB());
                        index += 2;
                    }
                }
            }
        }

        response.setContentType("image/jpeg");
        OutputStream out = response.getOutputStream();
        if (imgArr != null) {
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(bi);
//            ImageIO.write(bi, "jpg", out);
        } else {
            BufferedImage artworkBuffered = ImageIO.read(dcmFile);
            BufferedImage ThumbnailsBuffered = new BufferedImage(cols, rows,
                    BufferedImage.TYPE_INT_RGB);
            ThumbnailsBuffered.getGraphics().drawImage(artworkBuffered, 0, 0,
                    cols, rows, null);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(ThumbnailsBuffered);
//            ImageIO.write(ThumbnailsBuffered, "jpg", out);
        }
        out.close();
    }

    /**
     * 上传操作第一步 -> 选择数据上传到服务器
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "/upload",method= RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request
            ,HttpSession session) {
        //声明本次下载状态的记录对象
        FileTransferRecord record = new FileTransferRecord(request);
        User user = (User) session.getAttribute("curUser");
        if (user != null) record.setUserAccount(user.getUserAccount());
        else return new HashMap<>();

        Calendar thisYear = Calendar.getInstance();
        Calendar fileYear = Calendar.getInstance();
        File uploadDir = new File(SavePath.uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();
        //扫描上传空间过期目录
        for (File file : uploadDir.listFiles()) {
            try {
                String fileName = file.getName();
                fileYear.setTime(dateFormat.parse(fileName));
                int span = Integer.valueOf(thisYear.get(Calendar.DAY_OF_YEAR) - fileYear.get(Calendar.DAY_OF_YEAR));
                if (span > 2) FileUtil.delete(file);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        MultipartHttpServletRequest params = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = params.getFiles("fileFolder");//file为文件项的name值
        Map<String, Object> result = dataImportService.upload(files);

        String uploadPath = SavePath.uploadPath + result.get("uploadPath");
        record.setFilePath(uploadPath);
        record.setStatus(TransferStatusEnum.SUCCESS.getCode());
        record.setMethod(TransferMethodEnum.UPLOAD.getCode());
        record.setEndTime(timeFormat.format(new Date()));
        double size = FileUtil.getSizeMB(new File(uploadPath));
        record.setLength(size + "MB");

        printLog(record);
        return result;
    }

    /**
     * 上传操作第二步 -> 选择性导入数据库
     * @param importVO
     * @param session
     */
    @RequestMapping(value = "/import")
    @ResponseBody
    public void dataImport(@RequestBody ImportVO importVO
            ,HttpSession session) {
        User user = (User) session.getAttribute("curUser");
        if (user == null) return;
        String inspectionID = importVO.getInspectionID();
        String uploadPath = importVO.getUploadPath();
        String deletePath = uploadPath.substring(0, uploadPath.indexOf("\\"));
        String hospitalID = importVO.getHospitalID();
        String importPath = uploadPath.substring(uploadPath.indexOf("\\")+1);
        List<ImportVO> importVOList = importVO.getSelections();
        boolean isSketch = false;
        List<String> sequenceNums = new ArrayList<>();
        for (ImportVO selection : importVOList) {
            StringBuilder src = new StringBuilder(SavePath.uploadPath);
            StringBuilder des = new StringBuilder(SavePath.srcPath);
            src.append(uploadPath);
            src.append("\\");
            src.append(selection.getSequenceNum());
            des.append(importPath);
            des.append("\\");
            des.append(selection.getSequenceNum());
            if (selection.getUploadType().contains("i")) {
                FileUtil.copy(src.toString()+"\\DCM",
                        des.toString()+"\\DCM");
            }
            if (selection.getUploadType().contains("m")) {
                FileUtil.copy(src.toString()+"\\MASK",
                        des.toString()+"\\MASK");
                isSketch = true;
                sequenceNums.add(selection.getSequenceNum());
            }
        }
        dataImportService.add(SavePath.srcPath + importPath, hospitalID, user.getUserID());
        if (isSketch) {
            for (String sequenceNum : sequenceNums) {
                Map<String, Object> param = new HashMap<>();
                param.put("inspectionID", inspectionID);
                param.put("sequenceNum", sequenceNum);
                Sequence sequence = sequenceService.findOne(param);
                sequence.setIsSketch(1);
                sequenceService.updateIsSketch(sequence);
            }
            Inspection inspection = new Inspection();
            inspection.setInspectionID(inspectionID);
            inspection.setProcessID(2);
            inspectionService.updateProcessID(inspection);
        }
        FileUtil.delete(new File(SavePath.uploadPath+deletePath));
    }

    //没用
    @RequestMapping(value = "/download")
    public void download(@RequestParam(value="checks", required = false) String[] checks
            ,HttpServletRequest request
            ,HttpServletResponse response
            ,HttpSession session) {
        //声明本次下载状态的记录对象
        FileTransferRecord record = new FileTransferRecord(request);
        User user = (User) session.getAttribute("curUser");
        if (user != null) record.setUserAccount(user.getUserAccount());
        else return;
        StringBuilder zipPath = new StringBuilder(SavePath.exportPath);
        File zipFile;
        String fileName = "";
        String filePath = "";
        if (checks.length == 1) {
            zipPath.append(checks[0]);
            zipPath.append(".zip");
            zipFile = new File(zipPath.toString());
            if (!zipFile.exists())
                ZipUtil.toZip(SavePath.rootPath + inspectionService.findByInspectionID(checks[0]).getSavePath(),
                        zipPath.toString(), true);
            fileName = zipFile.getName();
            filePath = zipPath.toString();
        } else if (checks.length > 1) {
            List<File> fileList = new ArrayList<>();
            for (int i = 0; i < checks.length; i++) {
                fileList.add(new File(SavePath.rootPath + inspectionService.findByInspectionID(checks[i]).getSavePath()));
            }
            while (true) {
                zipPath.append(Integer.toString((int) (Math.random() * 10000 + 1)));
                zipPath.append(".zip");
                zipFile = new File(zipPath.toString());
                if (!zipFile.exists()) break;
            }
            ZipUtil.toZip(fileList, zipPath.toString(), true);
            fileName = zipFile.getName();
            filePath = zipPath.toString();
        }
        //设置响应头和客户端保存文件名
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.setHeader("fileName", fileName);
        //用于记录以完成的下载的数据量，单位是byte
        try {
            //打开本地文件流
            InputStream inputStream = new FileInputStream(filePath);
            //激活下载操作
            OutputStream os = response.getOutputStream();
            //循环写入输出流
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
            os.close();
            inputStream.close();
        } catch (Exception e) {
            record.setStatus(TransferStatusEnum.ERROR.getCode());
            e.printStackTrace();
        }
        record.setFilePath(filePath);
        record.setStatus(TransferStatusEnum.SUCCESS.getCode());
        record.setMethod(TransferMethodEnum.DOWNLOAD.getCode());
        record.setEndTime(timeFormat.format(new Date()));
        record.setLength(Double.toString(FileUtil.getSizeMB(new File(filePath))) + "MB");

        printLog(record);
    }

    /**
     * 导出到肺癌项目Export文件夹下
     * @param sequences
     * @param request
     * @param session
     */
    @RequestMapping(value = "/export")
    @ResponseBody
    public void dataExport(@RequestParam(value="sequences") int[] sequences
            ,HttpServletRequest request
            ,HttpSession session) {
        FileTransferRecord record = new FileTransferRecord(request);
        Calendar thisYear = Calendar.getInstance();
        Calendar fileYear = Calendar.getInstance();
        User user = (User) session.getAttribute("curUser");
        if (user != null) record.setUserAccount(user.getUserAccount());
        else return;
        File export = new File(SavePath.exportPath);
        if (!export.exists()) export.mkdirs();
        //扫描导出空间过期目录
        for (File file : export.listFiles()) {
            try {
                String fileName = file.getName();
                String[] t = fileName.split("_");
                fileYear.setTime(dateFormat.parse(t[0]));
                int span = Integer.valueOf(thisYear.get(Calendar.DAY_OF_YEAR) - fileYear.get(Calendar.DAY_OF_YEAR));
                if (span > 10) FileUtil.delete(file);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Integer[] is = ArrayUtils.toObject(sequences);
        List<Integer> sequenceIDs = Arrays.asList(is);
        List<Sequence> sequenceList = sequenceService.findBySequenceIDs(sequenceIDs);
        StringBuilder sb = new StringBuilder(SavePath.exportPath);
        sb.append(dateFormat.format(new Date()));
        sb.append("_");
        sb.append(user.getRealName());
        sb.append("\\");
        String exportDir = sb.toString();

        PermissionGroup permission = (PermissionGroup) session.getAttribute("curPermission");
        if (permission != null) {
            for (Sequence sequence : sequenceList) {
                Inspection inspection = sequence.getInspection();
                //权限验证
                int dataMod = permission.getDataMod();
                if (dataMod == 0) {
                    record.setStatus(TransferStatusEnum.ERROR.getCode());
                } else if (dataMod == 1) {
                    if (!user.getUserID().equals(inspection.getUploader()))
                        record.setStatus(TransferStatusEnum.ERROR.getCode());
                } else if (dataMod == 2) {
                    String userHospitalID = user.getHospital().getHospitalID();
                    String dataHospitalID = inspection.getHospital().getHospitalID();
                    if (!userHospitalID.equals(dataHospitalID))
                        record.setStatus(TransferStatusEnum.ERROR.getCode());
                }
            }
        } else return;

        if (record.getStatus() == null) {
            ExportDataHandler.handle(exportDir, sequenceList);
            InfoXml.ergodicHosp(exportDir);
            record.setStatus(TransferStatusEnum.SUCCESS.getCode());
        }
        record.setFilePath(exportDir);
        record.setMethod(TransferMethodEnum.DOWNLOAD.getCode());
        record.setEndTime(timeFormat.format(new Date()));
        double size = FileUtil.getSizeMB(new File(exportDir));
        record.setLength(size + "MB");

        printLog(record);
    }

    /**
     * 打印日志
     * @param record
     */
    public void printLog(FileTransferRecord record) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        File logDir = new File(SavePath.logPath);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            File file = new File(SavePath.logPath + dateFormat.format(new Date()) + ".log");
            if (!file.exists()) file.createNewFile();
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(record.toString());
            bw.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
