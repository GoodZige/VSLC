<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/3/7
  Time: 17:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>用户管理</title>
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
        <div id="userDLG" title="操作用户" class="easyui-dialog" style="width:350px;height:450px;"
             data-options="resizable:true,modal:true" closed="true">
            <form id="userff" method="post">
                <input type="hidden" name="userID"/>
                <table class="r_edit">
                    <tr>
                        <td class="left">用户账号: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="userAccount" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">用户密码: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="userPassword" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">所属权限组: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="permissionGroupID" panelHeight="auto" data-options="required:true,valueField:'permissionGroupID',textField:'permissionGroupName',url:'/VSLC/pergr/find'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">真实姓名: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="realName" data-options="required:true" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">性别: </td>
                        <td style="font-size: 14px">
                            男<input type="radio" name="sex" value="1"/>
                            女<input type="radio" name="sex" value="0"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">出生日期: </td>
                        <td>
                            <input class="easyui-datebox r_txt01" type="text" name="birday" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">身份证号码: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="cardID" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">所在单位: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="hospitalID" panelHeight="auto" data-options="required:false,valueField:'hospitalID',textField:'hospitalName',url:'/VSLC/hospital/find'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">省份: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="provinceID" data-options="required:false,valueField:'provinceID',textField:'provinceName',url:'/VSLC/province/find',onSelect:loadCity" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">城市: </td>
                        <td>
                            <input class="easyui-combobox r_txt01" name="cityID" id="city_input" data-options="required:false,valueField:'cityID',textField:'cityName'" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">地址: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="address" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">邮箱编码: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="postCode" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">职务: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="title" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">电话: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="tel" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">传真: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="fax" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">邮箱: </td>
                        <td>
                            <input class="easyui-validatebox r_txt01" type="text" name="email" data-options="required:false" autocomplete="off"/>
                        </td>
                    </tr>
                </table>
                <a id="btn" onclick="submitUserForm()" style="width: 240px;height: 35px;margin: 10px auto;display: block;" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">保存</a>
            </form>
        </div>
        <div id="tb" style="padding:5px;height:auto">
            <div style="margin-bottom:5px">
                <a onclick="userAddDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
                <a onclick="deleteUser()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
                <a onclick="userUpdateDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
                账户: <input type="text" id="search_account">
                真实姓名: <input type="text" id="search_realName">
                <a href="#" onclick="setUserQueryPar()" class="easyui-linkbutton" iconCls="icon-search">搜索</a>
            </div>
        </div>

        <table class="easyui-datagrid" id="userTable"></table>
        <div id="userAdd"></div>
        <div id="userUpdate"></div>
    </c:when>
    <c:otherwise>
        对不起，您没有权限
    </c:otherwise>
</c:choose>


