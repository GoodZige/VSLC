package com.vslc.controller;

import com.vslc.model.PermissionGroup;
import com.vslc.service.IPermissionGroupService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/pergr")
public class PermissionGroupController {

    @Resource
    private IPermissionGroupService permissionGroupService;

    @RequestMapping(value="/search")
    @ResponseBody
    public Map<String,Object> find() {
        Map<String,Object> reMap = new HashMap<>();
        reMap.put("total", permissionGroupService.getCount());
        reMap.put("rows", permissionGroupService.find());
        return reMap;
    }

    @RequestMapping(value="/find")
    @ResponseBody
    public List<PermissionGroup> getPermissionComboItem() {
        return permissionGroupService.find();
    }


    @RequestMapping(value="/add")
    @ResponseBody
    public String add(PermissionGroup permissionGroup) {
        permissionGroupService.add(permissionGroup);
        return "success";
    }

    @RequestMapping(value="/update")
    @ResponseBody
    public String update(PermissionGroup permissionGroup) {
        permissionGroupService.update(permissionGroup);
        return "success";
    }

    @RequestMapping(value="/delete")
    public @ResponseBody Map<String,Object> delete(@RequestParam(value="permissionGroupID")int permissionGroupID) {
        permissionGroupService.delete(permissionGroupID);
        HashMap<String,Object> reMap = new HashMap<>();
        reMap.put("success",true);
        return reMap;
    }
}
