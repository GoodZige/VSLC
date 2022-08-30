package com.vslc.controller;

import com.vslc.model.Inspection;
import com.vslc.model.Page;
import com.vslc.model.User;
import com.vslc.service.IInspectionService;
import com.vslc.service.IUserService;
import com.vslc.tools.IniUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inspection")
public class InspectionController {

    @Resource
    private IInspectionService inspectionService;

    @Resource
    private IUserService userService;

    @RequestMapping(value="/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam(value="page") Integer page
            ,@RequestParam(value="rows") Integer rows
            ,@RequestParam(value="fuzzySearch", required=false) String fuzzySearch
            ,@RequestParam(value="logicalSearch", required=false) String logicalSearch
            ,@RequestParam(value="timeSearch") String timeSearch
            ,@RequestParam(value="hospitalID") String hospitalID
            ,@RequestParam(value="uploader") String uploader
            ,@RequestParam(value="drawer") String drawer
            ,@RequestParam(value="drawExaminer") String drawExaminer
            ,@RequestParam(value="signer") String signer
            ,@RequestParam(value="signExaminer") String signExaminer) {
        Map<String, Object> integrationMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        if (logicalSearch != null) {
            param.put("logicalSearch", IniUtil.logicalTransfer(logicalSearch));
        } else {
            param.put("fuzzySearch", fuzzySearch);
        }
        param.put("timeSearch", timeSearch);
        param.put("hospitalID", hospitalID);
        param.put("uploader", uploader);
        param.put("drawer", drawer);
        param.put("drawExaminer", drawExaminer);
        param.put("signer", signer);
        param.put("signExaminer", signExaminer);
        List<Inspection> inspectionList = inspectionService.search(param);
        Page pa = new Page(page, rows);
        int start = pa.getFirstPage();
        int end = start + pa.getRows();
        if (inspectionList.size() < end) end = inspectionList.size();
        integrationMap.put("total", inspectionList.size());
        integrationMap.put("rows", inspectionList.subList(start, end));
        return integrationMap;
    }

    /**
     * 任务分配（管理员）
     * @param distributeInfo
     */
    @RequestMapping(value="/taskDistribute")
    @ResponseBody
    public void taskDistribute(@RequestBody Map<String,Object> distributeInfo) {
        Integer userID = (Integer) distributeInfo.get("userSelected");
        List<String> checks = (List<String>) distributeInfo.get("inspectionChecks");
        if (checks.size() > 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("checks", checks);
            User user = userService.findByUserID(userID);
            //自动判断任务
            int permission = user.getPermissionGroup().getPermissionGroupID();
            if (permission == 3) param.put("drawer", userID);
            else if (permission == 4) param.put("drawExaminer", userID);
            else if (permission == 5) param.put("signer", userID);
            else if (permission == 6) param.put("signExaminer", userID);
            inspectionService.updateWorker(param);
        }
    }

    /**
     * 完成任务
     * @param distributeInfo
     */
    @RequestMapping(value="/taskFinish")
    @ResponseBody
    public void taskFinish(@RequestBody Map<String,Object> distributeInfo) {
        Integer permission = (Integer) distributeInfo.get("permission");
        List<String> checks = (List<String>) distributeInfo.get("inspectionChecks");
        if (checks.size() > 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("checks", checks);
            if (permission == 3) param.put("drawer", null);
            else if (permission == 4) param.put("drawExaminer", null);
            else if (permission == 5) param.put("signer", null);
            else if (permission == 6) param.put("signExaminer", null);
            inspectionService.updateWorker(param);
        }
    }

    @RequestMapping(value="/findByPatientID")
    @ResponseBody
    public Map<String, Object> findByPatientID(@RequestParam(value="patientID") String patientID) {
        Map<String, Object> integrationMap = new HashMap<>();
        List<Inspection> inspectionList = inspectionService.findByPatientID(patientID);
        integrationMap.put("total", inspectionList.size());
        integrationMap.put("rows", inspectionList);
        return integrationMap;
    }

    @RequestMapping(value="/getInspectionInfo")
    @ResponseBody
    public Map<String,Object> getInspectionInfo(@RequestParam(value="inspectionID") String inspectionID){
        Inspection inspection = inspectionService.findByInspectionID(inspectionID);
        HashMap<String,Object> prMap = new HashMap<>();
        prMap.put("inspectionTime", inspection.getInspectTime());
        prMap.put("processID", inspection.getProcessID());
        prMap.put("patientName",inspection.getPatient().getEnglishName());
        return prMap;
    }

    @RequestMapping(value="/update")
    @ResponseBody
    public void update(@RequestParam(value="inspectionID") String inspectionID
            ,@RequestParam(value="processID", required=false) Integer processID
            ,@RequestParam(value="imageMethod", required=false) Integer imageMethod
            ,@RequestParam(value="diseaseName", required=false) String diseaseName
            ,@RequestParam(value="isAbdomen", required=false) Integer isAbdomen
            ,@RequestParam(value="isAbdomenCE", required=false) Integer isAbdomenCE
            ,@RequestParam(value="PNSize", required=false) Float PNSize
            ,@RequestParam(value="PNNum", required=false) Integer PNNum
            ,@RequestParam(value="PNSign", required=false) Integer PNSign
            ,@RequestParam(value="thickness", required=false) Float thickness
            ,@RequestParam(value="editor", required=false) int editor
            ,@RequestParam(value="remark", required=false) String remark) {
        Map<String, Object> param = new HashMap<>();
        param.put("editor", editor);
        param.put("inspectionID", inspectionID);
        param.put("processID", processID);
        param.put("imageMethod", imageMethod);
        param.put("thickness", thickness);
        param.put("isAbdomen", isAbdomen);
        param.put("isAbdomenCE", isAbdomenCE);
        param.put("PNSize", PNSize);
        param.put("PNNum", PNNum);
        param.put("PNSign", PNSign);
        param.put("remark", remark);
        if (diseaseName != null) param.put("diseaseName", diseaseName);
        else if (diseaseName.equals("")) param.put("diseaseName", null);
        inspectionService.update(param);
    }

    @RequestMapping(value="/delete")
    @ResponseBody
    public void delete(@RequestParam(value="checks") List<String> checks) {
        inspectionService.delete(checks);
    }
}
