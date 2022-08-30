package com.vslc.controller;

import com.vslc.model.*;
import com.vslc.service.IInspectionService;
import com.vslc.service.ISequenceService;
import com.vslc.tools.*;
import com.vslc.tools.dicom.ImgMatrixHandler;
import com.vslc.tools.SketchResult;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping(value = "/sequence")
public class SequenceController {

    @Resource
    private IInspectionService inspectionService;

    @Resource
    private ISequenceService sequenceService;

    /**
     * 获取某个检查的所有序列
     * @param inspectionID 检查ID
     * @param winLung 根据需求判断是否修改序列名
     * @return
     */
    @RequestMapping(value="/getSequenceList")
    @ResponseBody
    public List<Sequence> findSequences(@RequestParam(value="inspectionID") String inspectionID
            ,@RequestParam(value="winLung", required = false) boolean winLung) {
        List<Sequence> sequenceList = new ArrayList<>();
        Inspection inspection = inspectionService.findByInspectionID(inspectionID);
        String inspePath = SavePath.rootPath + inspection.getSavePath();
        File inspeDir = new File(inspePath);
        if (inspeDir.exists()) {
            sequenceList = sequenceService.findByInspectionID(inspectionID);
            for (Sequence sequence : sequenceList) {
                StringBuilder sb = new StringBuilder(SavePath.rootPath);
                sb.append(sequence.getDcmPath());
                File file = new File(sb.toString());
                DcmInfo dcmInfo = new DcmInfo(file, false);
                if (winLung) {
                    int winWidth = dcmInfo.getWindowWidth();
                    int winCenter = dcmInfo.getWindowCenter();
                    if (winWidth >= 1200 && winWidth <= 2000) {
                        if (winCenter >= -800 && winCenter <= -400) {
                            sequence.setSequenceName("肺窗");
                        }
                    }
                }
                sequence.setWidth(dcmInfo.getColumns());
                sequence.setHeight(dcmInfo.getRows());
            }
        }
        return sequenceList;
    }

    /**
     * dicom文件的文件名大多是1开始且连续
     * 但是可能存在断序的文件名，因此先排列成数组传到前端
     * @param sequenceID
     * @return
     */
    @RequestMapping(value="/getImageList")
    @ResponseBody
    public List<Integer> getImageList(@RequestParam(value="sequenceID") int sequenceID) {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        List<Integer> indexList = new ArrayList<>();
        File dcmDir = new File(PathUtil.getDcmFile(sequence, SavePath.rootPath, null));
        for (File dcm : dcmDir.listFiles()) {
            String fileName = dcm.getName();
            indexList.add(Integer.valueOf(fileName.substring(0, fileName.indexOf("."))));
        }
        indexList.sort(Integer::compareTo);
        return indexList;
    }

