var targetElement = document.getElementById('dicomImage');
var currentDraw = null;
var addX = null;
var addY = null;
var imgWidth = null;
var imgHeight = null;
var canvasImgChange = null;
var $sequenceID;
var $sketchFile;
var $sketchType;
var $sketchNum;

$(document).ready(function () {

    //获取检查信息
    $.ajax({
        type: 'post',
        url: '/VSLC/inspection/getInspectionInfo.action',
        data: {"inspectionID":GetRequest().inspectionID},
        success: function (data) {
            $(".picInfo_01_00").html(data.patientName);
            $(".picInfo_01_01").html(transferDate(data.inspectionTime, 1));
        }
    });

    var imageIds1 = [];
    //获取序列列表
    $.ajax({
        type: 'post',
        url: '/VSLC/sequence/getSequenceList.action',
        data: {"inspectionID":GetRequest().inspectionID},
        success: function (data) {
            var $html = '';
            $(".picInfo_01_02").html("CT/SR : " + data.length + " series");
            for(i = 0; i < data.length; i++){
                $html += '<div class="picInfo_02_01" zWidth='+ data[i].width +' yHeight='+ data[i].height +' fileNum='+ data[i].fileNum +' sequenceID='+ data[i].sequenceID +' thickness='+ data[i].thickness +' sequenceNum='+ data[i].sequenceNum +'>';
                $html += '<div class="picInfo_02_01_01">'+ transferIsSketch(data[i].isSketch) + "  " + data[i].sequenceName +'</div>';
                $html += '<div class="picInfo_02_01_02"><div class="picInfo_02_01_02_01" style="background:url(http://localhost:8080/VSLC/function/displayDcm.action?sequenceID=' + data[i].sequenceID + ') no-repeat 100% 100%;background-size:cover"></div></div></div>';
                $html += '<div style="display: none;overflow: auto" class="picInfo_02_01" expand=' + data[i].sequenceID + data[i].isSketch + '><ul id="sketchTree'+data[i].sequenceID+'" class="easyui-tree"></ul></div>';
            }
            $(".picInfo_02").html($html);
            cornerstone.enable(targetElement);
            // 选择序列
            $(".picInfo_02_01").click(function() {
                var $filenum = $(this).attr("fileNum");
                var $height = $(this).attr("yHeight");
                var $width = $(this).attr("zWidth");
                $(".picZone_02_01").attr("fileNum",$filenum);
                $(".picZone_02_02").attr("height",$height);
                $(".picZone_02_03").attr("width",$width);
                $(".picInfo_02_01").removeClass("listChoose");
                $(this).addClass("listChoose");
                $sequenceID = $(this).attr("sequenceID");
                $('[expand]').hide();
                $('[expand=' + $sequenceID + 1 + ']').show();
                imageIds1 = [];
                for(var i = 1; i < (Number($filenum)+1); i++) {
                    imageIds1.push("example://"+$sequenceID+"_x_" + i);
                }
                $('#sketchTree'+$sequenceID).tree({
                    url:'/VSLC/reviewResult/getSketchList.action?sequenceID=' + $sequenceID,
                    lines:true,
                    onClick: function(node) {
                        if (node.attributes != null) {

                        }
                    }
                });
                imageLoad(imageIds1,targetElement,$sequenceID,'0');
                var slice = $("#slice-range");
                slice.show();
                slice.width($("canvas")[0].offsetHeight);
                slice.css("top",slice.width()-12);
                waterMark($(this));
            });
        }
    });
});

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

/**
 * json会把Date数据类型转成时间戳，需要调用此方法解析
 * @param date 传入需要解析的json字段
 * @param key 根据key值选择转换格式
 */
function transferDate(date, key) {
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
    if (value == 1) return "已勾画";
    else return "未勾画";
}