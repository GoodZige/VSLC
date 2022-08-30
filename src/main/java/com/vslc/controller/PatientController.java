package com.vslc.controller;

import com.vslc.model.Page;
import com.vslc.model.Patient;
import com.vslc.service.IPatientService;
import com.vslc.tools.IniUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/patient")
public class PatientController {

    @Resource
    private IPatientService patientService;

    @RequestMapping(value="/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam(value="page") Integer page
            , @RequestParam(value="rows") Integer rows
            , @RequestParam(value="fuzzySearch", required=false) String fuzzySearch
            , @RequestParam(value="logicalSearch", required=false) String logicalSearch) {
        Map<String, Object> patientMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("fuzzySearch", fuzzySearch);
        if (logicalSearch != null)
            param.put("logicalSearch",
                    IniUtil.logicalTransfer(logicalSearch));
        List<Patient> patientList = patientService.search(param);
        Page pa = new Page(page, rows);
        int start = pa.getFirstPage();
        int end = start + pa.getRows();
        if (patientList.size() < end) end = patientList.size();
        patientMap.put("total", patientList.size());
        patientMap.put("rows", patientList.subList(start, end));
        return patientMap;
    }
}
