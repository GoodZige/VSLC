package com.vslc.controller;

import com.vslc.model.Examination;
import com.vslc.service.IExaminationService;
import com.vslc.tools.report.ExaminationReport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/examination")
public class ExaminationController {

    @Resource
    private IExaminationService examinationService;

    @RequestMapping(value = "/findByPatientID")
    @ResponseBody
    public List<Examination> findByPatientID(@RequestParam(value = "patientID") String patientID) {
        return examinationService.findByPatientID(patientID);
    }

    @RequestMapping(value = "/getReport")
    @ResponseBody
    public void getReport(@RequestParam(value = "examinationID") Integer examinationID
            , @RequestParam(value = "format") Integer format
            , HttpServletResponse response) throws IOException {
        Map<String, Object> param = new HashMap<>();
        Examination examination = examinationService.findByExaminationID(examinationID);
        param.put("patient", examination.getPatient());
        param.put("examination", examination);
        param.put("format", format);
        String path = ExaminationReport.getReport(param);

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
