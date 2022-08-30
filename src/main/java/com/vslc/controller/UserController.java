package com.vslc.controller;

import com.vslc.model.*;
import com.vslc.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    private IUserService userService;

    @RequestMapping(value="/logout")
    @ResponseBody
    public Map<String,Object> logout(HttpSession session) {
        Map<String,Object> reMap = new HashMap<>();
        reMap.put("success",true);
        session.removeAttribute("curUser");
        session.removeAttribute("curPermission");
        return reMap;
    }

    @RequestMapping(value="/login")
    @ResponseBody
    public Map<String,Object> login(HttpSession session
            ,@RequestParam(value="userAccount") String userAccount
            ,@RequestParam(value="userPassword") String userPassword) {
        Map<String,Object> reMap = new HashMap<>();
        Map<String,Object> param = new HashMap<>();
        param.put("userAccount", userAccount);
        param.put("userPassword", userPassword);
        User user = userService.login(param);
        if(user != null) {
            reMap.put("success", true);
            session.setAttribute("curUser", user);
            session.setAttribute("curPermission", user.getPermissionGroup());
            user.setUserPassword(null);
            user.setPermissionGroup(null);
        } else {
            reMap.put("fail", true);
        }
        return reMap;
    }

    @RequestMapping(value="/search")
    @ResponseBody
    public Map<String,Object> search(@RequestParam(value="page") Integer page
            ,@RequestParam(value="rows") Integer rows
            ,@RequestParam(value="account",required=false) String account
            ,@RequestParam(value="realName",required=false) String realName) {
        HashMap<String,Object> reMap = new HashMap<>();
        Map<String,Object> param = new HashMap<>();
        Page pa = new Page(page, rows);
        param.put("pageStart", pa.getFirstPage());
        param.put("pageSize", pa.getRows());
        param.put("account", account);
        param.put("realName", realName);
        reMap.put("total", userService.getCount(param));
        reMap.put("rows", userService.search(param));
        return reMap;
    }

    @RequestMapping(value="/find")
    @ResponseBody
    public List<User> find(@RequestParam(value="permissionGroupID", required=false) String permissionGroupID
            ,@RequestParam(value="fuzzySearch", required=false) String fuzzySearch) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("permissionGroupID", permissionGroupID);
        param.put("fuzzySearch", fuzzySearch);
        return userService.find(param);
    }

    @RequestMapping(value="/add")
    @ResponseBody
    public String add(User user
            ,@RequestParam(value="birday",required=false) String birday
            ,@RequestParam(value="permissionGroupID",required=false) Integer permissionGroupID
            ,@RequestParam(value="hospitalID",required=false) String hospitalID
            ,@RequestParam(value="provinceID",required=false) String provinceID
            ,@RequestParam(value="cityID",required=false) String cityID) {
        PermissionGroup p = new PermissionGroup();
        p.setPermissionGroupID(permissionGroupID);

        Hospital h = new Hospital();
        if (hospitalID.equals("")) h.setHospitalID(null);
        else h.setHospitalID(hospitalID);

        Province pr = new Province();
        if (provinceID.equals("")) pr.setProvinceID(null);
        else pr.setProvinceID(provinceID);

        City c = new City();
        if (cityID.equals("")) c.setCityID(null);
        else c.setCityID(cityID);

        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        try {
            if (birday.equals("")) user.setBirthday(null);
            else user.setBirthday(sdf.parse(birday));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(h.getHospitalID());
        user.setPermissionGroup(p);
        user.setHospital(h);
        user.setProvince(pr);
        user.setCity(c);
        userService.add(user);
        return "success";
    }

    @RequestMapping(value="/update")
    @ResponseBody
    public String update(User user
            ,@RequestParam(value="birday",required=false) String birday
            ,@RequestParam(value="permissionGroupID",required=false) Integer permissionGroupID
            ,@RequestParam(value="hospitalID",required=false) String hospitalID
            ,@RequestParam(value="provinceID",required=false) String provinceID
            ,@RequestParam(value="cityID",required=false) String cityID) {
        PermissionGroup p = new PermissionGroup();
        p.setPermissionGroupID(permissionGroupID);

        Hospital h = new Hospital();
        if (hospitalID.equals("")) h.setHospitalID(null);
        else h.setHospitalID(hospitalID);

        Province pr = new Province();
        if (provinceID.equals("")) pr.setProvinceID(null);
        else pr.setProvinceID(provinceID);

        City c = new City();
        if (cityID.equals("")) c.setCityID(null);
        else c.setCityID(cityID);

        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        try {
            if (birday.equals("")) user.setBirthday(null);
            else user.setBirthday(sdf.parse(birday));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        user.setPermissionGroup(p);
        user.setHospital(h);
        user.setProvince(pr);
        user.setCity(c);
        userService.update(user);
        return "success";
    }

    @RequestMapping(value="/delete")
    @ResponseBody
    public Map<String,Object> delete(int userID) {
        userService.delete(userID);
        HashMap<String,Object> reMap = new HashMap<>();
        reMap.put("success",true);
        return reMap;
    }
}
