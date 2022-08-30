<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/3/7
  Time: 17:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>图像审核</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="icon" href="../img/bg/delogo.ico" type="image/x-icon"/>
    <link rel="stylesheet" href="../css/self-adaption.css" type="text/css"/>
    <link rel="stylesheet" href="../css/review.css" type="text/css"/>
    <link rel="stylesheet" href="../css/cornerstone.min.css" type="text/css"/>
    <link rel="stylesheet" href="../css/modal.min.css" type="text/css"/>
    <link rel="stylesheet" href="../css/toastr.css" type="text/css"/>
    <link rel="stylesheet" href="../css/buttons.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/default/easyui.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/icon.css" type="text/css"/>
    <script type="text/javascript" src="../js/jquery.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/self-adaption.js"></script>
    <script type="text/javascript" src="../js/info/toastr.js"></script>
    <script type="text/javascript" src="../js/info/buttons.js"></script>
</head>
<body>

<div class="shader">
    <div id="preloader_1">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
    </div>
</div>
<div class="result-shader">
    <div id="indicatorContainer"></div>
</div>
<div class="modal fade" id="add_modal" tabindex="-1" role="dialog" aria-labelledby="addLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="addLabel">选择编号</h2>
            </div>
            <div class="modal-body" style="text-align: center">
                <div class="addDiv">
                    <div class="add-sortDiv">
                        <label>请选择类型</label>
                        <select id="addSortInput">
                            <option value="nodule.bin">病灶</option>
                            <option value ="lung.bin">肺分割结果</option>
                            <option value ="airWay.bin">气管</option>
                            <option value="artery.bin">动脉</option>
                            <option value="vein.bin">静脉</option>
                        </select>
                    </div>
                    <div class="add-numDiv">
                        <label>请填入编号</label>
                        <input type="number" id="addNumInput"/>
                    </div>
                    <button id="addNumButton">确定</button>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>
<div class="title">
    <a class="logo" href="info"><img src="../img/bg/logo.png"></a>
    <div class="tools-right">
        <a><div id="save_sketch"><img src="../img/icon/save.png"><span>保存</span></div></a>
        <a><div id="last_review"><img src="../img/icon/last.png"><span>上条</span></div></a>
        <a><div id="next_review"><img src="../img/icon/next.png"><span>下条</span></div></a>
    </div>
