var targetElement = document.getElementById('dicomImage');
var currentDraw = null;
var currentResult = null;
var sketchNumResult = null;
var addX = null;
var addY = null;
var imgWidth = null;
var imgHeight = null;
var canvasImgChange = null;
var $sequenceID;
var $sketchFile;
var $sketchType;
var $sketchNum;
var alreadySign = false;
var inspectionList;
var inspectionIndex = 0;
var imgIndexs;
var noduleCalcs = []; //当前正在计算的结节
var curObjNums = [];

$(document).ready(function () {
    toastr.options.timeOut="3000";
    toastr.options.positionClass="toast-top-left";
    toastr.options.closeButton=true;
    $(".picInfo_00").niceScroll({
        cursoropacitymin: 0,
        cursoropacitymax: 0
    });
    inspectionList = getInspectionCookie("inspections");
    getInspectionInfo(inspectionList[0]);
    getSequenceList(inspectionList[0]);
});

//上一条
$("#last_review").click(function () {
    if (inspectionIndex > 0) {
        inspectionIndex--;
        if ($("#toolsState").text() != '')
            clearCornerstoneTools();
        cornerstone.disable(targetElement);
        alreadySign=false;
        $("#aboveCanvas").remove();
        getInspectionInfo(inspectionList[inspectionIndex]);
        getSequenceList(inspectionList[inspectionIndex]);
        toastr.info('当前任务：' + (inspectionIndex+1) + '  /  ' + inspectionList.length);
        $("#slice-range").hide();
        $("#sketchTree").tree('loadData', []);
        clearMark();
        cleanGrade();
        clearNoduleInfo();
    } else if (inspectionIndex == 0) {
        alert("这是第一条审核任务");
    }
});

//下一条
$("#next_review").click(function () {
    if (inspectionIndex < inspectionList.length - 1) {
        inspectionIndex++;
        if ($("#toolsState").text() != '')
            clearCornerstoneTools();
        cornerstone.disable(targetElement);
        alreadySign=false;
        $("#aboveCanvas").remove();
        getInspectionInfo(inspectionList[inspectionIndex]);
        getSequenceList(inspectionList[inspectionIndex]);
        toastr.info('当前任务：' + (inspectionIndex+1) + '  /  ' + inspectionList.length);
        $("#slice-range").hide();
        $("#sketchTree").tree('loadData', []);
        clearMark();
        cleanGrade();
        clearNoduleInfo();
    } else if (inspectionIndex == inspectionList.length - 1) {
        if (confirm("本组任务已完成，是否返回主页选择下一组任务"))
            window.location.href = '/VSLC/page/info';
    }
});

function getInspectionInfo(inspectionID) {
    $.ajax({
        type: 'post',
        url: '/VSLC/inspection/getInspectionInfo',
        data: {"inspectionID":inspectionID},
        success: function (data) {
            $(".picInfo_01_00").html(data.patientName);
            $(".picInfo_01_01").html(transferDate(data.inspectionTime, 1));
        }
    });
}

