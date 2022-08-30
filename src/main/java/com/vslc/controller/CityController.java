package com.vslc.controller;

import com.vslc.model.City;
import com.vslc.service.ICityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/city")
public class CityController {

    @Resource
    private ICityService cityService;

    @RequestMapping(value="/findByProvinceID")
    @ResponseBody
    public List<City> findByProvinceID(@RequestParam(value = "provinceID") String provinceID) {
        return cityService.findByProvinceID(provinceID);
    }
}
