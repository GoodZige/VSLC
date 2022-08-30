<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/3/7
  Time: 17:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>权限组管理</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="../themes/default/easyui.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/icon.css" type="text/css"/>
    <script type="text/javascript" src="../js/jquery.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/easyui-lang-ch.js"></script>
</head>
<body>

<c:choose>
    <c:when test='${curPermission.baseMod==3}'>
        <table class="easyui-datagrid" id="permissionGroupTable"></table>
        <div id="permissionGroupDLG" title="操作权限组" class="easyui-dialog" title="My Dialog" style="width:300px;height:400px;"
             data-options="resizable:true,modal:true" closed="true" >
            <form id="permissionGroupff" method="post">
                <input type="hidden" name="permissionGroupID"/>
                <table class="r_edit">
                    <tr>
                        <td class="left">权限组名称: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="permissionGroupName" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">基本模块: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="baseMod" panelHeight="auto" data-options="required:true,valueField:'id',textField:'text',url:'/VSLC/json/perlv.json'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">任务分配模块: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="taskMod" panelHeight="auto" data-options="required:true,valueField:'id',textField:'text',url:'/VSLC/json/perlv.json'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">数据上传模块: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="uploadMod" panelHeight="auto" data-options="required:true,valueField:'id',textField:'text',url:'/VSLC/json/perlv.json'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">数据管理模块: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="dataMod" panelHeight="auto" data-options="required:true,valueField:'id',textField:'text',url:'/VSLC/json/perlv.json'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">勾画模块: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="drawMod" panelHeight="auto" data-options="required:true,valueField:'id',textField:'text',url:'/VSLC/json/perlv.json'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">勾画审核模块: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="drawVerifyMod" panelHeight="auto" data-options="required:true,valueField:'id',textField:'text',url:'/VSLC/json/perlv.json'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">征象分析模块: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="signMod" panelHeight="auto" data-options="required:true,valueField:'id',textField:'text',url:'/VSLC/json/perlv.json'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">征象审核模块: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="signVerifyMod" panelHeight="auto" data-options="required:true,valueField:'id',textField:'text',url:'/VSLC/json/perlv.json'" autocomplete="off"/>
                        </td>
                    </tr>
                </table>
                <a id="btn" onclick="submitPermissionGroupForm()" style="width: 240px;height: 35px;margin: 10px auto;display: block;" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">保存</a>
                <span id="ts"></span>
            </form>
        </div>
    </c:when>
    <c:otherwise>
        对不起，您没有权限
    </c:otherwise>
</c:choose>



<script type="text/javascript">
    var url = "";
    function submitPermissionGroupForm(){
        $('#permissionGroupff').form({
            url:url,
            onSubmit: function(){
                return  $('#permissionGroupff').form("validate");
            },
            success:function(data){
                $("#permissionGroupTable").datagrid("reload");
                $("#permissionGroupDLG").dialog("close");
            }
        });
        $('#permissionGroupff').submit();
    }

    $('#permissionGroupTable').datagrid({
        width:'auto',   //表格宽度
        height:'auto',   //表格高度，可指定高度，可自动
        border:true,  //表格是否显示边框
        url:'/VSLC/pergr/search',   //获取表格数据时请求的地址
        columns:[[
            {field:'permissionGroupID',title:'编号',width:50,align:'center'},
            {field:'permissionGroupName',title:'名称',width:120,align:'center'},
            {field:'baseMod',title:'基本模块',width:100,align:'center',formatter: format},
            {field:'taskMod',title:'任务分配模块',width:100,align:'center',formatter: format},
            {field:'uploadMod',title:'数据上传模块',width:100,align:'center',formatter: format},
            {field:'dataMod',title:'数据管理模块',width:100,align:'center',formatter: format},
            {field:'drawMod',title:'勾画模块',width:100,align:'center',formatter:format},
            {field:'drawVerifyMod',title:'勾画审核模块',width:100,align:'center',formatter: format},
            {field:'signMod',title:'征象分析模块',width:100,align:'center',formatter: format},
            {field:'signVerifyMod',title:'征象审核模块',width:100,align:'center',formatter: format},
        ]],
        singleSelect:true,
        pageSize:10,   //表格中每页显示的行数
        nowrap: false,
        striped: true,  //奇偶行是否使用不同的颜色
        method:'post',   //表格数据获取方式,请求地址是上面定义的url
        idField: 'userId',
        loadMsg:'数据正在努力加载，请稍后...',   //加载数据时显示提示信息
        toolbar: [{
            text: '添加',
            iconCls: 'icon-add',
            handler: function() {
                url = "/VSLC/pergr/add";
                $("#permissionGroupff").form("clear");
                $("#permissionGroupDLG").dialog("open");
                $("#ts").val("");
            }
        }, '-', {
            text: '删除',
            iconCls: 'icon-remove',
            handler: function() {
                var row = $("#permissionGroupTable").datagrid("getSelected");
                if(row){
                    var perID = row.permissionGroupID;
                    $.messager.confirm("操作提示", "确定要删除吗?", function(data){
                        if(data){
                            $.post("/VSLC/pergr/delete",{permissionGroupID:perID}, function (result){
                                if (result.success) {
                                    $("#permissionGroupTable").datagrid("reload");
                                }
                            },"json");
                        }
                    })
                }


            }
        }, '-', {
            text: '修改',
            iconCls: 'icon-edit',
            handler: function() {
                url = "/VSLC/pergr/update";
                var row = $("#permissionGroupTable").datagrid("getSelected");
                if(row){
                    $("#permissionGroupff").form("load", row);
                    $("#permissionGroupDLG").dialog("open");
                    $("#ts").val("");
                }
            }
        }, '-', {
            text: '刷新',
            iconCls: 'icon-reload',
            handler: function() {
                $("#permissionGroupTable").datagrid("reload");
            }
        }]
    });

    function format(value,row,index){
        if(value==0)
            return "没有";
        else if(value==1)
            return "个人";
        else if(value==2)
            return "医院";
        else if(value==3)
            return "所有";
        else
            return "错误";
    }
</script>

</body>
</html>