function getSequenceList(inspectionID) {
    // 获取序列列表
    $.ajax({
        type: 'post',
        url: '/VSLC/sequence/getSequenceList',
        data: {"inspectionID":inspectionID},
        success: function (data) {
            var $html = '';
            $(".picInfo_01_02").html("CT/SR : " + data.length + " series");
            for(let i = 0; i < data.length; i++){
                $html += '<div class="picInfo_02_01" zWidth='+ data[i].width +' yHeight='+ data[i].height +' fileNum='+ data[i].fileNum +' sequenceID='+ data[i].sequenceID +' thickness='+ data[i].thickness +' sequenceNum='+ data[i].sequenceNum +'>';
                $html += '<div class="picInfo_02_01_01">'+ transferIsSketch(data[i].isSketch) + "  " + data[i].sequenceName +'</div>';
                $html += '<div class="picInfo_02_01_02"><div class="picInfo_02_01_02_01" style="background:url(/VSLC/function/displayDcm?sequenceID=' + data[i].sequenceID + ') no-repeat 100% 100%;background-size:cover"></div></div></div>';
            }
            $(".picInfo_02").html($html);
            // 选择序列
            $(".picInfo_02_01").click(function(){
                if ($("#toolsState").text() != '') {
                    $("#toolsState").text('');
                    clearCornerstoneTools();
                }
                sumx=0,sumy=0;
                cornerstone.disable(targetElement);
                cornerstone.enable(targetElement);
                alreadySign=false;
                $("#aboveCanvas").remove();
                // var $filenum = $(this).attr("fileNum");
                var $height = $(this).attr("yHeight");
                var $width = $(this).attr("zWidth");
                imgWidth = $width;
                imgHeight = $height;
                // $(".picZone_02_01").attr("fileNum",$filenum);
                // $(".picZone_02_02").attr("height",$height);
                // $(".picZone_02_03").attr("width",$width);
                $(".picInfo_02_01").removeClass("listChoose");
                $(this).addClass("listChoose");
                $("canvas").css("width","100%");
                $("canvas").css("height","100%");
                let lastSequenceID = $sequenceID;
                let tempId;
                $sequenceID = $(this).attr("sequenceID");
                //图像名称不一定从1开始
                getImageList($sequenceID);
                let imageIds1 = [];
                for(var i = 1; i < imgIndexs.length; i++)
                    imageIds1.push("example://"+$sequenceID+"_x_" + i + "_" + imgIndexs[i-1]);
                // for(var i = 1; i < (Number($filenum)+1); i++)
                //     imageIds1.push("example://"+$sequenceID+"_x_" + i);
                // for(var j = 1; j < (Number($height)+1); j++)
                //     imageIds2.push("example://"+$sequenceID+"_y_" + j);
                // for(var k = 1; k < (Number($width)+1); k++)
                //     imageIds3.push("example://"+$sequenceID+"_z_" + k);

                $('#sketchTree').tree({
                    url: '/VSLC/sequence/getSketchList?sequenceID=' + $sequenceID,
                    lines:true,
                    loadFilter: function(data){
                        if (data.fileTree) {
                            if (data.numberList != null)
                                sketchNumResult = data.numberList;
                            else
                                sketchNumResult = null;
                            return data.fileTree;
                        } else
                            return [];
                    },
                    onLoadSuccess: function (node, data) {
                        let sketchSum = 0;
                        curObjNums = [];
                        for (let i = 0;i < data.length; i++) {
                            let n = data[i];
                            if (n.attributes.sketchNum == null) {
                                let nc = n.children;
                                if (nc != null) {
                                    for (let j = 0; j < nc.length; j++) {
                                        if (nc[j].attributes != null)
                                            sketchSum++;
                                        curObjNums.push(nc[j].attributes.sketchNum);
                                    }
                                }
                            } else sketchSum++;
                        }
                        $("#reportSubmit").attr("sketchSum", sketchSum);
                        $("#sketchBox").niceScroll({
                            touchbehavior:true
                        });
                    },
                    onClick: function(node) {
                        if($(this).tree('getParent',node.target)!=null) {
                            $("#scoreBoxTitle").text($(this).tree("getParent", node.target).text);
                        } else {
                            $("#scoreBoxTitle").text(node.text);
                        }
                        let lastFile = $sketchFile;
                        if (lastFile != node.attributes.sketchFile || lastSequenceID != $sequenceID){
                            $sketchFile = node.attributes.sketchFile;
                            $sketchType = node.attributes.sketchType;
                            $sketchNum = node.attributes.sketchNum;
                            let numList = new Array();
                            if ($sketchNum == null) {
                                let child = node.children;
                                if (child != null) {
                                    for (let j = 0; j < child.length; j++) {
                                        if (child[j].attributes != null) {
                                            let num = new Object();
                                            num.sketchNum = child[j].attributes.sketchNum;
                                            num.position = child[j].attributes.position;
                                            numList.push(num);
                                        }
                                    }
                                }
                            } else {
                                let num = new Object();
                                num.sketchNum = $sketchNum;
                                num.position = node.attributes.position;
                                numList.push(num);
                            }
                            if ($sketchNum != null && $sketchFile.indexOf("nodule") != -1) {
                                $.ajax({
                                    type: "get",
                                    url: "/VSLC/sequence/D3NoduleInfo",
                                    data: {
                                        sequenceID: $sequenceID,
                                        sketchNum: $sketchNum
                                    },
                                    beforeSend:function () {
                                        noduleCalcs.push($sketchNum);
                                        clearNoduleInfo();
                                        $(".tableInfo_04_01").css('width', '1.3rem');
                                        let msg = '结节'+$sketchNum+'计算中...';
                                        $("#noduleTitle").text(msg);
                                    },
                                    success:function(data) {
                                        for (let i = 0; i < noduleCalcs.length; i++) {
                                            if (noduleCalcs[i] == $sketchNum) {
                                                noduleCalcs.splice(i, 1);
                                                break;
                                            }
                                        }
                                        if (data.sketchNum == $sketchNum) {
                                            $(".tableInfo_04_01").css('width', '0.9rem');
                                            let msg = '结节'+$sketchNum+'信息';
                                            $("#noduleTitle").text(msg);
                                            $("#table_position").val(data.position);
                                            $("#table_maxCT").val(data.maxCT.toFixed(2));
                                            $("#table_minCT").val(data.minCT.toFixed(2));
                                            $("#table_avgCT").val(data.avgCT.toFixed(2));
                                            $("#table_volume").val(data.volume.toFixed(2));
                                            $("#table_diameter").val(data.diameter.toFixed(2));
                                            toastr.success('结节'+$sketchNum+' 计算成功');
                                        }
                                    },
                                    error : function() {
                                        $(".tableInfo_04_01").css('width', '0.9rem');
                                        let msg = '结节'+$sketchNum+'出错';
                                        $("#noduleTitle").text(msg);
                                        toastr.error('结节'+$sketchNum+' 计算出错');
                                    }
                                });
                            }
                            getGrade($sequenceID,$sketchType,$sketchNum, numList);
                            var stackToolDataSource = cornerstoneTools.getToolState(targetElement, 'stack');
                            if (stackToolDataSource === undefined) {
                                return;
                            }
                            var stackData = stackToolDataSource.data[0];
                            var objectIndex = node.attributes.position - 1; //勾画位置
                            alreadySign = true;
                            if ($sketchFile != lastFile || ($sketchFile == lastFile && tempId != $sequenceID)) {
                                $(".result-shader").show();
                                $('#indicatorContainer').radialIndicator({
                                    barColor: {
                                        0: '#FF0000',
                                        33: '#FFFF00',
                                        66: '#0066FF',
                                        100: '#33CC33'
                                    },
                                    barWidth: 10,
                                    initValue: 0,
                                    roundCorner : true,
                                    percentage: true
                                });
                                let radialObj = $('#indicatorContainer').data('radialIndicator');
                                radialObj.animate(20);
                                let load1,load2;
                                $.ajax({
                                    type : "get",
                                    async:true,
                                    url : "/VSLC/sequence/getSketchPosition",
                                    dataType: "json",
                                    data : {
                                        sequenceID : $sequenceID,
                                        sketchFile : $sketchFile
                                    },
                                    beforeSend : function () {
                                        radialObj.animate(50);
                                        let base=50;
                                        load1 = setInterval(function () {
                                            base++;
                                            radialObj.animate(base);
                                            if (base==80){
                                                clearInterval(load1);
                                                load2 = setInterval(function () {
                                                    base++;
                                                    radialObj.animate(base);
                                                    if (base==90){
                                                        clearInterval(load2);
                                                    }
                                                }, 1000);
                                            }
                                        }, 300);
                                    },
                                    success : function(data){
                                        clearInterval(load1);
                                        clearInterval(load2);
                                        radialObj.animate(100);
                                        setTimeout(function () {
                                            $(".result-shader").hide();
                                            $(".result-shader canvas").remove();
                                        },500);
                                        currentResult = data;
                                        currentDraw = data[objectIndex-1];
                                        let canvas = $("#aboveCanvas")[0];
                                        var context = canvas.getContext("2d");
                                        context.clearRect(0,0,canvas.width,canvas.height);
                                        context.beginPath();
                                        context.lineWidth = 1;
                                        context.strokeStyle = "#00ff00";
                                        viewport = cornerstone.getViewport(targetElement);
                                        addX = (canvas.width-$width*viewport.scale)/2;
                                        addY =($height*viewport.scale-canvas.height)/2;
                                        currentDraw.positionList.forEach(outline => {
                                            outline.forEach((position,index) => {
                                                if(index == 0) {
                                                    context.moveTo(position.x*viewport.scale+addX,position.y*viewport.scale-addY);
                                                } else {
                                                    context.lineTo(position.x*viewport.scale+addX,position.y*viewport.scale-addY);
                                                }
                                            })
                                        });
                                        context.stroke();
                                        context.closePath();
                                        if (sketchNumResult != null && $sketchFile.indexOf("nodule") != -1){
                                            let curNumList = sketchNumResult[dcmIndex-1];
                                            for (let i = 0; i < curNumList.length; i++) {
                                                context.font = ".2rem Airal";
                                                context.fillStyle = "#ffffff";
                                                context.fillText(curNumList[i].num, curNumList[i].x*viewport.scale+addX + 7, curNumList[i].y*viewport.scale-addY);
                                            }
                                            context.stroke();
                                            context.closePath();
                                        }

                                        if (dcmIndex != objectIndex) {
                                            cornerstone.loadAndCacheImage(stackData.imageIds[objectIndex]).then(function(image) {
                                                var viewport = cornerstone.getViewport(targetElement);
                                                stackData.currentImageIdIndex = objectIndex;
                                                cornerstone.displayImage(targetElement, image, viewport);
                                            });
                                        }
                                    },

                                });
                            } else {
                                let canvas = $("#aboveCanvas")[0];
                                var context = canvas.getContext("2d");
                                context.clearRect(0,0,canvas.width,canvas.height);
                                context.beginPath();
                                context.lineWidth = 1;
                                context.strokeStyle = "#00ff00";
                                viewport = cornerstone.getViewport(targetElement);
                                addX = (canvas.width-$width*viewport.scale)/2;
                                addY =($height*viewport.scale-canvas.height)/2;
                                currentDraw.positionList.forEach(outline => {
                                    outline.forEach((position,index) => {
                                        if(index == 0) {
                                            context.moveTo(position.x*viewport.scale+addX,position.y*viewport.scale-addY);
                                        } else {
                                            context.lineTo(position.x*viewport.scale+addX,position.y*viewport.scale-addY);
                                        }
                                    })
                                });
                                context.stroke();
                                context.closePath();
                                if (sketchNumResult != null && $sketchFile.indexOf("nodule") != -1){
                                    let curNumList = sketchNumResult[dcmIndex-1];
                                    for (let i = 0; i < curNumList.length; i++) {
                                        context.font = ".2rem Airal";
                                        context.fillStyle = "#ffffff";
                                        context.fillText(curNumList[i].num, curNumList[i].x*viewport.scale+addX + 7, curNumList[i].y*viewport.scale-addY);
                                    }
                                    context.stroke();
                                    context.closePath();
                                }

                                if (dcmIndex != objectIndex) {
                                    cornerstone.loadAndCacheImage(stackData.imageIds[objectIndex]).then(function(image) {
                                        var viewport = cornerstone.getViewport(targetElement);
                                        stackData.currentImageIdIndex = objectIndex;
                                        cornerstone.displayImage(targetElement, image, viewport);
                                    });
                                }
                            }
                            tempId = $sequenceID;
                        }

                    }
                });
                var $dicDom = $("#dicomImage");
                if ($("#aboveCanvas").length == 0){
                    $dicDom.append("<canvas id='aboveCanvas' width='"+$("#dicomImage").css("width")+"px' height='"+$("#dicomImage").css("height")+"' style='position: absolute;left: 0px'></canvas>");
                }
                waterMark($(this));
                var sliceElement = $("#slice-range");
                sliceElement.show();
                sliceElement.width($("#dicomImage canvas")[0].offsetHeight);
                sliceElement.css("top",sliceElement.width()-12);
                imageLoad(imageIds1,targetElement,"imageIndexSpan",sliceElement);

                // $.ajax({
                //     url : "/VSLC/sequence/matrixExists",
                //     async : false,
                //     data : {
                //         sequenceID : $sequenceID
                //     },
                //     success : function (exist) {
                //         if (exist) {
                //             var sliceElement = $("#slice-range");
                //             var sliceElement1 = $("#slice-range1");
                //             var sliceElement2 = $("#slice-range2");
                //             sliceElement.show();
                //             sliceElement.width($("#dicomImage canvas")[0].offsetHeight);
                //             sliceElement.css("top",sliceElement.width()-12);
                //             sliceElement1.show();
                //             sliceElement1.width($("#dicomImage1 canvas")[0].offsetHeight);
                //             sliceElement1.css("top",sliceElement1.width()-12);
                //             sliceElement2.show();
                //             sliceElement2.width($("#dicomImage2 canvas")[0].offsetHeight);
                //             sliceElement2.css("top",sliceElement2.width()-12);
                //             imageLoad(imageIds1,targetElement,"imageIndexSpan",sliceElement);
                //             imageLoad(imageIds2,targetElement1,"imageIndexSpan1",sliceElement1);
                //             imageLoad(imageIds3,targetElement2,"imageIndexSpan2",sliceElement2);
                //         } else {
                //             $(".result-shader").show();
                //             $('#indicatorContainer').radialIndicator({
                //                 barColor: {
                //                     0: '#FF0000',
                //                     33: '#FFFF00',
                //                     66: '#0066FF',
                //                     100: '#33CC33'
                //                 },
                //                 barWidth: 10,
                //                 initValue: 0,
                //                 roundCorner : true,
                //                 percentage: true
                //             });
                //             let radialObj = $('#indicatorContainer').data('radialIndicator');
                //             radialObj.animate(20);
                //             let load1,load2;
                //             $.ajax({
                //                 url : "/VSLC/sequence/matrixProcess",
                //                 data : {
                //                     sequenceID : $sequenceID
                //                 },
                //                 beforeSend : function () {
                //                     radialObj.animate(34);
                //                     let base=34;
                //                     load1 = setInterval(function () {
                //                         base++;
                //                         radialObj.animate(base);
                //                         if (base==58){
                //                             clearInterval(load1);
                //                             load2 = setInterval(function () {
                //                                 base++;
                //                                 radialObj.animate(base);
                //                                 if (base==87){
                //                                     clearInterval(load2);
                //                                 }
                //                             }, 1000);
                //                         }
                //                     }, 300);
                //                 },
                //                 success : function (exist) {
                //                     clearInterval(load1);
                //                     clearInterval(load2);
                //                     radialObj.animate(100);
                //                     setTimeout(function () {
                //                         $(".result-shader").hide();
                //                         $(".result-shader canvas").remove();
                //                     },500);
                //                     var sliceElement = $("#slice-range");
                //                     var sliceElement1 = $("#slice-range1");
                //                     var sliceElement2 = $("#slice-range2");
                //                     sliceElement.show();
                //                     sliceElement.width($("#dicomImage canvas")[0].offsetHeight);
                //                     sliceElement.css("top",sliceElement.width()-12);
                //                     sliceElement1.show();
                //                     sliceElement1.width($("#dicomImage1 canvas")[0].offsetHeight);
                //                     sliceElement1.css("top",sliceElement1.width()-12);
                //                     sliceElement2.show();
                //                     sliceElement2.width($("#dicomImage2 canvas")[0].offsetHeight);
                //                     sliceElement2.css("top",sliceElement2.width()-12);
                //                     imageLoad(imageIds1,targetElement,"imageIndexSpan",sliceElement);
                //                     imageLoad(imageIds2,targetElement1,"imageIndexSpan1",sliceElement1);
                //                     imageLoad(imageIds3,targetElement2,"imageIndexSpan2",sliceElement2);
                //                 }
                //             });
                //         }
                //     }
                // });
            });
        }
    });
}

