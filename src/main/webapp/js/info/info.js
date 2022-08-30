var timeSelected = "all";
var hospId = "all";
var loadIndex = 0; //记录加载次数，保证切换tab时能及时刷新数据表且不影响性能
var clearRightIndex = 0; //解决清空右序列表数据后出现空白的问题
var imgIndexs = null;
var patientID;
var inspectionID;
var sequenceID;
var dcmIndex = -1;
var fileNum = -1;
var isTabCheck = true;
var isTabPatient = false;
var sketchFile = null;
var sketchNum = null;
var sketchResult = null;
var sketchNumResult = null;
var switchMask = true;
var winWidth = 1600;
var winCenter = -600;
var pixelBuffer;
var userSelected;
var inspectionChecks;

$(document).ready(function () {

    toastr.options.timeOut="3000";
    toastr.options.positionClass="toast-top-right";
    toastr.options.closeButton=true;
    $("body").niceScroll();
    //时间轴滚动框初始化
    $("#box_scroll").niceScroll("#content_scroll", {
        cursorcolor:"#38f",
        // cursoropacitymax:0.7,
        // boxzoom:true,
        touchbehavior:true
    });

    //页面加载初始化
    $(window).load(function () {
        getHospital();
        tabCheckAction();
        showInitDcm();
        $("#dcm_scroll").zoomify();
        $("#examination_img").zoomify();
        $("#pathology_img").zoomify();
    });

    $('#dcm_canvas').on('click',function(){
        $("#dcm_range").focus();
    });

    $("#dcm_range").on('click',function() {
        let scale = parseInt($("#dcm_scroll").css('transform').replace(/[^0-9\-,]/g,'').split(',')[0]);
        if (isNaN(scale)||scale==1){
            $("#dcm_scroll").zoomify('zoomOut');
        }else {
            $("#dcm_scroll").zoomify('reposition');
        }
    });

    $('body').delegate('.lcs_check', 'lcs-statuschange', function() {
        switchMask = $(this).is(':checked');
        if (imgIndexs != null && sequenceID != null)
            drawSketchResult();
    });

    $("#logout").click(function () {
        $.post("/VSLC/user/logout",null, function (result){
            if (result.success)
                window.location.href="/VSLC/page/login";
        },"json");
    });

    function loadLeftDatagrid() {
        loadIndex = 0;
        patientID = null;
        var searchContent = $("#search_txt").val();
        //判断是否为逻辑表达式
        if (searchContent.indexOf("=") > -1
            || searchContent.indexOf(">") > -1
            || searchContent.indexOf("<") > -1) {

            if (isTabCheck) {
                $("#left_check_datagrid")
                    .datagrid("load", {
                        timeSearch: timeSelected,
                        hospitalID: hospId,
                        uploader : uploader,
                        drawer : drawer,
                        drawExaminer : drawExaminer,
                        signer : signer,
                        signExaminer : signExaminer,
                        logicalSearch: searchContent
                    });
            }
            if (isTabPatient) {
                $("#left_patient_datagrid")
                    .datagrid("load", {
                        logicalSearch: searchContent
                    });
            }
        } else {
            if (isTabCheck) {
                $("#left_check_datagrid")
                    .datagrid("load", {
                        timeSearch: timeSelected,
                        hospitalID: hospId,
                        uploader : uploader,
                        drawer : drawer,
                        drawExaminer : drawExaminer,
                        signer : signer,
                        signExaminer : signExaminer,
                        fuzzySearch: searchContent
                    });
            }
            if (isTabPatient) {
                $("#left_patient_datagrid")
                    .datagrid("load", {
                        fuzzySearch: searchContent
                    });
            }
        }
    }

    function clearDategrid() {
        $("#right_check_datagrid").datagrid("loadData",{total:0,rows:[]});
        $("#right_patient_datagrid").datagrid("loadData",{total:0,rows:[]});
        $("#timebar_ul").empty();
        clearRightIndex = 0;
    }

    $("#timeSearch").change(function() {
        timeSelected = $("#timeSearch").find('option:selected').attr('value');
        loadLeftDatagrid();
        clearDategrid();
    });

    $("#hospitalSearch").change(function() {
        var select = $("#hospitalSearch").find('option:selected').attr('selected', 'selected').val();
        var id = $("#hospitalSearch").find('option:selected').attr('id');
        if (select == "AllHospital") {
            hospId = "all";
            loadLeftDatagrid();
        } else {
            $.ajax({
                url : "/VSLC/hospital/find",
                type: "get",
                datatype: "json",
                success:function(data) {
                    hospId = data[id].hospitalID;
                    loadLeftDatagrid();
                },
                error : function() {
                    alert("数据提交失败");
                }
            });
        }
        clearDategrid();
    });

    function getHospital() {
        $.ajax({
            url : "/VSLC/hospital/find",
            type: "get",
            datatype: "json",
            success:function(data) {
                var index = 0;
                for(var i = 0; i < data.length; i++) {
                    $("#hospitalSearch").append("<option id='"+index+"'>"+data[i].hospitalName+"</option>");
                    $("#hospital-ul").append("<li class='hospitalId' hpId='"+data[i].hospitalID+"'>"+data[i].hospitalName+"</li>");
                    index++;
                }
            },
            error : function() {
                alert("数据提交失败");
            }
        });
    }

    $("#winSelect").change(function() {
        winWidth = $("#winSelect").find('option:selected').attr('winWidth');
        winCenter = $("#winSelect").find('option:selected').attr('winCenter');
        if (imgIndexs != null && sequenceID != null) drawSketchResult();
    });

    /**
     * 切换tab检查所需操作
     */
    function tabCheckAction() {
        isTabCheck = true;
        isTabPatient = false;
        //隐藏病人视图
        $("#left_patient_box").hide();
        $("#right_patient_box").hide();
        //显示检查视图
        $("#left_check_box").show();
        $("#right_check_box").show();
        //修改按钮样式
        $("#tab_check").css({"background-color":"#38f", "color":"#fff"});
        $("#tab_patient").css({"background-color":"#fff", "color":"#000"});
    }

    /**
     * 切换tab病人所需操作
     */
    function tabPatientAction() {
        isTabPatient = true;
        isTabCheck = false;
        //隐藏检查视图
        $("#left_check_box").hide();
        $("#right_check_box").hide();
        //$("#dcm_scroll").hide();
        //显示病人视图
        $("#left_patient_box").show();
        $("#right_patient_box").show();
        //修改旁边按钮样式
        $("#tab_check").css({"background-color":"#fff", "color":"#000"});
        $("#tab_patient").css({"background-color":"#38f", "color":"#fff"});
    }

    /**
     * 输入病人信息搜索数据
     */
    $("#search_txt").bind('keypress', function (event) {
        if(event.keyCode == "13") {
            loadLeftDatagrid();
            clearDategrid();
            showInitDcm();
        }
    });

    $("#search_btn").click(function () {
        loadLeftDatagrid();
        clearDategrid();
        showInitDcm();
    });

    $("#importBtn").on("click",function () {
        uploadTrigger();
    });

    function uploadTrigger() {
        $("#file_input").trigger("click");
    }

    $("#file_input").on("change",function () {
        var num = this.files.length;
        $("#file_num").text(num);
    });
    
    $("#hospital-ul").on("click",".hospitalId",function () {
        $("#chooseHospital").text($(this).text());
        $("#chooseHospital").attr("hpId",$(this).attr("hpId"));
    });

    $("#upload-open").click(function () {
       $(".upload-info-old").html("");
       $(".upload-info-new").html("");
       $("#indicatorContainer").html("");
       $("#upload-box1").show();
       $("#upload-box2").hide();
       $("#newsqsLength").text(0);
    });

    var dropZone = document.getElementById('importBtn');
    function handleDragOver(evt) {
        evt.stopPropagation();
        evt.preventDefault();
        evt.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
    }
    function handleFileSelect(evt) {
        evt.stopPropagation();
        evt.preventDefault();
        var files = [],
            items = evt.dataTransfer.items;
        function folderRead(entry){
            entry.createReader().readEntries(function (entries) {
                for(var i = 0; i < entries.length; i++) {
                    var entry = entries[i];
                    if(entry.isFile) {
                        entry.file(function(file) {
                            show(file);
                        })
                    } else {
                        folderRead(entry);
                    }
                }
            });
        }
        function show(f){
            files.push(f);
            formData.append("fileFolder",f);
        }

        for(var i = 0; i < items.length; i++) {
            var entry = items[i].webkitGetAsEntry();
            if(!entry) {
                return;
            }
            if(entry.isFile) {
                entry.file(function(file) {
                    show(file);
                })
            } else {
                folderRead(entry);
            }
            // if (i==items.length-1) {
            //     console.log(formData.get("fileFolder"));
            // }
        }
    }
    var dragData = new FormData();
    dropZone.addEventListener('dragover', handleDragOver, false);
    dropZone.addEventListener('drop', handleFileSelect, false);
    $("#file_confirm").click(function() {
        var formData = new FormData($("#file_upload")[0]);
        formData.append("hospitalID",$("#chooseHospital").attr("hpId"));
        if (formData.get("fileFolder").name=="") {
            toastr.info("请选择文件");
        } else {
            $("#indicator").show();
            $('#indicatorContainer').radialIndicator({
                barColor: '#87CEEB',
                barWidth: 10,
                initValue: 0,
                roundCorner : true,
                percentage: true
            });
            let radialObj = $('#indicatorContainer').data('radialIndicator');
            var loadingText = null;
            $.ajax({
                type : "post",
                url: "/VSLC/function/upload",
                data: formData,
                cache: false,
                async:true,
                contentType: false,
                processData: false,
                xhr: function () {
                    var xhr = new window.XMLHttpRequest();
                    //Upload progress
                    xhr.upload.addEventListener("progress", function (e) {
                        if (e.lengthComputable) {
                            var percentComplete = (e.loaded || e.position) * 100 / e.total;
                            //Do something with upload progress
                            radialObj.animate(percentComplete);
                            if (percentComplete==100){
                                $("#tipForUpload").text("上传成功,等待后台处理");
                                function uploading() {
                                    let tipText = $("#tipForUpload").text();
                                    if (tipText.length<=14) {
                                        if (tipText.length==14) {
                                            $("#tipForUpload").text("上传成功,等待后台处理");
                                        } else {
                                            $("#tipForUpload").text(tipText+'.');
                                        }
                                    }
                                }
                                loadingText = setInterval(uploading,500);

                            }
                        }
                    }, false);

                    return xhr;
                },
                beforeSend: function () {
                    // 禁用按钮防止重复提交，发送前响应
                    // $(".shader").show();
                    $("#file_confirm").attr('disabled',true);
                },
                success: function (data) {
                    $('#file_upload')[0].reset();
                    $("#file_num").text(0);
                    $("#file_confirm").attr('disabled',false);
                    $("#tipForUpload").text("文件上传中");
                    $('#indicatorContainer').data('radialIndicator').animate(0);

                    $("#upload-box1").hide();
                    $("#upload-box2").show();
                    $(".upload-info-new").append("<h4>"+data.inspection+"</h4>");
                    let hospitalID = data.hospital.hospitalID;
                    if (hospitalID==null) $("#hospitalSpan").show();
                    let sqsCount = 0, maskCount = 0;
                    let uploadPath = data.uploadPath;
                    let inspectionID = data.inspectionID;
                    data.sequence.forEach((sequence)=>{
                        sqsCount++;
                        let isSketch;
                        if (sequence.isSketch == 0) {
                            isSketch="未标注";
                            $(".upload-info-new").append("<p style='float: left; width: 5.3rem'>"+"<input type='checkbox' name='newSqs' value='"+sequence.sequenceNum+"' checked='checked'/>"+sequence.sequenceName+"_序列号 "+sequence.sequenceNum+"_文件数 "+sequence.fileNum+"<span style='float: right'>"+isSketch+"</span></p>");
                        } else {
                            maskCount++;
                            isSketch="已标注";
                            $(".upload-info-new").append("<p style='float: left; width: 5.3rem'>"+"<input type='checkbox' name='newSqs' value='"+sequence.sequenceNum+"' checked='checked'/>"+sequence.sequenceName+"_序列号 "+sequence.sequenceNum+"_文件数 "+sequence.fileNum+"<span style='float: right'>"+"<input type='checkbox' name='maskCheck' value='"+sequence.sequenceNum+"' checked='checked'/>"+isSketch+"</span></p>");
                        }
                    });
                    $("#newsqsLength").text(sqsCount);
                    $("#maskLength").text(maskCount);
                    let requestinID = data.sequence[0].inspection.inspectionID;
                    $.ajax({
                        type : "get",
                        url : '/VSLC/sequence/getSequenceList',
                        data : "inspectionID="+requestinID,
                        success : function (oldData) {
                            $(".upload-info-old").append("<h4>"+data.inspection+"</h4>");
                            oldData.forEach((sequence)=> {
                                let isSketch;
                                if (sequence.isSketch == 0) {
                                    isSketch="未标注";
                                } else {
                                    isSketch="已标注";
                                }
                                $(".upload-info-old").append("<p style='float: left; width: 5.3rem'>"+sequence.sequenceName+"_序列号 "+sequence.sequenceNum+"_文件数 "+sequence.fileNum+"<span style='float: right'>"+isSketch+"</span></p>");
                            })
                        }
                    });
                    $(".upload-info-new input[name='newSqs']").click(function () {
                        if($(this).is(':checked')){
                            if ($(this))
                            $("#newsqsLength").text(parseInt($("#newsqsLength").text())+1);
                        }else {
                            $("#newsqsLength").text(parseInt($("#newsqsLength").text())-1);
                        }
                    });
                    $(".upload-info-new input[name='maskCheck']").click(function () {
                        if($(this).is(':checked')){
                            if ($(this))
                                $("#maskLength").text(parseInt($("#maskLength").text())+1);
                        }else {
                            $("#maskLength").text(parseInt($("#maskLength").text())-1);
                        }
                    });
                    $("#uploadSequence").click(function () {
                        if (hospitalID == null) hospitalID = $("#chooseHospital").attr("hpId");
                        let selections = new Array();
                        let sequenceNode = {
                            "uploadType" : "",
                            "sequenceNum" : ""
                        };
                        let iSet = new Array();
                        let mSet = new Array();
                        $.each($(".upload-info-new input[name='newSqs']:checkbox:checked"),function(){
                            iSet.push($(this).val());
                        });
                        $.each($(".upload-info-new input[name='maskCheck']:checkbox:checked"),function(){
                            mSet.push($(this).val());
                        });
                        data.sequence.forEach((sequence)=>{
                            sequence.sequenceNum
                            let iFlag = iSet.indexOf(sequence.sequenceNum)>=0?true:false;
                            let mFlag = mSet.indexOf(sequence.sequenceNum)>=0?true:false;
                            if (iFlag){
                                sequenceNode.uploadType = "i";
                                if (mFlag){
                                    sequenceNode.uploadType += "m";
                                }
                                sequenceNode.sequenceNum = sequence.sequenceNum;
                                selections.push(sequenceNode);
                            }else {
                                if (mFlag){
                                    sequenceNode.uploadType = "m";
                                    sequenceNode.sequenceNum = sequence.sequenceNum;
                                    selections.push(sequenceNode);
                                }
                            }
                        });
                        $('.shader').show();
                        $.ajax({
                            type : "post",
                            url : "/VSLC/function/import",
                            contentType : "application/json",
                            data : JSON.stringify({
                                "uploadPath" : uploadPath,
                                "inspectionID" : inspectionID,
                                "hospitalID" : hospitalID,
                                "selections" : selections
                            }),
                            success : function () {
                                $('.shader').hide();
                                toastr.success("导入成功");
                            }
                        })
                    })

                },
                complete: function () {//完成响应
                    // $(".shader").hide();
                    $("#indicator").hide();
                    window.clearInterval(loadingText);
                },
                error: function(data) {
                    if (data == 'hospitalError')
                        toastr.error("我们无法识别改检查的医院信息，请选择医院");
                    else
                        toastr.error("数据导入失败");
                }
            });
        }
    });

    $("#tab_check").click(function () {
        tabCheckAction();
        //不重新加载无法显示数据，多次加载影响性能
        if (loadIndex < 1)
            $("#left_check_datagrid").datagrid("reload");
        if (clearRightIndex < 1)
            $("#right_check_datagrid").datagrid("loadData",{total:0,rows:[]});
        loadIndex++;
        clearRightIndex++;
    });

    $("#tab_patient").click(function () {
        tabPatientAction();
        //不重新加载无法显示数据，多次加载影响性能
        if (loadIndex < 1)
            $("#left_patient_datagrid").datagrid("reload");
        if (clearRightIndex < 1)
            $("#right_patient_datagrid").datagrid("loadData",{total:0,rows:[]});
        loadIndex++;
        clearRightIndex++;
    });

    /**
     * 检查 --> 左表
     */
    $("#left_check_datagrid").datagrid({
        url : "/VSLC/inspection/search",
        queryParams:{
            timeSearch : timeSelected,
            hospitalID : hospId,
            uploader : uploader,
            drawer : drawer,
            drawExaminer : drawExaminer,
            signer : signer,
            signExaminer : signExaminer
        },
        method:'post',
        singleSelect: true,
        fit: true,
        fitColumns: false,
        pagination: true,
        pageSize: 30,
        checkOnSelect: false,
        selectOnCheck: false,
        onDblClickCell: onDblClickCell,
        onAfterEdit: onAfterEdit,
        columns:[[
            {field:'left__check_checkbox',checkbox:true},
            {field:'editor',title:'是否修改',width:56,align:'center',
                formatter: function (value, row, index) {
                    if (row.editor) {
                        return row.editor.realName;
                    } else {
                        return "";
                    }
                }},
            {field:'remark',title:'备注',width:80,align:'center',
                editor: {
                    type:'textbox'
                }},
            {field:'processID',title:'工作流程状态',width:80,align:'center',
                formatter: function (value, row, index) {
                    if (value)
                        return transferProcessID(value);
                },
                editor: {
                    type:'combobox',
                    options: {
                        valueField: 'id',
                        textField: 'text',
                        method:'get',
                        url:'/VSLC/json/process.json',
                        panelHeight: '115',
                        required:false
                    }
                }},
            {field:'imageMethod',title:'工作流程说明',width:80,align:'center',
                formatter: function (value, row, index) {
                    if (value)
                        return transferImageMethod(value);
                },
                editor: {
                    type:'combobox',
                    options: {
                        valueField: 'id',
                        textField: 'imageMethod',
                        method:'get',
                        url:'/VSLC/json/imageMethod.json',
                        panelHeight: '70',
                        required:false
                    }
                }},
            {field:'englishName',title:'病人姓名',width:120,align:'center',
                formatter: function (value, row, index) {
                    if (row.patient)
                        return row.patient.englishName;
                    else
                        return value;
                }},
            {field:'birday',title:'出生年月',width:80,align:'center',
                formatter: function (value, row, index) {
                    if (row.patient){
                        return transferDate(row.patient.birday, 0);
                    } else {
                        return value;
                    }
                }},
            {field:'isAbdomen',title:'腹部平扫',width:56,align:'center',
                formatter: function(value) {
                    return transferIsEver(value);
                },
                editor: {
                    type:'checkbox',
                    options: {
                        on:'1',off:'0'
                    }
                }},
            {field:'isAbdomenCE',title:'腹部增强',width:56,align:'center',
                formatter: function(value) {
                    return transferIsEver(value);
                },
                editor: {
                    type:'checkbox',
                    options:{
                        on:'1',off:'0'
                    }
                }},
            {field:'pnsize',title:'病灶大小',width:56,align:'center',
                formatter: function(value) {
                    if (value == 0)
                        return "";
                    else
                        return value;
                },
                editor: {
                    type: 'numberbox',
                    options: {
                        precision:2
                    }
                }},
            {field:'pnnum',title:'病灶数量',width:56,align:'center',editor:'numberbox',
                formatter: function(value) {
                    if (value == 0)
                        return "";
                    else
                        return value;
                }},
            {field:'pnsign',title:'病灶表现',width:56,align:'center',
                formatter: function(value) {
                    return transferPNSign(value);
                },
                editor: {
                    type:'combobox',
                    options: {
                        valueField: 'id',
                        textField: 'text',
                        method: 'get',
                        url:'/VSLC/json/PNSign.json',
                        panelHeight: '70',
                        required: false
                    }
                }},
            {field:'thickness', title:'是否薄层',width:56,align:'center',
                formatter: function (value) {
                    return transferThickness(value);
                },
                editor: {
                    type: 'numberbox',
                    options: {
                        precision:2
                    }
                }},
            {field:'ctnumber', title:'CT号',width:80,align:'center'},
            {field:'patientID', title:'病人ID',width:80,align:'center',
                formatter: function (value, row, index) {
                    if (row.patient)
                        return row.patient.patientID;
                }},
            {field:'modeName',title:'检查设备',width:56,align:'center',
                formatter: function (value, row, index) {
                    if (row.mode)
                        return row.mode.modeName;
                    else
                        return value;
                }},
            {field:'diseaseName',title:'病种',width:65,align:'center',
                formatter: function(value, row, index) {
                    if (row.diseaseName != undefined)
                        return row.diseaseName;
                    else if (row.disease)
                        return row.disease.diseaseName;
                    else return value;
                },
                editor: {
                    type:'combobox',
                    options: {
                        valueField: 'diseaseName',
                        textField: 'diseaseName',
                        method: 'get',
                        url: '/VSLC/disease/find',
                        panelHeight: '115',
                        required: false
                    }
                }},
            {field:'inspectTime',title:'检查日期和时间',width:130,align:'center',
                formatter: function (value) {
                    return transferDate(value, 1);
                }},
            {field:'hospitalName', title:'医院', width:190,align:'center',
                formatter: function(value, row, index) {
                    if (row.hospital)
                        return row.hospital.hospitalName;
                }},
            {field:'savePath', title:'存储路径', width:750,align:'center'}
        ]],
        onClickRow: function () {
            var row = $("#left_check_datagrid").datagrid('getSelected');
            if(row) {
                if (inspectionID != row.inspectionID) {
                    showInitDcm();
                    dcmIndex = -1;
                    fileNum = -1;
                    imgIndexs = null;
                    sequenceID = null;
                    sketchFile = null;
                }
                inspectionID = row.inspectionID;
            }
            $("#right_check_datagrid")
                .datagrid("load", "/VSLC/sequence/getSequenceList?inspectionID=" + inspectionID + "&winLung=true");
        },
        onBeginEdit: function (index, row) {
            var lcEditor = $('#left_check_datagrid').datagrid('getEditor', {index:index, field:'diseaseName'});
            if (lcEditor != null) {
                $(lcEditor.target).combobox({
                    onLoadSuccess: function () {
                        if (row.diseaseName != undefined)
                            $(lcEditor.target).combobox('setValue', row.diseaseName);
                        else if (row.disease)
                            $(lcEditor.target).combobox('setValue', row.disease.diseaseName);
                    }
                });
            }
        }
    });

    var pager = $("#left_check_datagrid").datagrid('getPager');
    pager.pagination({
        buttons:[{
            iconCls:'icon-cut',
            text:'删除',
            handler: function(){
                var checks = new Array();
                var rows = $("#left_check_datagrid").datagrid('getChecked');
                if (rows.length > 0 && confirm('已选择'+rows.length+'条数据，是否删除')) {
                    for (var i = 0; i < rows.length; i++) {
                        checks.push(rows[i].inspectionID);
                    }
                    $.ajax({
                        type : "post",
                        traditional: true,
                        url : "/VSLC/inspection/delete",
                        data : {
                            checks : checks
                        },
                        success : function() {
                            $('#left_check_datagrid').datagrid("reload");
                            clearDategrid();
                        },
                        error : function() {
                            alert("failed");
                        }
                    });
                } else if (rows.length == 0) {
                    alert("您未选择任何任务");
                }
            }
        },{
            iconCls:'icon-print',
            text:'导出',
            handler: function(){
                // $(".shader").show();
                var rows = $("#left_check_datagrid").datagrid('getChecked');
                var checks = new Array();
                let sequences = new Array();
                $(".download-info-check").html("");
                $(".download-info-sequence").html("");
                $("#download_modal").modal("toggle");
                for (var i = 0; i < rows.length; i++) {
                    checks.push(rows[i].inspectionID);
                    $(".download-info-check").append("<p inspId='"+rows[i].inspectionID+"'>"+rows[i].patient.englishName+"_"+rows[i].ctnumber+"_"+rows[i].patient.patientID+"</p>");
                    $.ajax({
                        type : "get",
                        url : "/VSLC/sequence/getSequenceList?inspectionID="+rows[i].inspectionID,
                        success : function (data) {
                            for (let i = 0; i < data.length; i++) {
                                sequences.push(data[i].sequenceID);
                            }
                            $("#sqsLength").text(sequences.length);
                        }
                    })
                }
                $(".download-info-check p").on("click",function () {
                    $(".download-info-sequence").html("");
                    $(".download-info-check p").css("color","#8e8e8e");
                    $(this).css("color","#91cdff");
                    hoverFlag=0;
                    let inspId = $(this).attr("inspId");
                    $.ajax({
                        type : "get",
                        url : "/VSLC/sequence/getSequenceList?inspectionID="+inspId,
                        success : function (data) {
                            for (let i = 0; i < data.length; i++) {
                                let isAlive = false;
                                let isSketch;
                                if (sequences.length!=0){
                                    if(sequences.indexOf(data[i].sequenceID)>=0) {
                                        isAlive = true;
                                    }
                                }
                                if (data[i].isSketch==0){
                                    isSketch="未标注";
                                }else{
                                    isSketch="已标注";
                                }
                                if (isAlive){
                                    $(".download-info-sequence").append("<p style='float: left; width: 5.5rem'>"+"<input type='checkbox' name='sqs' value='"+data[i].sequenceID+"' checked='checked'/>"+data[i].sequenceName+"<span style='float: right'>"+isSketch+"</span></p>")
                                }else {
                                    $(".download-info-sequence").append("<p style='float: left; width: 5.5rem'>"+"<input type='checkbox' name='sqs' value='"+data[i].sequenceID+"'/>"+data[i].sequenceName+"<span style='float: right'>"+isSketch+"</span></p>")
                                }
                            }
                            $(".download-info-sequence input").on("click",function () {
                                if($(this).is(':checked')){
                                    sequences.push(parseInt($(this).val()));
                                }else {
                                    sequences.splice(sequences.indexOf(parseInt($(this).val())),1);
                                }
                                $("#sqsLength").text(sequences.length);
                                // $('.download-info-sequence input:checkbox:checked').each(function() {
                                //     sequences.push($(this).val());
                                // });
                            })
                        }
                    })
                });
                let hoverFlag = 1;
                $(".download-info-check p").hover(function () {
                    if (hoverFlag==1){
                        $(".download-info-check p").css("color","#8e8e8e");
                        $(this).css("color","#91cdff");
                    }
                });
                $("#downloadSequence").off("click").on("click",function(){
                    $("#downloadSequence").click = null;
                    if (sequences.length == 0) {
                        toastr.info("请选择序列");
                    } else {
                        let url ="";
                        sequences.forEach((sequence,index)=>{
                            url += "sequences=" + sequence;
                            if (index < sequences.length - 1)
                                url += "&";
                        });
                        $('.shader').show();
                        $.ajax({
                            type : "post",
                            url : "/VSLC/function/export?"+url,
                            data : sequences,
                            success : function (data) {
                                $('.shader').hide();
                                toastr.success("导出成功");
                            }
                        });
                    }
                });

                // $('<iframe src="'+url+'" id="frame1" style="display: none"></iframe>').prependTo('body');

                // var elemIF = document.createElement("iframe");
                // elemIF.id = "elemIF";
                // elemIF.innerHTML="<p>"+123+"</p>";
                // elemIF.src = url;
                // elemIF.style.display = "none";
                // document.body.appendChild(elemIF);
                // console.log(elemIF);
                // console.log(document.getElementById("elemIF"));
                // $.ajax({
                //     type: 'HEAD', // 获取头信息，type=HEAD即可
                //     url : url,
                //     complete: function(xhr,data){
                //         // 获取相关Http Response header
                //         var wpoInfo = {
                //             // 服务器端时间
                //             "date" : xhr.getResponseHeader('Date'),
                //             // 如果开启了gzip，会返回这个东西
                //             "contentEncoding" : xhr.getResponseHeader('Content-Encoding'),
                //             // keep-alive ？ close？
                //             "connection" : xhr.getResponseHeader('Connection'),
                //             // 响应长度
                //             "contentLength" : xhr.getResponseHeader('Content-Length'),
                //             // 服务器类型，apache？lighttpd？
                //             "server" : xhr.getResponseHeader('Server'),
                //             "vary" : xhr.getResponseHeader('Vary'),
                //             "transferEncoding" : xhr.getResponseHeader('Transfer-Encoding'),
                //             // text/html ? text/xml?
                //             "contentType" : xhr.getResponseHeader('Content-Type'),
                //             "cacheControl" : xhr.getResponseHeader('Cache-Control'),
                //             // 生命周期？
                //             "exprires" : xhr.getResponseHeader('Exprires'),
                //             "lastModified" : xhr.getResponseHeader('Last-Modified'),
                //             "fileName" : xhr.getResponseHeader("fileName")
                //         };
                //         console.log(wpoInfo);
                //         // 在这里，做想做的事。。。
                //         $(".shader").hide();
                //     }
                // });
            }
        },{
            iconCls:'icon-tip',
            text:'任务',
            handler: function() {
                inspectionChecks = new Array();
                var rows = $("#left_check_datagrid").datagrid('getChecked');
                if (rows.length > 0) {
                    for (var i = 0; i < rows.length; i++) {
                        inspectionChecks.push(rows[i].inspectionID);
                    }
                }
                if (permission == 1) {
                    $("#distribute_modal").modal("toggle");
                    $("#checksNum").html("已选择" + rows.length);
                    getDistributeUsers();
                } else {
                    if (confirm('确定完成任务吗？')) {
                        $.ajax({
                            type: 'post',
                            url: '/VSLC/inspection/taskFinish',
                            contentType: 'application/json',
                            data: JSON.stringify({
                                permission : permission,
                                inspectionChecks : inspectionChecks
                            }),
                            success: function () {
                                $("#left_check_datagrid").datagrid('reload');
                                toastr.success("任务完成");
                            }
                        });
                    }
                }
            }
        },{
            iconCls:'icon-search',
            text:'审核',
            handler: function() {
                let inspections = new Array();
                let rows = $("#left_check_datagrid").datagrid('getRows');
                clearInspectionCookie();
                for (let i = 0; i < rows.length; i++) {
                    let temp = rows[i].inspectionID;
                    inspections.push(temp);
                    //如果是当前选择的检查
                    if (temp == inspectionID) {
                        inspections[i] = inspections[0];
                        inspections[0] = temp;
                    }
                }
                document.cookie = 'inspections=' + inspections.toString();
                window.location.href = '/VSLC/page/review';
            }
        }]
    });

    $("#permissionGroupSelect").change(function() {
        getDistributeUsers();
    });

    function getDistributeUsers() {
        let searchContent = $("#userSearch").val();
        let permissionGroupID = $("#permissionGroupSelect").find('option:selected').attr('value');
        $.ajax({
            url: '/VSLC/user/find',
            data: {
                permissionGroupID:permissionGroupID,
                fuzzySearch : searchContent
            },
            success: function (data) {
                cleanUsers();
                for (let i = 0; i < data.length; i++) {
                    $("#distributeUsers").append(
                        '<tr userID="' + data[i].userID +'">' +
                        '<td>'+data[i].realName+'</td>' +
                        '<td>'+data[i].permissionGroup.permissionGroupName+'</td></tr>');
                }
            }
        });
    }

    $("#distributeUsers").on("click", "tr", function () {
        if ($(this).attr('userID') != undefined) {
            userSelected = $(this).attr('userID');
            $(".user_selected").removeClass("user_selected");
            $(this).addClass("user_selected");
        }
    });

    $("#distributeBtn").click(function () {
        if (inspectionChecks.length > 0) {
            $.ajax({
                type : "post",
                url: '/VSLC/inspection/taskDistribute',
                contentType: "application/json",
                data: JSON.stringify({
                    userSelected : parseInt(userSelected),
                    inspectionChecks : inspectionChecks
                }),
                success: function () {
                    $("#distribute_modal").modal("toggle");
                    toastr.success("分配成功");
                }
            });
        } else if (inspectionChecks.length == 0) {
            toastr.error("您未选择任何数据");
        }
    });

    $("#userSearch").bind('keypress', function (event) {
        if(event.keyCode == "13") {
            getDistributeUsers();
        }
    });

    $("#userSearch_btn").click(function () {
        getDistributeUsers();
    });

    $.extend($.fn.datagrid.methods, {
        editCell: function(jq,param){
            return jq.each(function(){
                var opts = $(this).datagrid('options');
                var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
                for(var i=0; i<fields.length; i++){
                    var col = $(this).datagrid('getColumnOption', fields[i]);
                    col.editor1 = col.editor;
                    if (fields[i] != param.field){
                        col.editor = null;
                    }
                }
                $(this).datagrid('beginEdit', param.index);
                for(var i=0; i<fields.length; i++){
                    var col = $(this).datagrid('getColumnOption', fields[i]);
                    col.editor = col.editor1;
                }
            });
        }
    });

    var editIndex = undefined;
    function endEditing() { //该方法用于关闭上一个焦点的editing状态
        if (editIndex == undefined)
            return true;
        if ($('#left_check_datagrid').datagrid('validateRow', editIndex)) {
            $('#left_check_datagrid').datagrid('endEdit', editIndex);
            editIndex = undefined;
            return true;
        } else {
            return false;
        }
    }

    function onDblClickCell(index, field, value) {
        var rows = $(this).datagrid('getRows');
        if (endEditing()) {
            if (rows.length == 1) {
                $('#left_check_datagrid').datagrid('selectRow', index)
                    .datagrid('editCell', {index:index,field:field});
            } else {
                $(this).datagrid('beginEdit', index);
                var ed = $(this).datagrid('getRows', {index:index, field:field});
                $(ed.target).focus();
            }
            editIndex = index;
        }
    }

    //单元格失去焦点执行的方法
    function onAfterEdit(index, row, changes) {
        submitForm(index, row);
    }

    //提交数据
    function submitForm(index, row) {
        var inspectionID = row.inspectionID; //主键
        if(inspectionID == ""){
            $("#left_check_datagrid").datagrid('reload');
            return;
        }
        var remark = row.remark;
        var processID = row.processID;
        var imageMethod = row.imageMethod;
        var diseaseName = null;
        if (row.diseaseName != undefined && row.diseaseName != "")
            diseaseName = row.diseaseName;
        else if (row.disease)
            diseaseName = row.disease.diseaseName;
        var isAbdomen = row.isAbdomen;
        var isAbdomenCE = row.isAbdomenCE;
        var PNSize = row.pnsize;
        var PNNum = row.pnnum;
        var PNSign = row.pnsign;
        var thickness = row.thickness;
        $.ajax({
            type : "post",
            url : "/VSLC/inspection/update",
            data : {
                inspectionID : inspectionID,
                processID : processID,
                imageMethod : imageMethod,
                diseaseName : diseaseName,
                isAbdomen : isAbdomen,
                isAbdomenCE : isAbdomenCE,
                PNSize : PNSize,
                PNNum : PNNum,
                PNSign : PNSign,
                thickness : thickness,
                editor : editor,
                remark : remark
            },
            success : function(data) {
            },
            error : function(data) {
                alert("failed");
            }
        });
    }

    /**
     * 检查 --> 右表
     */
    $("#right_check_datagrid").datagrid({
        method:"get",
        singleSelect:"true",
        columns:[[
            {field:'sequenceName',title:'序列说明',width:170,align:'center'},
            {field:'isSketch',title:'勾画',width:50,align:'center',
                formatter: function (value) {
                    return transferIsSketch(value);
                }},
            {field:'thickness',title:'层厚',width:65,align:'center'},
            {field:'insepectTime',title:'序列日期和时间',width:130,align:'center',
                formatter: function (value, row, index) {
                    if (row.inspection)
                        return transferDate(row.inspection.inspectTime, 1);
                    else
                        return transferDate(value, 1);
                }}
        ]],
        onClickRow: function () {
            var row = $("#right_check_datagrid").datagrid('getSelected');
            if(row) {
                sketchFile = null;
                sequenceID = row.sequenceID;
                fileNum = row.fileNum;
                $("#dcm_range").hide();
                getImageList(sequenceID, true);
                var canvas = document.getElementById("dcm_canvas");
                canvas.width = row.width;
                canvas.height = row.height;
            }
        }
    });

    $('#right_check_datagrid').datagrid({
        view: detailview,
        detailFormatter:function(){
            return '<div id="sketchBox" style="width: 100%;height: 100px;overflow: auto">' +
                '<ul style="width:120px;" id="sketchTree" class="easyui-tree"></ul>' +
                '</div>';
        },
        onExpandRow: function(index,row){
            var sketchBox = $(this).datagrid('getRowDetail',index).find('#sketchBox');
            var sketchTree = $(this).datagrid('getRowDetail',index).find('#sketchTree');
            sketchBox.panel({
                border:false,
                cache:false
            });
            sketchTree.tree({
                url:'/VSLC/sequence/getSketchList?sequenceID=' + row.sequenceID,
                lines:true,
                loadFilter: function(data){
                    if (data.fileTree) {
                        if (data.numberList != null)
                            sketchNumResult = data.numberList;
                        else
                            sketchNumResult = null;
                        return data.fileTree;
                    } else return [];
                },
                onBeforeLoad: function () {
                    sequenceID = row.sequenceID;
                    fileNum = row.fileNum;
                    getImageList(row.sequenceID, false);
                    $(".shader").show();
                },
                onLoadSuccess: function (node, data) {
                    showInitDcm();
                    $(".shader").hide();
                    sketchBox.niceScroll({
                        cursorcolor:"#38f",
                        touchbehavior:true
                    });
                },
                onClick: function(node) {
                    var canvas = document.getElementById("dcm_canvas");
                    canvas.width = row.width;
                    canvas.height = row.height;
                    let tempId = sequenceID;
                    let tempFile = sketchFile;
                    sketchFile = node.attributes.sketchFile;
                    sketchNum = node.attributes.sketchNum;
                    dcmIndex = node.attributes.position - 1;
                    if (tempFile != sketchFile || tempId != sequenceID) {
                        $(".result-shader").show();
                        $('#indicatorContainer1').radialIndicator({
                            barColor: '#87CEEB',
                            barWidth: 10,
                            initValue: 0,
                            roundCorner : true,
                            percentage: true
                        });
                        let radialObj = $('#indicatorContainer1').data('radialIndicator');
                        radialObj.animate(20);
                        let load1,load2;
                        $.ajax({
                            type : "get",
                            url : "/VSLC/sequence/getSketchPosition",
                            dataType: "json",
                            data : {
                                sequenceID : sequenceID,
                                sketchFile : sketchFile
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
                                sketchResult = data;
                                drawSketchResult();
                            }
                        });
                    } else {
                        drawSketchResult();
                    }
                    $("#dcm_range").show();
                    $("#dcm_range").focus();
                    $("#dcm_range").attr("max",fileNum-1);
                    $("#dcm_range").val(dcmIndex);
                }
            });
            $('#right_check_datagrid').datagrid('fixDetailRowHeight',index);
        }
    });

    /**
     * 如果delta的值是-1，即向下滚动，+1则向上滚动
     */
    $("#dcm_scroll").mousewheel(function(event, delta) {
        if (dcmIndex!=-1){
            if(delta < 0) {
                if(imgIndexs[dcmIndex] < imgIndexs[imgIndexs.length-1]) {
                    dcmIndex++;
                    $("#dcm_range").val(dcmIndex);
                    drawSketchResult();
                }
            } else if(delta > 0) {
                if(imgIndexs[dcmIndex] > imgIndexs[0]) {
                    dcmIndex--;
                    $("#dcm_range").val(dcmIndex);
                    drawSketchResult();
                }
            }
            return false; //div滚动时页面不滚动
        }
    });

    $("#dcm_range").on("input",selectIndex);

    function selectIndex(event){
        // Get the range input value
        var newImageIdIndex = parseInt(event.currentTarget.value, 10);
        dcmIndex = newImageIdIndex;
        drawSketchResult();
    }

    /**
     * 病人 --> 左表
     */
    $("#left_patient_datagrid").datagrid({
        url: "/VSLC/patient/search",
        method:'post',
        singleSelect: true,
        fit: true,
        fitColumns: false,
        pagination: true,
        pageSize: 30,
        columns:[[
            {field:'englishName',title:'姓名',width:120,align:'center'},
            {field:'chineseName',title:'中文名',width:50,align:'center'},
            {field:'patientSex',title:'性别',width:40,align:'center',
                formatter: function (value, row, index) {
                    return transferSex(value);
                }},
            {field:'idnumber',title:'身份证号',width:150,align:'center'},
            {field:'birday',title:'出生年月',width:80,align:'center',
                formatter: function (value, row, index) {
                    return transferDate(value, 0);
                }},
            {field:'patientID',title:'病人ID',width:80,align:'center'},
            {field:'admissionNum',title:'住院号',width:70,align:'center'},
            {field:'nativePlace',title:'籍贯',width:80,align:'center'},
            {field:'contacts',title:'联系人',width:50,align:'center'},
            {field:'tel',title:'联系电话',width:120,align:'center'},
            {field:'admissionDate',title:'入院日期',width:80,align:'center',
                formatter: function (value, row, index) {
                    return transferDate(value, 0);
                }},
            {field:'dischargeDate',title:'出院日期',width:80,align:'center',
                formatter: function (value, row, index) {
                    return transferDate(value, 0);
                }},
            {field:'surgeon',title:'术者',width:50,align:'center'},
            {field:'dischargeDiagnosis',title:'出院诊断',width:150,align:'center'},
            {field:'operationDate',title:'手术日期',width:80,align:'center',
                formatter: function (value, row, index) {
                    return transferDate(value, 0);
                }},
            {field:'operationName',title:'手术名称',width:500,align:'center'}
        ]],
        onClickRow: function () {
            var row = $("#left_patient_datagrid").datagrid('getSelected');
            if(row && patientID != row.patientID) {
                $("#timebar_ul").empty();
                $("#examination_box").hide();
                $("#pathology_box").hide();
                $("#right_sequence_box").show();
                $("#right_patient_datagrid").datagrid("loadData",{total:0,rows:[]});
                patientID = row.patientID;
                $.ajax({
                    url : "/VSLC/inspection/findByPatientID?patientID="+patientID,
                    type: "get",
                    datatype: "json",
                    success:function(data) {
                        var timeIndex = 0;
                        for(var i = 0; i < data.rows.length; i++) {
                            var date = data.rows[i].inspectTime;
                            var method = data.rows[i].imageMethod;
                            $("#timebar_ul").append("<li><a class='timelist' liIndex='"+timeIndex+"'>"
                                + transferDate(date, 2)
                                +" "+transferImageMethod(method) +"</a></li>");
                            timeIndex++;
                            $(".timelist").click(function() {
                                $("#examination_box").hide();
                                $("#pathology_box").hide();
                                $("#right_sequence_box").show();
                                liIndex = $(this).attr("liIndex");
                                var inId = data.rows[liIndex].inspectionID;
                                $("#right_patient_datagrid").datagrid("load",
                                    "/VSLC/sequence/getSequenceList?inspectionID=" + inId);
                            });
                        }
                        initTimeBox();
                        $("#box_scroll").getNiceScroll().resize();
                    },
                    error : function() {
                        alert("数据提交失败");
                    }
                });
                examinationList(patientID);
                pathologyList(patientID);
            }
        }
    });

    /**
     * 病人 --> 右表
     */
    $("#right_patient_datagrid").datagrid({
        method:"get",
        singleSelect:"true",
        columns:[[
            {field:'sequenceName',title:'序列说明',width:170,align:'center'},
            {field:'thickness',title:'层厚',width:65,align:'center',
                formatter: function (value){
                    return transferThickness(value);
                }},
            {field:'inspectTime',title:'序列日期和时间',width:130,align:'center',
                formatter: function (value, row, index) {
                    if (row.inspection)
                        return transferDate(row.inspection.inspectTime, 1);
                    else
                        return transferDate(value, 1);
                }}
        ]],
        onClickRow: function () {

        }
    });
});

