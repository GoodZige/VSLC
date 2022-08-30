<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/3/7
  Time: 16:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>信息管理</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="icon" href="../img/bg/delogo.ico" type="image/x-icon"/>
    <link rel="stylesheet" href="../css/self-adaption.css" type="text/css"/>
    <script type="text/javascript" src="../js/self-adaption.js"></script>
    <link rel="stylesheet" href="../css/info.css" type="text/css"/>
    <link rel="stylesheet" href="../css/buttons.css" type="text/css"/>
    <link rel="stylesheet" href="../css/modal.min.css" type="text/css"/>
    <link rel="stylesheet" href="../css/lc_switch.css" type="text/css"/>
    <script type="text/javascript" src="../js/jquery.min.js"></script>
    <link rel="stylesheet" href="../themes/default/easyui.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/icon.css" type="text/css"/>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/easyui-lang-ch.js"></script>
    <script type="text/javascript" src="../js/info/lc_switch.js"></script>
    <script type="text/javascript" src="../js/info/auto.js"></script>
    <script type="text/javascript" src="../js/info/info.js"></script>
</head>
<body>

<div class="container">
    <div class="shader">
        <div id="preloader">
            <span></span>
            <span></span>
            <span></span>
            <span></span>
            <span></span>
        </div>
    </div>
    <div class="result-shader">
        <div id="indicatorContainer1"></div>
    </div>
    <!-- 导航栏1 -->
    <div class="top">
        <div style="width: 16rem; margin: 0 auto;">
            <div class="logo">
                <a href="info" target="_self"><img src="../img/bg/logo.png"/></a>
            </div>
            <div class="nav">
                <ul class="nav" style="margin-top: 3px">
                    <li><a id="surface" style="color: #38f">浏览</a></li>
                    <li>
                        <select id="timeSearch" style="position: absolute; z-index: 1;"
                                onmousedown="if(this.options.length>5){this.size=6}" onblur="this.size=0" onchange="this.size=0">
                            <option value="all">AllTime</option>
                            <option value="90">最近三个月</option>
                            <option value="183">最近半年</option>
                            <option value="365">最近一年</option>
                        </select>
                    </li>
                    <li>
                        <select id="hospitalSearch" style="position: absolute; z-index: 1; margin-left: 90px;"
                                onmousedown="if(this.options.length>5){this.size=6}" onblur="this.size=0" onchange="this.size=0">
                            <option id="allHospital">AllHospital</option>
                        </select>
                    </li>
                </ul>
            </div>
            <div style="float: right;">
                <ul class="ic_nav">
                    <li><a href="authority">欢迎你，${curUser.realName}</a></li>
                    <li><a id="logout">注销</a></li>
                </ul>
            </div>
        </div>
    </div>
    <!-- 导航栏2 -->
    <div class="top_second">
        <div style="width: 16rem;margin: 0 auto">
            <div class="nav">
                <div class="search">
                    <input id="search_txt" type="text" maxlength="1000px"
                           placeholder="姓名、ID、CT、逻辑表达式"/>
                    <a id="search_btn" title="搜索"><img class="ic_search" src="../img/icon/search.png"></a>
                    <div class="auto hidden" id="auto">
                        <div class="auto_out">1</div>
                        <div class="auto_out">2</div>
                    </div>
                </div>
            </div>
            <div class="nav_right">
                <button class="button button-rounded button-tiny" id="upload-open" data-toggle="modal" data-target="#file_modal">文件导入</button>
                <div class="modal fade" id="file_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
                    <div class="modal-dialog" id="modal-upload">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                <h2 class="modal-title" id="myModalLabel">文件导入</h2>
                            </div>
                            <div class="modal-body" style="text-align: center" id="modal-upload-body">
                                <div id="indicator">
                                    <div id="indicatorContainer"></div>
                                    <div id="tipForUpload">文件上传中</div>
                                </div>
                                <div id="upload-box1">
                                    <form id="file_upload" method="post" enctype="multipart/form-data">
                                        <button type="button" class="button button-rounded button-tiny" id="importBtn">
                                            文件选择
                                        </button>
                                        <br/>
                                        <span>已选择<span id="file_num">0</span>个文件</span>
                                        <input style="display: none" type="file" name="fileFolder" id="file_input"
                                               webkitdirectory/>
                                    </form>
                                    <br/>
                                    <input class="button button-rounded button-tiny button-primary" type="button"
                                           value="确认" id="file_confirm"/>
                                </div>
                                <div id="upload-box2">
                                    <div class="upload-info-header">
                                        <span class="upload-th">已有数据</span>
                                        <span class="upload-th">导入数据</span>
                                    </div>
                                    <div class="upload-info-old"></div>
                                    <div class="upload-info-new"></div>
                                    <p>已选择<span id="newsqsLength">0</span>个序列,<span id="maskLength">0</span>个标注结果</p>
                                    <span class="button-dropdown" id="hospitalSpan" data-buttons="dropdown" style="margin: 12px 0px;display: none">
                                        <button class="button button-block button-rounded button-tiny" style="width: 2.8rem;"
                                                id="chooseHospital">
                                          选择医院 <i class="fa fa-caret-down"></i>
                                        </button>
                                        <ul class="button-dropdown-list is-below" style="text-align: left" id="hospital-ul">

                                        </ul>
                                    </span>
                                    <button id="uploadSequence" class="button">导入</button>
                                </div>
                            </div>
                        </div><!-- /.modal-content -->
                    </div><!-- /.modal -->
                </div>
                <div class="modal fade" id="download_modal" tabindex="-1" role="dialog" aria-labelledby="downloadLabel" aria-hidden="true">
                    <div class="modal-dialog" id="modal-export">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                <h2 class="modal-title" id="downloadLabel">文件导出 \\192.168.195\Export</h2>
                            </div>
                            <div class="modal-body" style="text-align: center">
                                <div class="download-info-header">
                                    <span class="download-th">检查</span>
                                    <span class="download-th">序列</span>
                                </div>
                                <div class="download-info-check"></div>
                                <div class="download-info-sequence"></div>
                                <p>已选择<span id="sqsLength">0</span>个序列</p>
                                <button id="downloadSequence" class="button">导出</button>
                            </div>
                        </div><!-- /.modal-content -->
                    </div><!-- /.modal -->
                </div>
                <div class="modal fade" id="distribute_modal" tabindex="-1" role="dialog" aria-labelledby="distributeLabel" aria-hidden="true">
                    <div class="modal-dialog" id="modal-distribute">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                <h2 class="modal-title" id="distributeLabel">任务分配</h2>
                            </div>
                            <div class="modal-body">
                                <div class="distribute-header">
                                    <select id="permissionGroupSelect" style="height: 100%">
                                        <option value="all">所有权限组</option>
                                        <option value="3">勾画医生</option>
                                        <option value="4">勾画审核医生</option>
                                        <option value="5">征象分析医生</option>
                                        <option value="6">征象审核医生</option>
                                    </select>
                                    <input id="userSearch" type="text" maxlength="500px" placeholder="用户名" style="margin-left: .2rem;padding-left: .05rem;height: 100%"/>
                                    <a id="userSearch_btn" title="搜索"><img class="ic_search" src="../img/icon/search.png"></a>
                                </div>
                                <div class="distribute-users">
                                    <table cellspacing="0" cellpadding="0" id="distributeUsers">
                                        <thead><tr><td>用户名</td><td>权限组</td></tr></thead>
                                    </table>
                                </div>
                                <div class="distribute-handle">
                                    <span id="checksNum"></span>
                                    <button id="distributeBtn" class="dis_btn">分配</button>
                                </div>
                            </div>
                        </div><!-- /.modal-content -->
                    </div><!-- /.modal -->
                </div>
            </div>
        </div>
    </div>
    <!-- 内容 -->
    <div class="main">
        <!-- 左边内容 -->
        <div class="left">
            <!-- 标题 -->
            <div style="float: left;padding: .15rem;"><h2>结果</h2></div>
            <!-- tab按钮 -->
            <div style="float: right;padding: .2rem .1rem;">
                <a class="tab" id="tab_check">检查</a>
                <a class="tab" id="tab_patient">病人</a>
            </div>
            <!-- tab检查左视图 -->
            <div id="left_check_box" style="width: 98%; height: 89%;margin: 0 auto;">
                <table id="left_check_datagrid" class="easyui-datagrid" style="width:100%;height: 100%;"></table>
            </div>
            <!-- tab病人左视图 -->
            <div id="left_patient_box" style="width: 98%; height: 89%;margin: 0 auto;">
                <table id="left_patient_datagrid" class="easyui-datagrid" style="width:100%;height: 100%"></table>
            </div>
        </div>
        <!-- 右边内容 -->
        <div class="right">
            <!-- tab检查右视图 -->
            <div id="right_check_box">
                <!-- 标题 -->
                <div style="float: left;padding: .15rem">
                    <select id="winSelect" style="height: .4rem;font-size: .25rem;">
                        <option winWidth="1600" winCenter="-600">肺窗</option>
                        <option winWidth="400" winCenter="60">纵隔窗</option>
                        <option winWidth="2000" winCenter="300">骨窗</option>
                    </select>
                </div>
                <%--<div style="float: left;padding: .15rem"><h2>序列</h2></div>--%>
                <div style="float: right;padding: .15rem;">
                    <input id="switchMask" type="checkbox" class="lcs_check lcs_tt1" checked="checked" autocomplete="off"/>
                </div>
                <script type="text/javascript">
                    $(document).ready(function(e) {
                        $("#switchMask").lc_switch();
                    });
                </script>
                <!-- tab检查右数据表 -->
                <div style="width: 3.7rem;height: 2.58rem;margin:0 auto;">
                    <table id="right_check_datagrid" class="easyui-datagrid" style="width: 100%;height:100%;"></table>
                </div>
                <!-- dcm滚动 -->
                <div id="dcm_scroll">
                    <canvas style="width: 100%;height: 100%;" id="dcm_canvas" width="512" height="512">你的浏览器不支持canvas，推荐使用chrome浏览器</canvas>
                    <input type="range" id="dcm_range" min="0" max="300" step="1" value="0" style="width: 3.7rem;height: 21px;
                                                              display: none;
                                                              float: left;
                                                              margin: -10.1px 0px 0px 0px;
                                                              transform-origin: right center;
                                                              -webkit-transform-origin: right center;
                                                              -moz-transform-origin: right center;
                                                              transform:rotate(90deg);
                                                              -webkit-transform:rotate(90deg);  /*兼容-webkit-引擎浏览器*/
                                                              -moz-transform:rotate(90deg);     /*兼容-moz-引擎浏览器*/ "/>
                </div>
            </div>
            <!-- tab病人右视图 -->
            <div id="right_patient_box">
                <!-- 标题 -->
                <div style="width:100%;padding: .15rem"><h2>时间轴</h2></div>
                <!-- 时间轴 -->
                <div class="timebar_box">
                    <!-- 滚动框 -->
                    <div id="box_scroll">
                        <div id="content_scroll">
                            <!-- 时间轴 -->
                            <div class="about4">
                                <div class="about4_main">
                                    <div class="line"></div>
                                    <ul id="timebar_ul"></ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- tab检查右数据表 -->
                <div id="examination_box" style="display: none">
                    <div style="width:100%;padding: 0 .1rem .2rem"><h2>检验</h2></div>
                    <img style="width: 100%;height: 100%;" id="examination_img"/>
                </div>
                <div id="pathology_box" style="display: none">
                    <div style="width:100%;padding: 0 .1rem .2rem"><h2>病理</h2></div>
                    <img id="pathology_img"/>
                </div>
                <div id="right_sequence_box" style="margin:0 auto;width: 3.7rem;height: 3.05rem">
                    <div style="width:100%;padding: .1rem"><h2>序列</h2></div>
                    <table id="right_patient_datagrid" class="easyui-datagrid" style="width:100%;height:100%;"></table>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="../js/info/auto.js"></script>