<script type="text/javascript">
    var url = "";

    function loadCity(rec){
        var url = '/VSLC/city/findByProvinceID?provinceID='+rec.provinceID;
        $('#city_input').combobox('reload', url);
    }

    $('#userTable').datagrid({
        width:'auto',   //表格宽度
        height:'auto',   //表格高度，可指定高度，可自动
        border:true,  //表格是否显示边框
        url:'/VSLC/user/search',   //获取表格数据时请求的地址
        columns:[[
            {field:'userID',title:'用户编号',align:'center'},
            {field:'userAccount',title:'用户账号',align:'center'},
            {field:'userPassword',title:'用户密码',align:'center'},
            {field:'permissionGroup',title:'所属权限组',align:'center',
                formatter: function(value,row,index){
                    return value.permissionGroupName;
                }
            },
            {field:'realName',title:'真实姓名',align:'center'},
            {field:'sex',title:'性别',align:'center',
                formatter: function(value,row,index){
                    if(value==1){
                        return '男';
                    }else{
                        return '女';
                    }
                }
            },
            {field:'birthday',title:'出生日期',align:'center',
                formatter: function(value,row,index) {
                    if (value != null) {
                        return transferDate(value, 0);
                    } else {
                        return "";
                    }
                }},
            {field:'cardID',title:'身份证号码',align:'center'},
            {field:'hospital',title:'所在单位',align:'center',
                formatter: function(value,row,index){
                    if(value!=null)
                        return value.hospitalName;
                }
            },
            {field:'province',title:'省份',align:'center',
                formatter: function(value,row,index){
                    if(value!=null)
                        return value.provinceName;
                }
            },
            {field:'city',title:'城市',align:'center',
                formatter: function(value,row,index){
                    if(value!=null)
                        return value.cityName;
                }
            },
            {field:'address',title:'地址',align:'center'},
            {field:'postCode',title:'邮箱编码',align:'center'},
            {field:'title',title:'职务',align:'center'},
            {field:'tel',title:'电话',align:'center'},
            {field:'fax',title:'传真',align:'center'},
            {field:'email',title:'邮箱',align:'center'},
            {field:'regDate',title:'注册日期',align:'center',
                formatter: function(value,row,index) {
                    return transferDate(value, 0);
                }}
        ]],
        singleSelect:true,
        pagination:true,//如果表格需要支持分页，必须设置该选项为true
        pageSize:10,   //表格中每页显示的行数
        nowrap: false,
        striped: true,  //奇偶行是否使用不同的颜色
        method:'post',   //表格数据获取方式,请求地址是上面定义的url
        idField: 'userId',
        loadMsg:'数据正在努力加载，请稍后...',   //加载数据时显示提示信息
    });
    $('#userTable').datagrid({
        toolbar: '#tb'
    });

    function submitUserForm(){
        $('#userff').form({
            url:url,
            onSubmit: function(){
                return  $('#userff').form("validate");
            },
            success:function(data){
                $("#userTable").datagrid("reload");
                $("#userDLG").dialog("close");
            }
        });
        $('#userff').submit();
    }

    function setUserQueryPar(){
        var queryParams = $('#userTable').datagrid('options').queryParams;
        queryParams.account = null;
        queryParams.realName = null;
        var search_account = $('#search_account').val();
        var search_realName = $('#search_realName').val();
        if(search_account!=null && search_account!="")
            queryParams.account = search_account;
        if(search_realName!=null && search_realName!="")
            queryParams.realName = search_realName;

        $('#userTable').datagrid('reload');
    }

    function userAddDialog(){
        url = "/VSLC/user/add";
        $("#userff").form("clear");
        var values = {sex : 1};
        $("#userff").form("load", values);
        $("#userDLG").dialog("open");
        $("#ts").val("");
    }

    function userUpdateDialog(){
        url = "/VSLC/user/update";
        var row = $("#userTable").datagrid("getSelected");
        if(row){
            var values = {
                userID:row.userID,
                userAccount : row.userAccount,
                userPassword : row.userPassword,
                permissionGroupID : row.permissionGroup.permissionGroupID,
                realName:row.realName,
                sex:row.sex,
                birday: transferDate(row.birthday, 2),
                cardID:row.cardID,
                hospitalID:row.hospital!=null ? row.hospital.hospitalID:null,
                provinceID: row.province!=null ? row.province.provinceID:null,
                cityID:row.city!=null ? row.city.cityID:null,
                address:row.address,
                postCode:row.postCode,
                title:row.title,
                tel:row.tel,
                fax:row.fax,
                email:row.email
            };
            $("#userff").form("load",values);
            $("#userDLG").dialog("open");
            $("#ts").val("");
        }
    }

    function deleteUser() {
        var row = $("#userTable").datagrid("getSelected");
        if(row){
            var userID = row.userID;
            $.messager.confirm("操作提示", "确定要删除吗?", function(data){
                if(data){
                    $.post("/VSLC/user/delete",{userID:userID}, function (result){
                        if (result.success) {
                            $("#userTable").datagrid("reload");
                        }
                    },"json");
                }
            })
        }
    }

    function transferDate(date, key) {
        if (date == "" || date == null) return "";
        //解析时间戳
        var jsonDate = new Date(parseInt(date));
        //为Date对象添加一个新属性，主要是将解析到的时间数据转换为“yyyy-MM-dd”格式
        Date.prototype.format = function(format) {
            var o = {
                //获得解析出来数据的相应信息，可参考js官方文档里面Date对象所具备的方法
                "y+" : this.getFullYear(),//得到对应的年信息
                "M+" : this.getMonth() + 1, //得到对应的月信息，得到的数字范围是0~11，所以要+1
                "d+" : this.getDate(), //得到对应的日信息
                "h+" : this.getHours(), //得到对应的小时信息
                "m+" : this.getMinutes(), //得到对应的分钟信息
                "s+" : this.getSeconds() //得到对应的秒信息
            };
            //将年转换为完整的年形式
            if (/(y+)/.test(format)) {
                format = format.replace(RegExp.$1,
                    (this.getFullYear() + "")
                        .substr(4 - RegExp.$1.length));
            }
            //连接得到的年月日 时分秒信息
            for ( var k in o) {
                if (new RegExp("(" + k + ")").test(format)) {
                    format = format.replace(RegExp.$1,
                        RegExp.$1.length == 1 ? o[k] : ("00" + o[k])
                            .substr(("" + o[k]).length));
                }
            }
            return format;
        };
        var dateFormat = jsonDate.format("yyyy/MM/dd");
        var dateFormat2 = jsonDate.format("yyyy-MM-dd");
        var TimeFormat = jsonDate.format("yyyy/MM/dd hh:mm:ss");
        if (key == 0) {
            return dateFormat;
        } else if (key == 1) {
            return TimeFormat;
        } else if (key == 2) {
            return dateFormat2;
        }
    }
</script>

</body>
</html>