function examinationList(patientID) {
    $.ajax({
        url : "/VSLC/examination/findByPatientID?patientID=" + patientID,
        type: "post",
        datatype: "json",
        success:function(data) {
            for (var i = 0; i < data.length; i++) {
                for (var j = 1; j <= 2; j++) {
                    $("#timebar_ul").append("<li><a class='examination' examinationID='"+data[i].examinationID+"' format='"+j+"'>" + "检验报告单" + j +"</a></li>");
                }
            }
            $(".examination").click(function () {
                $("#right_sequence_box").hide();
                $("#examination_box").show();
                $("#pathology_box").hide();
                $("#examination_img").attr("src", "/VSLC/examination/getReport?examinationID=" + $(this).attr("examinationID") + "&format=" + $(this).attr("format"));
            });
            initTimeBox();
            $("#box_scroll").getNiceScroll().resize();
        },
        error : function() {
            alert("数据提交失败");
        }
    });
}

function pathologyList(patientID) {
    $.ajax({
        url : "/VSLC/pathology/findByPatientID?patientID=" + patientID,
        type: "post",
        datatype: "json",
        success:function(data) {
            for (var i = 0; i < data.length; i++) {
                $("#timebar_ul").append("<li><a class='pathology' admissionNum='"+data[i]+"'>病理报告单</a></li>");
            }
            $(".pathology").click(function () {
                $("#right_sequence_box").hide();
                $("#examination_box").hide();
                $("#pathology_box").show();
                $("#pathology_img").attr("src", "/VSLC/pathology/getReport?admissionNum=" + $(this).attr("admissionNum"));
            });
            initTimeBox();
            $("#box_scroll").getNiceScroll().resize();
        },
        error : function() {
            alert("数据提交失败");
        }
    });
}