//打分
$("#reportSubmit").click(function () {
    let len = $("#suggestionList").find("tr").length - 1;
    let sketchSum = $("#reportSubmit").attr("sketchSum");
    for (let i = 1; i <= len; i++) {
        let number = suggestionList.rows[i].cells[0].innerText;
        let sketchScore = suggestionList.rows[i].cells[1].getElementsByTagName("input")[0].value;
        let reviewAdvice = suggestionList.rows[i].cells[2].getElementsByTagName("input")[0].value;
        if (sketchScore != '') {
            $.ajax({
                type: 'post',
                url: '/VSLC/reviewResult/add',
                data: {
                    sketchSum : sketchSum,
                    sketchScore : sketchScore,
                    reviewAdvice : reviewAdvice,
                    sketchFile : $sketchFile,
                    sketchType : $sketchType,
                    sketchNum : number,
                    sequenceID : $sequenceID,
                    examiner : examiner
                },
                success: function (data) {
                    toastr.success("提交成功");
                },
                error: function (data) {
                    toastr.error("提交失败");
                }
            });
        }
    }
});

//获取审核打分列表
function getGrade(sequenceID,sketchType,sketchNum,numList) {
    $.ajax({
        type: 'post',
        url: '/VSLC/reviewResult/getGrade',
        data: {
            sequenceID:sequenceID,
            sketchType:sketchType,
            sketchNum:sketchNum
        },
        success: function (data) {
            cleanGrade();
            let len = data.length;
            for (let i = 0; i < numList.length; i++) {
                let score = '';
                let advice = '';
                if (len > 0) {
                    for (let j = 0; j < len; j++) {
                        if (numList[i].sketchNum == data[j].sketchNum) {
                            score = data[j].sketchScore;
                            advice = data[j].reviewAdvice;
                            break;
                        }
                    }
                }
                $("#suggestionList").append(
                    '<tr position="'+numList[i].position+'" sketchNum="'+numList[i].sketchNum+'">'+
                    '<td style="width:12%"><a>'+ numList[i].sketchNum +'</a></td>' +
                    '<td style="width:12%"><input type="text" value='+score+'></td>' +
                    '<td style="width:76%"><input type="text" value='+advice+'></td></tr>');
            }
        }
    });
}