</div>
<div class="functionZone">

    <div class="tableInfo">
            <div class="tableInfo_02">
                <div class="tableInfo_02_01">工具</div>
                <div class="tableInfo_02_00">
                    <a><div id="enablePan"><img src="../img/icon/move.png"></div></a>
                    <a><div id="enableLength"><img src="../img/icon/ruler.png"></div></a>
                    <a><div id="enablePlay"><img id="playIcon" state="pause" src="../img/icon/play.png"></div></a>
                    <a><div id="enableReset"><img src="../img/icon/reset.png"></div></a>
                    <a><div id="enableDraw"><img src="../img/icon/pencil.png"></div></a>
                    <a><div id="enableWwcc"><img src="../img/icon/bright.png"></div></a>
                    <span class="button-dropdown" data-buttons="dropdown" style="position: absolute">
                        <button class="button button-block button-rounded button-tiny" style="padding: .23rem .05rem;background-color: #d2cece">
                            <img style="width: .1rem; height: .1rem" src="../img/icon/triangle.png">
                            <i class="fa fa-caret-down"></i>
                        </button>
                        <ul class="button-dropdown-list is-below" style="width: 1.5rem;">
                            <li id="abdomenWin"><a>纵隔窗</a></li>
                            <li id="lungWin"><a>肺窗</a></li>
                            <li id="boneWin"><a>骨窗</a></li>
                        </ul>
                    </span>
                    <a><div id="enableMeasure"><img src="../img/icon/HU.png"></div></a>
                </div>
            </div>
            <div class="sketchBoxTitle">标注结果</div>
            <div id="sketchBox">
                <ul id="sketchTree" class="easyui-tree"></ul>
            </div>
        <div class="tableInfo_03">
            <div class="tableInfo_03_01" id="scoreBoxTitle">选择序列打分</div>
            <div class="tableInfo_03_02">
                <table cellspacing="0" cellpadding="0" id="suggestionList">
                    <thead>
                    <tr>
                        <td style="width:12%">编号</td>
                        <td style="width:12%">评分</td>
                        <td style="width:76%">意见</td>
                    </tr>
                    </thead>
                </table>
            </div>
            <div class="tableInfo_03_03">
                <a><div class="tableInfo_03_03_01" id="addSuggestion" data-toggle="modal" data-target="#add_modal"><span>+</span></div></a>
                <a><div class="tableInfo_03_03_02" id="removeSuggestion"><span>-</span></div></a>
                <a><div class="tableInfo_03_03_03" id="reportSubmit"><span>报告</span></div></a>
            </div>
        </div>
        <!-- 征象表格 -->
        <div id="noduleTitle" class="tableInfo_04_01">结节信息</div>
        <div class="tableInfo_04">
            <table cellspacing="0" cellpadding="0">
                <tr class="tableTitle">
                    <td>形态特征</td><td>值</td>
                </tr>
                <tr>
                    <td>位置</td><td><input id="table_position" type="text"></td>
                </tr>
                <tr>
                    <td>肺叶</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>肺段</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>最大CT值</td><td><input id="table_maxCT" type="text"></td>
                </tr>
                <tr>
                    <td>最小CT值</td><td><input id="table_minCT" type="text"></td>
                </tr>
                <tr>
                    <td>平均CT值</td><td><input id="table_avgCT" type="text"></td>
                </tr>
                <tr>
                    <td>体积（mm³）</td><td><input id="table_volume" type="text"></td>
                </tr>
                <tr>
                    <td>最大直径（mm）</td><td><input id="table_diameter" type="text"></td>
                </tr>
                <tr>
                    <td>三维比值</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>实性部分体积</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>实性/整体比率</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>风险</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>是否毛刺</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>是否分页</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>边缘是否光滑</td><td><input type="text"></td>
                </tr>
                <tr>
                    <td>磨玻璃</td><td><input type="text"></td>
                </tr>
            </table>
        </div>
    </div>

    <div class="picZone">
        <div class="frameNumber_01"></div>
        <div class="frameNumber_02"></div>
        <div class="frameNumber_03"></div>
        <div id="reloadImg">
            <div class="picZone_02">
                <div class="picZone_02_01">
                    <div class="container">
                        <div class="row">
                            <div style="width:100%;height:100%;color:white"
                                 oncontextmenu="return false"
                                 class='cornerstone-enabled-image'
                                 unselectable='on'
                                 onselectstart='return false;'
                                 onmousedown='return false;'>
                                <div id="dicomImage"
                                     style="width:100%;height:100%;position:absolute;">
                                </div>
                                <div id="imageIndexSpan" class="waterMark"></div>
                                <div id="sequenceNumSpan" class="waterMark"></div>
                                <div id="maxCTSpan" class="waterMark"></div>
                                <div id="minCTSpan" class="waterMark"></div>
                                <div id="avgCTSpan" class="waterMark"></div>
                                <div id="thicknessSpan" class="waterMark"></div>
                                <div id="WHSpan" class="waterMark"></div>
                                <div id="scaleSpan" class="waterMark"></div>
                                <div id="WWWLSpan" class="waterMark"></div>
                                <div id="toolsState" class="waterMark"></div>
                            </div>
                        </div>
                    </div>
                    <input type="range" id="slice-range" style="position: absolute;top: 0px;right: 10px;
                                                    display: none;
                                                    transform-origin: right center;
                                                    -webkit-transform-origin: right center;
                                                    -moz-transform-origin: right center;
                                                    transform:rotate(90deg);
                                                    -webkit-transform:rotate(90deg);  /*兼容-webkit-引擎浏览器*/
                                                    -moz-transform:rotate(90deg);     /*兼容-moz-引擎浏览器*/ "/>
                </div>

                <%--<div class="picZone_02_01">--%>
                    <%--<div class="container">--%>
                        <%--<div class="row">--%>
                            <%--<div style="width:100%;height:100%;color:white"--%>
                                 <%--oncontextmenu="return false"--%>
                                 <%--class='cornerstone-enabled-image'--%>
                                 <%--unselectable='on'--%>
                                 <%--onselectstart='return false;'--%>
                                 <%--onmousedown='return false;'>--%>
                                <%--<div id="dicomImage1"--%>
                                     <%--style="width:100%;height:100%;position:absolute;">--%>
                                <%--</div>--%>
                                <%--<div id="imageIndexSpan1" class="waterMark"></div>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<input type="range" id="slice-range1" style="position: absolute;top: 0px;right: 10px;--%>
                                                    <%--display: none;--%>
                                                    <%--transform-origin: right center;--%>
                                                    <%---webkit-transform-origin: right center;--%>
                                                    <%---moz-transform-origin: right center;--%>
                                                    <%--transform:rotate(90deg);--%>
                                                    <%---webkit-transform:rotate(90deg);  /*兼容-webkit-引擎浏览器*/--%>
                                                    <%---moz-transform:rotate(90deg);     /*兼容-moz-引擎浏览器*/ "/>--%>
                <%--</div>--%>
                <%--<div class="picZone_02_01">--%>
                    <%--<div class="container">--%>
                        <%--<div class="row">--%>
                            <%--<div style="width:100%;height:100%;color:white"--%>
                                 <%--oncontextmenu="return false"--%>
                                 <%--class='cornerstone-enabled-image'--%>
                                 <%--unselectable='on'--%>
                                 <%--onselectstart='return false;'--%>
                                 <%--onmousedown='return false;'>--%>
                                <%--<div id="dicomImage2"--%>
                                     <%--style="width:100%;height:100%;position:absolute;">--%>
                                <%--</div>--%>
                                <%--<div id="imageIndexSpan2" class="waterMark"></div>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<input type="range" id="slice-range2" style="position: absolute;top: 0px;right: 10px;--%>
                                                    <%--display: none;--%>
                                                    <%--transform-origin: right center;--%>
                                                    <%---webkit-transform-origin: right center;--%>
                                                    <%---moz-transform-origin: right center;--%>
                                                    <%--transform:rotate(90deg);--%>
                                                    <%---webkit-transform:rotate(90deg);  /*兼容-webkit-引擎浏览器*/--%>
                                                    <%---moz-transform:rotate(90deg);     /*兼容-moz-引擎浏览器*/ "/>--%>
                <%--</div>--%>
                <%--<div class="picZone_02_01">--%>
                    <%--<div class="container">--%>
                        <%--<div class="row">--%>
                            <%--<div style="width:100%;height:100%;color:white"--%>
                                 <%--oncontextmenu="return false"--%>
                                 <%--class='cornerstone-enabled-image'--%>
                                 <%--unselectable='on'--%>
                                 <%--onselectstart='return false;'--%>
                                 <%--onmousedown='return false;'>--%>
                                <%--<div id="dicomImage3"--%>
                                     <%--style="width:100%;height:100%;position:absolute;">--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</div>--%>

            </div>
        </div>

    </div>

    <div class="picInfo">
        <div class="picInfo_000">
            <div class="picInfo_00">
                <div class="picInfo_01">
                    <div class="picInfo_01_00"></div>
                    <div class="picInfo_01_01"></div>
                    <div class="picInfo_01_02"></div>
                </div>
                <a><div class="picInfo_02"></div></a>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var examiner = "${curUser.userID}";
</script>
</body>
<script type="text/javascript" src="../js/jquery.nicescroll.min.js"></script>
<script type="text/javascript" src="../js/jquery.mousewheel.min.js"></script>
<script type="text/javascript" src="../js/review/cornerstone.min.js"></script>
<script type="text/javascript" src="../js/review/cornerstoneMath.min.js"></script>
<script type="text/javascript" src="../js/review/cornerstoneTools.js"></script>
<script type="text/javascript" src="../js/radialIndicator.js"></script>
<script type="text/javascript" src="../js/modal.min.js"></script>
<script type="text/javascript" src="../js/review/loadAndScroll.js"></script>
<script type="text/javascript" src="../js/review/exampleImageLoader.js"></script>
<script type="text/javascript" src="../js/review/review.js"></script>
</html>