function drawSketchResult() {
    var canvas = document.getElementById("dcm_canvas");
    var context = canvas.getContext("2d");
    var img = new Image();
    img.src = "/VSLC/function/displayDcm?dcmIndex=" + imgIndexs[dcmIndex]
        + "&matrixIndex=" + dcmIndex
        + "&sequenceID=" + sequenceID
        + "&winWidth=" + winWidth
        + "&winCenter=" + winCenter;
    img.onload = function() {
        context.drawImage(img, 0, 0);
        context.font = "25px Airal";
        context.fillStyle = "#ffffff";
        context.fillText("Image: " + imgIndexs[dcmIndex] + "/" + imgIndexs.length, canvas.width-200, canvas.height-20);
        if (sketchFile != null && switchMask) {
            let curDraw = sketchResult[dcmIndex];
            context.beginPath();
            context.lineWidth = 1;
            context.strokeStyle = "#00ff00";
            let startX,startY,endX,endY;
            curDraw.positionList.forEach(outline => {
                outline.forEach((position,index) => {
                    if(index == 0) {
                        context.moveTo(position.x,position.y);
                        startX = position.x;
                        startY = position.y;
                    } else {
                        context.lineTo(position.x,position.y);
                        endX = position.x;
                        endY = position.y;
                    }
                })
            });
            context.lineTo(startX, startY);
            context.stroke();
            context.closePath();
            if (sketchNumResult != null && sketchFile.indexOf("nodule") != -1) {
                let curNumList = sketchNumResult[dcmIndex];
                for (let i = 0; i < curNumList.length; i++) {
                    context.font = ".2rem Airal";
                    context.fillStyle = "#ffffff";
                    context.fillText(curNumList[i].num, curNumList[i].x + 7, curNumList[i].y);
                }
                context.stroke();
                context.closePath();
            }
        }
    };
}

