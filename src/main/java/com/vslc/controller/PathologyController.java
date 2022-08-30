package com.vslc.controller;

import com.vslc.model.Pathology;
import com.vslc.service.IPathologyService;
import com.vslc.tools.report.PathologyReport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlele
 * 2018/5/27 17:12
 */
@Controller
@RequestMapping(value = "/pathology")
public class PathologyController {

    @Resource
    private IPathologyService pathologyService;

    @RequestMapping(value = "/findByPatientID")
    @ResponseBody
    public List<String> findByPatientID(@RequestParam(value = "patientID") String patientID) {
        List<Pathology> pathologyList = pathologyService.findByPatientID(patientID);
        List<String> admissionNums = new ArrayList<>();
        for (Pathology pathology : pathologyList) {
            boolean exist = false;
            for (String admissionNum : admissionNums) {
                if (admissionNum.equals(pathology.getAdmissionNum())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) admissionNums.add(pathology.getAdmissionNum());
        }
        return admissionNums;
    }

    @RequestMapping(value = "/getReport")
    @ResponseBody
    public void getReport(@RequestParam(value = "admissionNum") String admissionNum
            , HttpServletResponse response) throws IOException {
        List<Pathology> pathologyList = pathologyService.findByAdmissionNum(admissionNum);
        String path = PathologyReport.getReport(pathologyList);
        File jpgFile = new File(path);
        DataInputStream inputStream = new DataInputStream(new FileInputStream(jpgFile));
        byte[] data = new byte[(int)jpgFile.length()];
        inputStream.read(data);
        inputStream.close();
        response.setContentType("image/jpeg");
        OutputStream stream = response.getOutputStream();
        stream.write(data);
        stream.flush();
        stream.close();
    }
}
