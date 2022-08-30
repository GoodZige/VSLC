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
    <title>登陆</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link href="css/style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="themes/default/easyui.css" type="text/css"/>
    <link rel="stylesheet" href="themes/icon.css" type="text/css"/>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui-lang-zh_CN.js"></script>
</head>
<body>

<!-- 导航栏1 -->
<div class="top">
    <div class="nav" style="width: 100%;">
        <div class="login_logo">
            <a href="login.jsp" target="_self"><img style="width: 250px;height: 70px;margin-top: 4px" src="../img/icon/logo.png"/></a>
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
                       placeholder="请输入账号" name="userAccount" />
            </td>
        </tr>
        <tr>
            <td>
                <input id="password" type="password" class="easyui-validatebox login_txt pwd_txt" data-options="required:true"
                       placeholder="请输入账号" name="userPassword"/>
            </td>
        </tr>
        <tr>
            <td><a id="btn" onclick="login()" style="width: 210px;height: 40px;" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">登陆</a></td>
        </tr>
        <tr>
            <td><span id="ts"></span></td>
        </tr>
    </table>
</div>
<!--
<div id="dd" class="easyui-dialog" title="登陆" style="width:400px;height:200px;"
    data-options="resizable:true,modal:true,closable:false">
    <center>
    </br></br>
    账号:<input id="account" class="easyui-validatebox" data-options="required:true" name="userAccount" />
    </br></br>
    密码:<input id="password" type="password" class="easyui-validatebox" data-options="required:true" name="userPassword"/>
    </br></br>
    <a id="btn" onclick="login()" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">登陆</a>
    </br>
    <span id="ts"></span>
    </center>
</div>
-->
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
        $.post("/VSLC/user/login.action",{userAccount:ac,userPassword:pd}, function (result){
            if (result.success) {
                window.location.href="${pageContext.request.contextPath}/jsp/info.jsp";
            }else{
                $("#ts").html("登陆失败");
            }
        },"json");
    }

</script>

</body>
</html>
