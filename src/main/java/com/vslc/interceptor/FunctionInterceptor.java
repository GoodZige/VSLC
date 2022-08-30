package com.vslc.interceptor;

import com.vslc.dao.ISequenceDao;
import com.vslc.model.*;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by chenlele
 * 2018/8/24 16:46
 */
public class FunctionInterceptor implements HandlerInterceptor {

    @Resource
    private ISequenceDao sequenceDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        String requestURI = request.getRequestURI();
        String[] uris = requestURI.split("/");
        int len = uris.length;
        if (len < 3) {
            return false;
        } else {
            String action = uris[len - 1];
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("curUser");
            if (user != null) {
                PermissionGroup permission = (PermissionGroup) session.getAttribute("curPermission");
                if (permission != null) {
                    int baseMode = permission.getBaseMod();
                    int dataMode = permission.getDataMod();
                    //没有基本权限
                    if (baseMode == 0) return false;
                    if (action.equals("displayDcm")) {
                        int sequenceID = Integer.valueOf(request.getParameter("sequenceID"));
                        Sequence sequence = sequenceDao.findBySequenceID(sequenceID);
                        Inspection inspection = sequence.getInspection();
                        //没有数据权限
                        if (dataMode == 0) {
                            return false;
                        } else if (dataMode == 1) {
                            if (!user.getUserID().equals(inspection.getUploader())) return false;
                        } else if (dataMode == 2) {
                            String userHospitalID = user.getHospital().getHospitalID();
                            String dataHospitalID = inspection.getHospital().getHospitalID();
                            if (!userHospitalID.equals(dataHospitalID)) return false;
                        }
                    }
                }
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
