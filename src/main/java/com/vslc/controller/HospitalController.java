package com.vslc.controller;

import com.vslc.model.Hospital;
import com.vslc.model.Page;
import com.vslc.service.IHospitalService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/hospital")
public class HospitalController {

    @Resource
    private IHospitalService hospitalService;

    @RequestMapping(value="/search")
    @ResponseBody
    public Map<String,Object> search(@RequestParam(value="hospitalName",required=false) String hospitalName
            ,@RequestParam(value="page") Integer page
            ,@RequestParam(value="rows") Integer rows) {
        Map<String,Object> reMap = new HashMap<>();
        Map<String,Object> param = new HashMap<>();
        Page pa = new Page(page, rows);
        param.put("pageStart", pa.getFirstPage());
        param.put("pageSize", pa.getRows());
        param.put("hospitalName", hospitalName);

        reMap.put("total", hospitalService.getCount(hospitalName));
        reMap.put("rows", hospitalService.search(param));
        return reMap;
    }

    @RequestMapping(value="/find")
    @ResponseBody
    public List<Hospital> find() {
        return hospitalService.find();
    }

    @RequestMapping(value="/add")
    @ResponseBody
    public void add(Hospital hospital) {
        hospitalService.add(hospital);
    }

    @RequestMapping(value="/update")
    @ResponseBody
    public void update(Hospital hospital) {
        hospitalService.update(hospital);
    }

    @RequestMapping(value="/delete")
    @ResponseBody
    public void delete(String hospitalID) {
        hospitalService.delete(hospitalID);
    }
}
