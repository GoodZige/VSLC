package com.vslc.controller;

import com.vslc.model.Disease;
import com.vslc.model.Page;
import com.vslc.service.IDiseaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/disease")
public class DiseaseController {

    @Resource
    private IDiseaseService diseaseService;

    @RequestMapping(value="/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam(value="page") Integer page
            ,@RequestParam(value="rows") Integer rows
            ,@RequestParam(value="diseaseName",required=false) String diseaseName) {
        Map<String,Object> reMap = new HashMap<>();
        Map<String,Object> param = new HashMap<>();
        Page pa = new Page(page, rows);
        param.put("pageStart", pa.getFirstPage());
        param.put("pageSize", pa.getRows());
        param.put("diseaseName", diseaseName);
        reMap.put("total", diseaseService.getCount(diseaseName));
        reMap.put("rows", diseaseService.search(param));
        return reMap;
    }

    @RequestMapping(value="/find")
    @ResponseBody
    public List<Disease> find() {
        return diseaseService.find();
    }

    @RequestMapping(value="/add")
    @ResponseBody
    public String add(Disease disease) {
        diseaseService.add(disease);
        return "success";
    }

    @RequestMapping(value="/update")
    @ResponseBody
    public String update(Disease disease) {
        diseaseService.update(disease);
        return "success";
    }

    @RequestMapping(value="/delete")
    @ResponseBody
    public Map<String,Object> delete(String diseaseID) {
        diseaseService.delete(diseaseID);
        HashMap<String,Object> reMap = new HashMap<>();
        reMap.put("success", true);
        return reMap;
    }
}