$("#suggestionList").on("click", "tr", function () {
    $(".grade_selected").removeClass("grade_selected");
    $(this).addClass("grade_selected");
    let position = parseInt($(this).attr('position'));
    $sketchNum = parseInt($(this).attr('sketchNum'));
    var stackToolDataSource = cornerstoneTools.getToolState(targetElement, 'stack');
    if (stackToolDataSource === undefined) return;
    var stackData = stackToolDataSource.data[0];
    var objectIndex = position;//勾画位置
    if (dcmIndex != objectIndex) {
        cornerstone.loadAndCacheImage(stackData.imageIds[objectIndex]).then(function(image) {
            var viewport = cornerstone.getViewport(targetElement);
            stackData.currentImageIdIndex = objectIndex;
            cornerstone.displayImage(targetElement, image, viewport);
        });
    }
});

function cleanGrade() {
    let len = $("#suggestionList").find("tr").length - 1;
    for (let i = len; i > 0; i--)
        $("#suggestionList").find("tr").eq(i).remove();
}

//水印
function waterMark(sequence) {
    var ltMark2 = document.getElementById('sequenceNumSpan');
    var lbMark1 = document.getElementById('thicknessSpan');
    var lbMark2 = document.getElementById('WHSpan');
    var thickness = parseFloat(sequence.attr('thickness'));
    lbMark1.textContent = '层厚 ' + thickness.toFixed(1);
    ltMark2.textContent = '序列 ' + sequence.attr('sequenceNum');
    lbMark2.textContent = '宽/高 ' + sequence.attr('zWidth') + "/" + sequence.attr('yHeight');
}

