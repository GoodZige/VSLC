package com.vslc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by chenlele
 * 2018/8/15 10:51
 */
@Controller
@RequestMapping(value = "/page")
public class PageController {

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/info")
    public String info() {
        return "info";
    }

    @RequestMapping(value = "/review")
    public String review() {
        return "review";
    }

    @RequestMapping(value = "/authority")
    public String authority() {
        return "authority";
    }

    @RequestMapping(value = "/userManagement")
    public String userManagement() {
        return "userManagement";
    }

    @RequestMapping(value = "/permissionGroupManagement")
    public String permissionGroupManagement() {
        return "permissionGroupManagement";
    }

    @RequestMapping(value = "/hospitalManagement")
    public String hospitalManagement() {
        return "hospitalManagement";
    }

    @RequestMapping(value = "/diseaseManagement")
    public String diseaseManagement() {
        return "diseaseManagement";
    }

    @RequestMapping(value = "/modeManagement")
    public String modeManagement() {
        return "modeManagement";
    }
}
