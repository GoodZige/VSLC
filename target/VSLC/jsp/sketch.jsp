<%--
  Created by IntelliJ IDEA.
  User: chenlele
  Date: 2018/5/18
  Time: 11:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*,com.vslc.model.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>图像勾画</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="icon" href="../img/bg/delogo.ico" type="image/x-icon"/>
    <link rel="stylesheet" href="../css/self-adaption.css" type="text/css"/>
    <link rel="stylesheet" href="../css/cornerstone.min.css" type="text/css"/>
    <link rel="stylesheet" href="../css/toastr.css" type="text/css"/>
    <link rel="stylesheet" href="../css/buttons.css" type="text/css"/>
    <link rel="stylesheet" href="../css/sketch.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/default/easyui.css" type="text/css"/>
    <link rel="stylesheet" href="../themes/icon.css" type="text/css"/>
    <script type="text/javascript" src="../js/jquery.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/self-adaption.js"></script>
</head>
<body>

<div class="title">
    <a class="logo" href="info"><img src="../img/bg/logo.png"></a>
    <!-- 工具栏 -->
    <div class="tools">
        <div id="enablePan"><img src="../img/icon/move.png"><span>移动</span></div>
        <div id="enableLength"><img src="../img/icon/ruler.png"><span>测量</span></div>
        <div id="enablePlay"><img id="playIcon" state="pause" src="../img/icon/play.png"><span>播放</span></div>
        <div id="enableReset"><img src="../img/icon/reset.png"><span>重置</span></div>
        <div id="enableDraw"><img src="../img/icon/pencil.png"><span>画笔</span></div>
        <div id="enableWwcc"><img src="../img/icon/bright.png"><span>调窗</span></div>
        <span class="button-dropdown" data-buttons="dropdown" style="position: absolute">
            <button class="button button-block button-rounded button-tiny"
                    style="margin: .1rem 0;height: .5rem;padding: .23rem .05rem;background-color: #d2cece">
                <img style="width: .1rem; height: .1rem" src="../img/icon/triangle.png">
                <i class="fa fa-caret-down"></i>
            </button>
            <ul class="button-dropdown-list is-below" style="width: 2rem">
                <li id="abdomenWin"><a>纵隔窗</a></li>
                <li id="lungWin"><a>肺窗</a></li>
                <li id="boneWin"><a>骨窗</a></li>
            </ul>
        </span>
    </div>
    <!-- 勾画结果树 -->
    <div class="sketch_result"></div>
</div>
<div class="main">

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
                                     style="width:100%;height:100%;top:0px;left:0px; position:absolute;">
                                </div>
                                <div class="waterMark" style="left:0.1rem; top:0.1rem;">
                                    <div id="imageIndexSpan"></div>
                                </div>
                                <div class="waterMark" style="left:0.1rem; top:0.4rem;">
                                    <div id="sequenceNumSpan"></div>
                                </div>
                                <div class="waterMark" style="left:0.1rem; bottom:0.4rem;">
                                    <div id="thicknessSpan"></div>
                                </div>
                                <div class="waterMark" style="left:0.1rem; bottom:0.1rem;">
                                    <div id="WHSpan"></div>
                                </div>
                                <div class="waterMark" style="right:0.1rem; bottom:0.4rem;">
                                    <div id="scaleSpan"></div>
                                </div>
                                <div class="waterMark" style="right:0.1rem; bottom:0.1rem;">
                                    <div id="WWWLSpan"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <input type="range" id="slice-range" style="position: absolute;top: 0px;right: .3rem;
                                                    display: none;
                                                    transform-origin: right center;
                                                    -webkit-transform-origin: right center;
                                                    -moz-transform-origin: right center;
                                                    transform:rotate(90deg);
                                                    -webkit-transform:rotate(90deg);  /*兼容-webkit-引擎浏览器*/
                                                    -moz-transform:rotate(90deg);     /*兼容-moz-引擎浏览器*/ "/>
    </div>

    <div class="picInfo">
        <div class="picInfo_000">
            <div class="picInfo_00">
                <div class="picInfo_01">
                    <div class="picInfo_01_00"></div>
                    <div class="picInfo_01_01"></div>
                    <div class="picInfo_01_02"></div>
                </div>
                <div class="picInfo_02">
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var sketcher = "${curUser.userID}";
</script>
</body>
<script type="text/javascript" src="../js/jquery.mousewheel.min.js"></script>
<script type="text/javascript" src="../js/review/hammer.min.js"></script>
<script type="text/javascript" src="../js/review/cornerstone.min.js"></script>
<script type="text/javascript" src="../js/review/cornerstoneMath.min.js"></script>
<script type="text/javascript" src="../js/review/cornerstoneTools.js"></script>
<script type="text/javascript" src="../js/review/exampleImageLoader.js"></script>
<script type="text/javascript" src="../js/review/loadAndScroll.js"></script>
<script type="text/javascript" src="../js/info/toastr.js"></script>
<script type="text/javascript" src="../js/info/buttons.js"></script>
<script type="text/javascript" src="../js/review/sketch.js"></script>
</html>