function clearMark() {
    $("#imageIndexSpan").html('');
    $("#sequenceNumSpan").html('');
    $("#avgCTSpan").html('');
    $("#thicknessSpan").html('');
    $("#WHSpan").html('');
    $("#scaleSpan").html('');
    $("#WWWLSpan").html('');
    $("#toolsState").html('');
    $("#maxCTSpan").html('');
    $("#minCTSpan").html('');
    $("#avgCTSpan").html('');
}

function clearNoduleInfo() {
    $(".tableInfo_04_01").css('width', '0.7rem');
    $("#noduleTitle").text('结节信息');
    $("#table_position").val('');
    $("#table_maxCT").val('');
    $("#table_minCT").val('');
    $("#table_avgCT").val('');
    $("#table_volume").val('');
    $("#table_diameter").val('');
}

function getImageList(sequenceID) {
    $.ajax({
        type : "get",
        async : false,
        url: "/VSLC/sequence/getImageList",
        data : {sequenceID : sequenceID},
        success: function (data) {
            imgIndexs = data;
        },
        error: function() {
            alert("error");
        }
    });
}

function getInspectionCookie(name) {
    var arr,reg = new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg)) {
        return unescape(arr[2]).split(',');
    }
    else
        return null;
}

function getInspectionCookie2() {
    //将本地cookie转换成数组形式,cookie以“;”结尾
    var cookieList = document.cookie.split("; ");
    var result = new Array();
    for (var i = 0; i < cookieList.length; i++) {
        var pos = cookieList[i].indexOf("=");
        var cname = cookieList[i].substring(0,pos);
        if (cname.indexOf("inspection") != -1) {
            var cvalue = cookieList[i].substring(pos+1);
            cvalue = decodeURIComponent(cvalue);
            result[i] = cvalue;
        }
    }
    return result;
}

/**
 * json会把Date数据类型转成时间戳，需要调用此方法解析
 * @param date 传入需要解析的json字段
 * @param key 根据key值选择转换格式
 */
function transferDate(date, key) {
    if (date == null) return '';
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

function transferIsSketch(value) {
    if (value == 0)
        return "未标注";
    else
        return "已标注";
}

//获取url参数
function GetRequest() {
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for(var i = 0; i < strs.length; i ++) {
            theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}