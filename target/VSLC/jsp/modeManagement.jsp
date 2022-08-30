<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/3/7
  Time: 17:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>模态管理</title>
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
        <div id="modeDLG" title="操作模态" class="easyui-dialog" style="width:330px;height:200px;"
             data-options="resizable:true,modal:true" closed="true" >
            <form id="modeff" method="post">
                <table class="r_edit" style="margin-top: 20px">
                    <tr>
                        <td class="left">模态编号: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="modeID" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">模态名称: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="modeName" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                </table>
                <a id="btn" onclick="submitModeForm()" style="width: 240px;height: 35px;margin: 10px auto;display: block;" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">保存</a>
            </form>

        </div>
        <div id="modetb" style="padding:5px;height:auto">
            <div style="margin-bottom:5px">
                <a onclick="modeAddDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
                <a onclick="deleteMode()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
                <a onclick="modeUpdateDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
                模态名字: <input type="text" id="search_modeName">
                <a href="#" onclick="setModeQueryPar()" class="easyui-linkbutton" iconCls="icon-search">搜索</a>
            </div>
        </div>

        <table class="easyui-datagrid" id="modeTable"></table>
        <div id="modeAdd"></div>
        <div id="modeUpdate"></div>
    </c:when>
    <c:otherwise>
        对不起，您没有权限
    </c:otherwise>
</c:choose>

<script type="text/javascript">
    var url = "";
    $('#modeTable').datagrid({
        width:'auto',   //表格宽度
        height:'auto',   //表格高度，可指定高度，可自动
        border:true,  //表格是否显示边框
        url:'/VSLC/mode/search',   //获取表格数据时请求的地址
        columns:[[
            {field:'modeID',title:'模态编号'},
            {field:'modeName',title:'模态名字'},
        ]],
        singleSelect:true,
        pagination:true,//如果表格需要支持分页，必须设置该选项为true
        pageSize:10,   //表格中每页显示的行数
        rownumbers:true,   //是否显示行号
        nowrap: false,
        striped: true,  //奇偶行是否使用不同的颜色
        method:'post',   //表格数据获取方式,请求地址是上面定义的url
        idField: 'modeID',
        loadMsg:'数据正在努力加载，请稍后...',   //加载数据时显示提示信息
    });
    $('#modeTable').datagrid({
        toolbar: '#modetb'
    });

    function submitModeForm(){
        $('#modeff').form({
            url:url,
            onSubmit: function(){
                return  $('#modeff').form("validate");
            },
            success:function(data){
                $("#modeTable").datagrid("reload");
                $("#modeDLG").dialog("close");
            }
        });
        $('#modeff').submit();
    }

    function setModeQueryPar(){
        var queryParams = $('#modeTable').datagrid('options').queryParams;
        queryParams.modeName = null;
        var search_modeName = $('#search_modeName').val();

        if(search_modeName!=null && search_modeName!="")
            queryParams.modeName = search_modeName;

        $('#modeTable').datagrid('reload');
    }

    function modeAddDialog(){
        url = "/VSLC/mode/add";
        $("#modeff").form("clear");
        $("#modeDLG").dialog("open");
        $("#ts").val("");
    }

    function modeUpdateDialog(){
        url = "/VSLC/mode/update";
        var row = $("#modeTable").datagrid("getSelected");
        if(row){
            $("#modeff").form("load",row);
            $("#modeDLG").dialog("open");
            $("#ts").val("");
        }
    }

    function deleteMode() {
        var row = $("#modeTable").datagrid("getSelected");
        if(row){
            var modeID = row.modeID;
            $.messager.confirm("操作提示", "确定要删除吗?", function(data){
                if(data){
                    $.post("/VSLC/mode/delete",{modeID:modeID}, function (result){
                        if (result.success) {
                            $("#modeTable").datagrid("reload");
                        }
                    },"json");
                }
            })
        }
    }
</script>

</body>
</html>