/**
 * dicom占位图
 */
function showInitDcm() {
    var canvas = document.getElementById("dcm_canvas");
    var context = canvas.getContext("2d");
    canvas.width = 512;
    canvas.height = 512;
    var img = new Image();
    img.src = "../img/bg/init_dcm.png";
    img.onload = function(){context.drawImage(img,0,0)};
    $("#dcm_range").hide();
}

/**
 * 时间轴初始化
 */
function initTimeBox() {
    //时间轴连接线
    var h = $(".about4_main ul li:first-child").height()/2;//第一个li高度的一半
    var h1 = $(".about4_main ul li:last-child").height()/2;//最后一个li高度的一半
    $(".line").css("top",h);
    $(".line").height($(".about4_main").height()-h1-h);
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

    if (key == 0)
        return dateFormat;
    else if (key == 1)
        return TimeFormat;
    else if (key == 2)
        return dateFormat2;
}

function transferSex(sex) {
    if (sex == 0)
        return "女";
    else if (sex == 1)
        return "男";
    else
        return "";
}

function transferProcessID(id) {
    if (id == 1)
        return "未标注";
    else if (id == 2)
        return "已标注";
    else if (id == 3)
        return "勾画已审核";
    else if (id == 4)
        return "征象已标注";
    else if (id == 5)
        return "征象已审核";
}

