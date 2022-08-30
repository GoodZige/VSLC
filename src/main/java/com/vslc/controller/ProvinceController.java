package com.vslc.controller;

import com.vslc.model.Province;
import com.vslc.service.IProvinceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/province")
public class ProvinceController {

    @Resource
    private IProvinceService provinceService;

    @RequestMapping(value="/find")
    @ResponseBody
    public List<Province> find() {
        return provinceService.find();
    }
}
