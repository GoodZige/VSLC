<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/3/7
  Time: 17:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>权限管理</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="icon" href="../img/bg/delogo.ico" type="image/x-icon"/>
    <link rel="stylesheet" href="../themes/default/easyui.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/icon.css" type="text/css"/>
    <link rel="stylesheet" href="../css/authority.css" type="text/css"/>
    <script type="text/javascript" src="../js/jquery.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/easyui-lang-ch.js"></script>
</head>
<body class="easyui-layout">

<div data-options="border:false,collapsible:false,region:'north',title:'',split:true" style="height:60px;">
    <a href="info" target="_self"><img style="height: 50px" src="../img/bg/logo.png"/></a>
</div>
<div data-options="collapsible:false,region:'west',title:'工作菜单',split:true" style="width:200px;">
    <ul id="tt" class="easyui-tree" data-options="lines:true">
        <li>
            <span>VSLC</span>
            <ul>
                <li>
                    <span>基本管理</span>
                    <ul>
                        <li>
                            <span>用户管理</span>
                        </li>
                        <li>
                            <span>权限组管理</span>
                        </li>
                        <li>
                            <span>病种管理</span>
                        </li>
                        <%--<li>--%>
                            <%--<span>日志管理</span>--%>
                        <%--</li>--%>
                        <li>
                            <span>医院管理</span>
                        </li>
                        <li>
                            <span>模态管理</span>
                        </li>
                    </ul>
                </li>
                <li>
                    <span>注销</span>
                </li>
            </ul>
        </li>
    </ul>
</div>
<div data-options="border:false,collapsible:false,region:'center'">
    <div id="workSpace" class="easyui-tabs" style="width:500px;height:250px;" data-options="fit:true">
    </div>
</div>
<script type="text/javascript">
    $('#tt').tree({
        onClick: function(node){
            if(node.text=="注销"){
                logout();
            }else if(node.text=="用户管理"){
                addtabs("用户管理","userManagement");
            }else if(node.text=="权限组管理"){
                addtabs("权限组管理","permissionGroupManagement");
            }else if(node.text=="医院管理"){
                addtabs("医院管理","hospitalManagement");
            }else if(node.text=="病种管理"){
                addtabs("病种管理","diseaseManagement");
            }else if(node.text=="模态管理"){
                addtabs("模态管理","modeManagement");
            }
        }
    });

    function addtabs(name,href){
        if($("#workSpace").tabs('exists', name)){
            $("#workSpace").tabs('select', name);
        }else{
            $('#workSpace').tabs('add',{
                title: name,
                closable:true,
                href:'/VSLC/page/'+href
            });
        }
    }

    function logout(){
        $.post("/VSLC/user/logout",null, function (result){
            if (result.success) {
                window.location.href="${pageContext.request.contextPath}/page/login";
            }
        },"json");
    }
</script>

</body>
</html>