<script type="text/javascript">
    var editor = parseInt("${curUser.userID}");
    var permission = parseInt("${curPermission.permissionGroupID}");
    if (permission == 1) {
        var uploader = "all";
        var drawer = "all";
        var drawExaminer = "all";
        var signer = "all";
        var signExaminer = "all";
    } else {
        var uploader = editor;
        var drawer = editor;
        var drawExaminer = editor;
        var signer = editor;
        var signExaminer = editor;
    }

    var autoComplete = new AutoComplete("search_txt", "auto", logicalTip);
    document.getElementById("search_txt").onkeyup = function(event){
        autoComplete.start(event);
    }
</script>

</body>
<link rel="stylesheet" href="../css/toastr.css" type="text/css"/>
<link rel="stylesheet" href="../css/zoomify.css" type="text/css"/>
<script type="text/javascript" src="../js/jquery.nicescroll.min.js"></script>
<script type="text/javascript" src="../js/jquery.mousewheel.min.js"></script>
<script type="text/javascript" src="../js/info/zoomify.min.js"></script>
<script type="text/javascript" src="../js/info/buttons.js"></script>
<script type="text/javascript" src="../js/modal.min.js"></script>
<script type="text/javascript" src="../js/info/toastr.js"></script>
<script type="text/javascript" src="../js/radialIndicator.js"></script>
<script type="text/javascript" src="../js/info/datagrid-detailview.js"></script>
</html>