function transferImageMethod(id) {
    if (id == 1)
        return "平扫";
    else if (id == 2)
        return "增强";
    else if (id == 3)
        return "肺动脉CTA";
}

function transferThickness(thickness) {
    if (thickness == "" || thickness == null)
        return "";
    else if(thickness == 0)
        return "";
    else if (thickness > 3)
        return "否";
    else
        return "是";
}

function transferIsEver(isEver) {
    if (isEver == 0) return "否";
    else if (isEver == 1) return "是";
    else return "";
}

function transferPNSign(PNSign) {
    if (PNSign == 1)
        return "磨玻璃";
    else if (PNSign == 2)
        return "混合";
    else if (PNSign == 3)
        return "实性";
    else
        return "";
}

function transferIsSketch(isSketch) {
    if (isSketch == 0)
        return "否";
    else
        return "是";
}

function getImageList(sequenceID, display) {
    $.ajax({
        type : "get",
        url: "/VSLC/sequence/getImageList",
        data : {sequenceID : sequenceID},
        success: function (data) {
            imgIndexs = data;
            dcmIndex = parseInt(imgIndexs.length/2);
            if (display) {
                drawSketchResult();
                $("#dcm_range").show();
                $("#dcm_range").focus();
                $("#dcm_range").attr("max",fileNum-1);
                $("#dcm_range").val(dcmIndex);
            }
        },
        error: function() {
            alert("error");
        }
    });
}

