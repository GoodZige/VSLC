<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/3/7
  Time: 16:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>登录</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="icon" href="../img/bg/delogo.ico" type="image/x-icon"/>
    <link rel="stylesheet" href="../css/login.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/default/easyui.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/icon.css" type="text/css"/>
    <script type="text/javascript" src="../js/jquery.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/easyui-lang-ch.js"></script>
</head>
<body>

<!-- 导航栏1 -->
<div class="top">
    <div class="nav" style="width: 100%;">
        <div class="login_logo">
            <a href="login" target="_self"><img style="width: 250px;height: 70px;margin-top: 4px" src="../img/bg/logo.png"/></a>
        </div>
    </div>
</div>
<div class="login_box" style="margin: 100px auto;">
    <div style="width:100%;padding: 15px 25px;">
        <h3>登录</h3>
    </div>
    <table class="login_table">
        <tr>
            <td>
                <input id="account" type="url" class="easyui-validatebox login_txt act_txt" data-options="required:true"
                       placeholder="请输入账号" name="userAccount"/>
            </td>
        </tr>
        <tr>
            <td>
                <input id="password" type="password" class="easyui-validatebox login_txt pwd_txt" data-options="required:true"
                       placeholder="请输入密码" name="userPassword" autocomplete="off"/>
            </td>
        </tr>
        <tr>
            <td><a id="btn" onclick="login()" style="width: 210px;height: 40px;" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">登录</a></td>
        </tr>
        <tr>
            <td><span id="ts"></span></td>
        </tr>
    </table>
</div>

<script type="text/javascript">

    $("#password").bind('keypress', function (event) {
        if(event.keyCode == "13") {
            postLogin();
        }
    });
    function login(){
        postLogin();
    }
    function postLogin() {
        var ac = $("#account").val();
        var pd = $("#password").val();
        $.post("/VSLC/user/login",{userAccount:ac,userPassword:pd}, function (result){
            if (result.success) {
                window.location.href="${pageContext.request.contextPath}/page/info";
            }else{
                $("#ts").html("登陆失败");
            }
        },"json");
    }

</script>

</body>
</html>
