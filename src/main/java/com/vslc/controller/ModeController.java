package com.vslc.controller;

import com.vslc.model.Mode;
import com.vslc.model.Page;
import com.vslc.service.IModeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/mode")
public class ModeController {

    @Resource
    private IModeService modeService;

    @RequestMapping(value="/search")
    @ResponseBody
    public Map<String, Object> find(@RequestParam(value="page") Integer page
            ,@RequestParam(value="rows") Integer rows
            ,@RequestParam(value="modeName",required=false) String modeName) {
        Map<String, Object> reMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        Page pa = new Page(page, rows);
        param.put("pageStart", pa.getFirstPage());
        param.put("pageSize", pa.getRows());
        param.put("modeName", modeName);
        reMap.put("total", modeService.getCount(modeName));
        reMap.put("rows", modeService.search(param));
        return reMap;
    }

    @RequestMapping(value="/find")
    @ResponseBody
    public List<Mode> find() {
        return modeService.find();
    }

    @RequestMapping(value="/add")
    @ResponseBody
    public String add(Mode mode) {
        modeService.add(mode);
        return "success";
    }

    @RequestMapping(value="/update")
    @ResponseBody
    public String update(Mode mode) {
        modeService.update(mode);
        return "success";
    }

    @RequestMapping(value="/delete")
    @ResponseBody
    public Map<String,Object> delete(String modeID) {
        modeService.delete(modeID);
        HashMap<String,Object> reMap = new HashMap<>();
        reMap.put("success", true);
        return reMap;
    }
}
