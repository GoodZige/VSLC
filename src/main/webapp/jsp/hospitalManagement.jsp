<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/3/7
  Time: 17:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>医院管理</title>
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
        <div id="hospitalDLG" title="操作医院" class="easyui-dialog" style="width:330px;height:350px;"
             data-options="resizable:true,modal:true" closed="true" >
            <form id="hospitalff" method="post">
                <table class="r_edit">
                    <tr>
                        <td class="left">医院编号: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="hospitalID" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">医院名称: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="hospitalName" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">医院简写: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="hospitalShortName" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">医院地址: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="hospitalAddress" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">医院电话: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="hospitalTel" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">传真号码: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="fax" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">医院邮箱: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="email" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                </table>
                <a id="btn" onclick="submitHospitalForm()" style="width: 240px;height: 35px;margin: 10px auto;display: block;" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">保存</a>
            </form>

        </div>
        <div id="hospitaltb" style="padding:5px;height:auto">
            <div style="margin-bottom:5px">
                <a onclick="hospitalAddDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
                <a onclick="deleteHospital()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
                <a onclick="hospitalUpdateDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
                医院名字: <input type="text" id="search_hospitalName">
                <a href="#" onclick="setHospitalQueryPar()" class="easyui-linkbutton" iconCls="icon-search">搜索</a>
            </div>
        </div>

        <table class="easyui-datagrid" id="hospitalTable"></table>
        <div id="hospitalAdd"></div>
        <div id="hospitalUpdate"></div>
    </c:when>
    <c:otherwise>
        对不起，您没有权限

    </c:otherwise>
</c:choose>

<script type="text/javascript">
    var url = "";

    $('#hospitalTable').datagrid({
        width:'auto',   //表格宽度
        height:'auto',   //表格高度，可指定高度，可自动
        border:true,  //表格是否显示边框
        url:'/VSLC/hospital/search',   //获取表格数据时请求的地址
        columns:[[
            {field:'hospitalID',title:'医院编号'},
            {field:'hospitalName',title:'医院名字'},
            {field:'hospitalShortName',title:'医院简写'},
            {field:'hospitalAddress',title:'医院地址'},
            {field:'hospitalTel',title:'医院电话'},
            {field:'fax',title:'传真'},
            {field:'email',title:'医院邮箱'}
        ]],
        singleSelect:true,
        pagination:true,//如果表格需要支持分页，必须设置该选项为true
        pageSize:10,   //表格中每页显示的行数
        rownumbers:true,   //是否显示行号
        nowrap: false,
        striped: true,  //奇偶行是否使用不同的颜色
        method:'post',   //表格数据获取方式,请求地址是上面定义的url
        idField: 'hospitalID',
        loadMsg:'数据正在努力加载，请稍后...',   //加载数据时显示提示信息
    });
    $('#hospitalTable').datagrid({
        toolbar: '#hospitaltb'
    });

    function submitHospitalForm(){
        $('#hospitalff').form({
            url:url,
            onSubmit: function(){
                return  $('#hospitalff').form("validate");
            },
            success:function(data){
                $("#hospitalTable").datagrid("reload");
                $("#hospitalDLG").dialog("close");
            }
        });
        $('#hospitalff').submit();
    }

    function setHospitalQueryPar(){
        var queryParams = $('#hospitalTable').datagrid('options').queryParams;
        queryParams.hospitalName = null;
        var search_hospitalName = $('#search_hospitalName').val();

        if(search_hospitalName!=null && search_hospitalName!="")
            queryParams.hospitalName = search_hospitalName;

        $('#hospitalTable').datagrid('reload');
    }

    function hospitalAddDialog(){
        url = "/VSLC/hospital/add";
        $("#hospitalff").form("clear");
        $("#hospitalDLG").dialog("open");
        $("#ts").val("");
    }

    function hospitalUpdateDialog(){
        url = "/VSLC/hospital/update";
        var row = $("#hospitalTable").datagrid("getSelected");
        if(row){
            $("#hospitalff").form("load",row);
            $("#hospitalDLG").dialog("open");
            $("#ts").val("");
        }
    }

    function deleteHospital() {
        var row = $("#hospitalTable").datagrid("getSelected");
        if(row){
            var hospitalID = row.hospitalID;
            $.messager.confirm("操作提示", "确定要删除吗?", function(data){
                if(data){
                    $.post("/VSLC/hospital/delete",{hospitalID:hospitalID}, function (result){
                        if (result.success) {
                            $("#hospitalTable").datagrid("reload");
                        }
                    },"json");
                }
            })
        }
    }
</script>

</body>
</html>
