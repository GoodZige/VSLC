package com.vslc.interceptor;

import com.vslc.model.Hospital;
import com.vslc.model.PermissionGroup;
import com.vslc.model.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by chenlele
 * 2018/8/24 15:44
 */
public class InspectionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        String requestURI = request.getRequestURI();
        String[] uris = requestURI.split("/");
        int len = uris.length;
        if (len < 3) {
            return false;
        } else {
            String action = uris[len-1];
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("curUser");
            if (user != null) {
                PermissionGroup permission = (PermissionGroup) session.getAttribute("curPermission");
                if (permission != null) {
                    int baseMode = permission.getBaseMod();
                    int dataMode = permission.getDataMod();
                    if (baseMode < 3) {
                        //管理员可以删除数据、分配任务
                        if (action.equals("delete") || action.equals("taskDistribute")) return false;
                        //工作者可以看到自己的被分配到的任务
                        if (permission.getTaskMod() == 3) {
                            String drawer = request.getParameter("drawer");
                            String drawExaminer = request.getParameter("drawExaminer");
                            String signer = request.getParameter("signer");
                            String signExaminer = request.getParameter("signExaminer");
                            if (drawer.equals("all")) return false;
                            if (drawExaminer.equals("all")) return false;
                            if (signer.equals("all")) return false;
                            if (signExaminer.equals("all")) return false;
                            return true;
                        }
                        //没有权限
                        if(dataMode == 0) {
                            return false;
                            //个人权限
                        } else if (dataMode == 1) {
                            if (action.equals("update")) return false;
                            String uploader = request.getParameter("uploader");
                            if (uploader.equals("all")) return false;
                            if (!uploader.equals(user.getUserID())) return false;
                            //医院权限
                        } else if (dataMode == 2) {
                            Hospital hospital = user.getHospital();
                            if (hospital == null) return false;
                            String hospitalID = request.getParameter("hospitalID");
                            if (hospitalID.equals("all")) return false;
                            if (!hospitalID.equals(hospital.getHospitalID())) return false;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView mv) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception e) throws Exception {

    }
}