function clearInspectionCookie() {
    var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
    if(keys) {
        for (var i = keys.length; i--;) {
            if (keys[i] == "inspections")
                document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString();
        }
    }
}

function cleanUsers() {
    let len = $("#distributeUsers").find("tr").length - 1;
    for (let i = len; i > 0; i--)
        $("#distributeUsers").find("tr").eq(i).remove();
}

/**
 * 解析base64
 * @param base64PixelData
 */
function getPixelData(base64PixelData) {
    var pixelDataAsString = window.atob(base64PixelData);
    var pixelData = str2ab(pixelDataAsString);
    return pixelData;
}
function str2ab(str) {
    var buf = new ArrayBuffer(str.length*2); // 2 bytes for each char
    var bufView = new Uint16Array(buf);
    var index = 0;
    for (var i=0, strLen=str.length; i<strLen; i+=2) {
        var lower = str.charCodeAt(i);
        var upper = str.charCodeAt(i+1);
        bufView[index] = lower + (upper <<8);
        index++;
    }
    return bufView;
}

function parseImage2() {
    var canvas = document.getElementById("dcm_canvas");
    var context = canvas.getContext("2d");
    $.ajax({
        type: 'post',
        url: '/VSLC/sequence/getMatrix',
        data: {
            sequenceID : sequenceID,
            type : 0,
            val : (dcmIndex + 1)
        },
        success: function (data) {
            pixelBuffer = getPixelData(data.imgMatrix);
            let width = canvas.width;
            let height = canvas.height;
            var lookupObject = new LookupTable();
            lookupObject.setData(winCenter,winWidth,data.imgBitsStored,data.imgSlope,data.imgIntercept);
            lookupObject.calculateHULookup();
            lookupObject.calculateLookup();

            var imageData = context.getImageData(0,0,width,height);
            var index = 0;
            for(var y = 0; y< height; y++) {
                for(var x = 0; x < width; x++) {
                    var offset = (y*width + x) * 4;
                    var pixelValue = lookupObject.lookup[pixelBuffer[index]];
                    imageData.data[offset] = pixelValue;
                    imageData.data[offset+1] = pixelValue;
                    imageData.data[offset+2] = pixelValue;
                    imageData.data[offset+3] = 255;
                    index++;
                }
            }
            context.putImageData(imageData, 0,0);
            context.font = "25px Airal";
            context.fillStyle = "#ffffff";
            context.fillText("Image: " + imgIndexs[dcmIndex] + "/" + imgIndexs.length, width-200, height-20);
            if (sketchFile != null && switchMask) {
                let curDraw = sketchResult[dcmIndex];
                context.beginPath();
                context.lineWidth = 1;
                context.strokeStyle = "#00ff00";
                let startX,startY,endX,endY;
                curDraw.positionList.forEach(outline => {
                    outline.forEach((position,index) => {
                        if(index == 0) {
                            context.moveTo(position.x,position.y);
                            startX = position.x;
                            startY = position.y;
                        } else {
                            context.lineTo(position.x,position.y);
                            endX = position.x;
                            endY = position.y;
                        }
                    })
                });
                context.lineTo(startX, startY);
                context.stroke();
                context.closePath();
                if (sketchNumResult != null && sketchFile.indexOf("nodule") != -1) {
                    let curNumList = sketchNumResult[dcmIndex];
                    for (let i = 0; i < curNumList.length; i++) {
                        context.font = ".2rem Airal";
                        context.fillStyle = "#ffffff";
                        context.fillText(curNumList[i].num, curNumList[i].x + 7, curNumList[i].y);
                    }
                    context.stroke();
                    context.closePath();
                }
            }
        }
    });
}