    //不考虑三维重建则没用
    @RequestMapping(value="/matrixExists")
    @ResponseBody
    public boolean matrixExists(@RequestParam(value="sequenceID") int sequenceID) {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        String matrixPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, "matrix.bin");
        File matrixFile = new File(matrixPath);
        if (!matrixFile.exists()) return false;
        File dcmFile = new File(SavePath.rootPath + sequence.getDcmPath());
        DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
        int fileNum = sequence.getFileNum();
        int cols = dcmInfo.getColumns();
        int rows = dcmInfo.getRows();
        long actualSize = matrixFile.length();
        long normalSize = cols*rows*fileNum*2;
        //文件存在并且实际大小>=理想大小
        return actualSize >= normalSize;
    }

    //不考虑三维重建则没用
    @RequestMapping(value="/matrixProcess")
    @ResponseBody
    public void matrixProcess(@RequestParam(value="sequenceID") int sequenceID) {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        String matrixPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, "matrix.bin");
        File matrixFile = new File(matrixPath);
        File dcmFile = new File(SavePath.rootPath + sequence.getDcmPath());
        DcmInfo dcmInfo = new DcmInfo(dcmFile, false);
        int fileNum = sequence.getFileNum();
        int cols = dcmInfo.getColumns();
        int rows = dcmInfo.getRows();
        long actualSize = matrixFile.length();
        long enableSize = cols*rows*fileNum*2; //理想执行大小
        while (actualSize < enableSize) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            actualSize = matrixFile.length(); //文件实际大小
        }
    }

    /**
     * 审核页面请求图像 返回base64编码
     * @param sequenceID
     * @param type 0横断位 1冠状位 2矢状位
     * @param matrixIndex 三维矩阵下标（无则取1）
     * @param dcmIndex dicom下标（无则默认取首个dicom文件）
     * @return cornerstone解析
     */
    @RequestMapping(value="/getMatrix")
    @ResponseBody
    public Map<String, Object> getMatrix(@RequestParam(value="sequenceID") int sequenceID
            ,@RequestParam(value="type") int type
            ,@RequestParam(value="matrixIndex") int matrixIndex
            ,@RequestParam(value="dcmIndex", required = false) Integer dcmIndex) {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        return ImgMatrixHandler.getMatrix(sequence, type, matrixIndex, dcmIndex);
    }

    /**
     * 获取标注结果列表
     * key：fileTree（easyui tree）
     * key：numberList（结节号坐标）
     * @param sequenceID
     * @return
     */
    @RequestMapping(value="/getSketchList")
    @ResponseBody
    public Map<String, Object> getReviewResultList(@RequestParam(value="sequenceID") Integer sequenceID) {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        String maskPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, null);
        if (SketchResult.isSketch(sequence))
            return SketchResult.getSketchList(maskPath);
        else
            return new HashMap<>();
    }

    /**
     * 获取所有标注结果坐标
     * @param sequenceID
     * @param sketchFile
     * @return
     */
    @RequestMapping(value="/getSketchPosition")
    @ResponseBody
    public List<Map<String,Object>> getSketchPositionList(@RequestParam(value="sequenceID") Integer sequenceID
            ,@RequestParam(value="sketchFile") String sketchFile) {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        String maskPath = PathUtil.getMaskFile(sequence, SavePath.rootPath, sketchFile);
        return SketchResult.getPositionList(maskPath);
    }

    /**
     * 保存标注结果
     * @param positionInfo json
     */
    @RequestMapping(value="/saveSketch")
    @ResponseBody
    public void saveMask(@RequestBody Map<String,Object> positionInfo) {
        JSONArray array = JSONArray.fromObject(positionInfo.get("allPositionsInfo"));
        Integer sequenceID = (Integer) positionInfo.get("sequenceID");
        String sketchFile = (String) positionInfo.get("sketchFile");
        Byte sketchNum = Byte.valueOf((String) positionInfo.get("sketchNum"));
        List<Integer> edits = (List<Integer>) positionInfo.get("edits");
        System.out.println("test");
        System.out.println(edits);
        Iterator<Object> it = array.iterator();
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        SketchResult.saveSketch(sequence, it, sketchFile, sketchNum);

        if (SketchResult.isSketch(sequence)) {
            sequence.setIsSketch(1);
            sequenceService.updateIsSketch(sequence);
            Inspection inspection = sequence.getInspection();
            inspection.setProcessID(2);
            inspectionService.updateProcessID(inspection);
        }
    }

    /**
     * 删除标注结果
     * @param sequenceID
     * @param sketchFile mask文件
     * @param sketchNum 结节号（无则删除整个mask）
     */
    @RequestMapping(value="/deleteSketch")
    @ResponseBody
    public void deleteSketch(@RequestParam(value="sequenceID") Integer sequenceID
            ,@RequestParam(value="sketchFile") String sketchFile
            ,@RequestParam(value="sketchNum", required = false) Byte sketchNum) {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        SketchResult.deleteSketch(sequence, sketchFile, sketchNum);
        if (!SketchResult.isSketch(sequence)) {
            sequence.setIsSketch(0);
            sequenceService.updateIsSketch(sequence);
        }
    }

    /**
     * 二维结节信息
     * @param positionInfo
     * @return
     */
    @RequestMapping(value="/D2NoduleInfo")
    @ResponseBody
    public Map<String, Object> D2NoduleInfo(@RequestBody Map<String,Object> positionInfo) {
        JSONArray list = JSONArray.fromObject(positionInfo.get("positionList"));
        Integer sequenceID = (Integer) positionInfo.get("sequenceID");
        Integer zIndex = (Integer) positionInfo.get("zIndex");
        Iterator<Object> it = list.iterator();
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        return SketchResult.D2NoduleInfo(sequence, it, zIndex);
    }

    /**
     * 三维结节信息
     * @param sequenceID
     * @param sketchNum 结节号
     * @return
     */
    @RequestMapping(value="/D3NoduleInfo")
    @ResponseBody
    public Map<String, Object> D3NoduleInfo(@RequestParam(value="sequenceID") Integer sequenceID
            ,@RequestParam(value="sketchNum") byte sketchNum) {
        Sequence sequence = sequenceService.findBySequenceID(sequenceID);
        return SketchResult.D3NoduleInfo(sequence, sketchNum);
    }
}
