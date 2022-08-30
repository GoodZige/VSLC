package com.vslc.interceptor;

import com.vslc.model.PermissionGroup;
import com.vslc.model.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

/**
 * Created by chenlele
 * 2018/8/24 15:35
 */
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        String requestURI = request.getRequestURI();
        String[] uris = requestURI.split("/");
        int len = uris.length;
        if (len < 3) {
            return false;
        } else {
            String action = uris[len-1];
            if (action.equals("login") || action.equals("logout")) {
                return true;
            } else {
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("curUser");
                if (user != null) {
                    PermissionGroup permission = (PermissionGroup) session.getAttribute("curPermission");
                    if (permission != null) {
                        if (permission.getBaseMod() < 3) {
                            response.setCharacterEncoding("GBK");
                            PrintWriter out = response.getWriter();
                            out.print("<script type='text/javascript'>alert('你没有权限访问');window.location.href='/VSLC/page/info';</script>");
                            return false;
                        }
                    } else {
                        return false;
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