var logicalTip = [
    {"value":"", "category":"工作流程状态"},
    {"value":"", "category":"工作流程说明"},
    {"value":"", "category":"病人姓名"},
    {"value":"", "category":"出生年月"},
    {"value":"", "category":"腹部平扫"},
    {"value":"", "category":"腹部增强"},
    {"value":"", "category":"病灶大小"},
    {"value":"", "category":"病灶数量"},
    {"value":"", "category":"病灶表现"},
    {"value":"", "category":"是否薄层"},
    {"value":"", "category":"CT号"},
    {"value":"", "category":"病人ID"},
    {"value":"", "category":"检查设备"},
    {"value":"", "category":"病种"},
    {"value":"", "category":"检查日期和时间"},
    {"value":"", "category":"医院"},
    {"value":"磨玻璃", "category":"病灶表现"},
    {"value":"混合", "category":"病灶表现"},
    {"value":"实性", "category":"病灶表现"},
    {"value":"未标注", "category":"工作流程状态"},
    {"value":"已标注", "category":"工作流程状态"},
    {"value":"勾画已审核", "category":"工作流程状态"},
    {"value":"征象已标注", "category":"工作流程状态"},
    {"value":"征象已审核", "category":"工作流程状态"},
    {"value":"平扫", "category":"工作流程说明"},
    {"value":"增强", "category":"工作流程说明"},
    {"value":"肺动脉CTA", "category":"工作流程说明"}
];

/**
 * 像素查找表，主要要先根据rescaleSlope和rescaleIntercept进行Hounsfield值的转换
 * HU[i] = pixel_val[i]*rescaleSlope+ rescaleIntercept
 */
function LookupTable() {
    this.bitsStored;
    this.rescaleSlope;
    this.rescaleIntercept;
    this.windowCenter;
    this.windowWidth;
    this.huLookup;
    this.lookup;
}

LookupTable.prototype.setData=function(wc,ww,bs,rs,ri) {
    this.windowCenter=wc;
    this.windowWidth=ww;
    this.bitsStored=bs;
    this.rescaleSlope=rs;
    this.rescaleIntercept=ri;
};

LookupTable.prototype.setWindowingdata=function(wc,ww) {
    this.windowCenter=wc;
    this.windowWidth=ww;
};

LookupTable.prototype.calculateHULookup=function() {
    var size=1<<this.bitsStored;
    this.huLookup = new Array(size);
    for(var inputValue=0;inputValue<size;inputValue++) {
        if(this.rescaleSlope == undefined && this.rescaleIntercept == undefined) {
            this.huLookup[inputValue] = inputValue;
        } else {
            this.huLookup[inputValue] = inputValue * this.rescaleSlope +this.rescaleIntercept;
        }
    }
 };

/**
 * 窗宽窗位的调整线性的Window-leveling算法
 * 非线性的gamma算法,稍微修改下：
 * var y=255.0 * Math.pow(this.huLookup[inputValue]/this.windowWidth, 1.0/gamma);
 */
LookupTable.prototype.calculateLookup=function() {
    var size=1<<this.bitsStored;
    var min=this.windowCenter-0.5-(this.windowWidth-1)/2;
    var max=this.windowCenter-0.5+(this.windowWidth-1)/2;
    this.lookup=new Array(size);
    for(var inputValue=0;inputValue<size;inputValue++) {
        if(this.huLookup[inputValue]<=min) {
            this.lookup[inputValue]=0 ;
        } else if (this.huLookup[inputValue]>max) {
            this.lookup[inputValue]=255;
        } else {
            var y=((this.huLookup[inputValue]-(this.windowCenter-0.5))/(this.windowWidth-1)+0.5)*255;
            this.lookup[inputValue]= parseInt(y);
        }
    }
 };