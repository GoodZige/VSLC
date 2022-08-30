var dcmIndex;//当前页数索引 例如 "图像 4/356"中的4 从1开始
var sliceFlag = true;
var allPositionsInfo = [];//保存勾画结果的数据格式 也是渲染勾画结果的数据
var sumx = 0, sumy = 0;//移动画布时的x,y偏移量

function loadImg(imageIds,targetElement,sliceText,sliceElement) {

    function onNewImage(e) {
        var eventData = e.detail;
        var newImageIdIndex = stack.currentImageIdIndex; //当前图像索引 从0开始

        var currentValueSpan = document.getElementById(sliceText);
        currentValueSpan.textContent = "图像 " + (newImageIdIndex + 1) + "/" + imageIds.length;
        dcmIndex = newImageIdIndex + 1;
        range.val(newImageIdIndex);//更新滚动条值
        var blue = (dcmIndex / imageIds.length) * 100;//滚动条样式变化
        range.css('backgroundSize', blue + '% 100%');//滚动条样式变化
        if (alreadySign) {//渲染方式之一 如果已显示标注结果，采用如下渲染方式
            currentDraw = currentResult[dcmIndex - 1];//获取当前页标注结果
            positionsArray = currentDraw.positionList;//将当前标注结果坐标集赋值给该数组
            // let positionsInfo = {//定义数据格式
            //     index : dcmIndex-1,
            //     positionsArray : currentDraw.positionList
            // };
            // let have = false;
            // allPositionsInfo.forEach((positionsInfo,infoIndex)=>{
            //    if (dcmIndex-1==positionsInfo.index){
            //        have = true;
            //    }
            // });
            // if (!have&&currentDraw.positionList.length!=0){
            //     allPositionsInfo.push(positionsInfo);
            // }
            if (!showCircle1){//如果没有点击调整按钮 进行不调整标注结果的渲染
                drawResult(0, 0);
            }
        }
        if (allPositionsInfo.length!=0){//当进行调整或勾画操作时
            let canvas = $("#aboveCanvas")[0];
            let $canvas = $("#aboveCanvas");
            let context = canvas.getContext("2d");
            let viewport = cornerstone.getViewport(targetElement);
            let scale = viewport.scale;
            let moveX = (canvas.width - imgWidth * scale) / 2;
            let moveY = (imgHeight * scale - canvas.height) / 2;
            context.clearRect(-5000, -5000, 9999, 9999);
            let hpFlag = false;
            for (let i=0;i<allPositionsInfo.length;i++){
                if (dcmIndex-1==allPositionsInfo[i].index){
                    hpFlag = true;
                    apiIndex = i;
                    positionsArray = allPositionsInfo[i].positionsArray;//通过遍历找到当前坐标集
                    drawResult(0,0);//渲染当前坐标集
                    // allPositionsInfo[i].positionsArray.forEach((positions,positionsIndex)=>{
                    //     context.beginPath();
                    //     context.strokeStyle = "#00ff00";
                    //     context.lineWidth = scale;
                    //     console.log(scale);
                    //     positions.forEach((p,pi)=>{
                    //         if (pi==0){
                    //             context.moveTo(p.x * scale + moveX,p.y * scale - moveY);
                    //         }else {
                    //             context.lineTo(p.x * scale + moveX,p.y * scale - moveY);
                    //         }
                    //     });
                    //     context.stroke();
                    //     context.closePath();
                    // });
                    break;
                }
            }
            if (hpFlag){//当当前页存在勾画结果时，将操作改为推拉操作
                canvas.onmousedown = null;
                canvas.onmouseup = null;
                if (showCircle)
                    canvas.onmousemove = pushMove;
                else if (showCircle1)
                    canvas.onmousemove = pushMove1;
            }else {//否则改为第一笔勾画操作
                canvas.onmouseup = null;
                canvas.onmousemove = null;
                firstDrawFlag = true;
                if (isDraw)
                    canvas.onmousedown = firstDraw1;
                else
                    canvas.onmousedown = firstDraw;
            }
        }else {
            let canvas = $("#aboveCanvas")[0];
            if (showCircle){
                canvas.onmouseup = null;
                canvas.onmousemove = null;
                firstDrawFlag = true;
                if (isDraw)
                    canvas.onmousedown = firstDraw1;
                else
                    canvas.onmousedown = firstDraw;
            }
        }
    }

    function onImageRendered(e) {
        var viewport = cornerstone.getViewport(e.target);
        var wwwlSpan = document.getElementById('WWWLSpan');
        var scaleSpan = document.getElementById('scaleSpan');
        scaleSpan.textContent = '缩放 ' + viewport.scale.toFixed(4);
        wwwlSpan.textContent = "窗宽/窗位 " + Math.round(viewport.voi.windowWidth)
            + '/' + Math.round(viewport.voi.windowCenter);
    };

    targetElement.removeEventListener('cornerstonenewimage', onNewImage);
    targetElement.removeEventListener('cornerstoneimagerendered', onImageRendered);
    targetElement.addEventListener('cornerstonenewimage', onNewImage);
    targetElement.addEventListener('cornerstoneimagerendered', onImageRendered);
    sliceElement.off("input").on("input", selectImage);

    function selectImage(event) {//对滚动条进行操作时运行的函数，input值改变一次就运行一次

        // Get the range input value
        var newImageIdIndex = parseInt(event.currentTarget.value, 10);

        // Get the stack data
        var stackToolDataSource = cornerstoneTools.getToolState(targetElement, 'stack');
        if (stackToolDataSource === undefined) {
            return;
        }
        var stackData = stackToolDataSource.data[0];

        // Switch images, if necessary
        if (newImageIdIndex !== stackData.currentImageIdIndex && stackData.imageIds[newImageIdIndex] !== undefined) {
            cornerstone.loadAndCacheImage(stackData.imageIds[newImageIdIndex]).then(function (image) {
                var viewport = cornerstone.getViewport(targetElement);
                stackData.currentImageIdIndex = newImageIdIndex;
                cornerstone.displayImage(targetElement, image, viewport);
            });
        }
    }

    var stack = {
        currentImageIdIndex: 0,
        imageIds: imageIds
    };

    var range = sliceElement;
    range.attr('min', 0);
    range.attr('step', 1);
    range.attr('max', imageIds.length - 1);
    range.val(stack.currentImageIdIndex);
    // range.attr('value', stack.currentImageIdIndex);

    // Enable the dicomImage element and the mouse inputs
    cornerstoneTools.mouseInput.enable(targetElement);
    cornerstoneTools.mouseWheelInput.enable(targetElement);
    //console.log(imageIds[0]);

    cornerstone.loadImage(imageIds[0]).then(function (image) {
        // Display the image
        cornerstone.displayImage(targetElement, image);
        document.getElementById('abdomenWin').addEventListener('click', function () {
            let viewport = cornerstone.getViewport(targetElement);
            viewport.voi.windowWidth = 400;
            viewport.voi.windowCenter = 60;
            cornerstone.setViewport(targetElement, viewport);
        });
        document.getElementById('boneWin').addEventListener('click', function () {
            let viewport = cornerstone.getViewport(targetElement);
            viewport.voi.windowWidth = 2000;
            viewport.voi.windowCenter = 300;
            cornerstone.setViewport(targetElement, viewport);
        });
        document.getElementById('lungWin').addEventListener('click', function () {
            let viewport = cornerstone.getViewport(targetElement);
            viewport.voi.windowWidth = 1600;
            viewport.voi.windowCenter = -600;
            cornerstone.setViewport(targetElement, viewport);
        });
        // Set the stack as tool state
        cornerstoneTools.addStackStateManager(targetElement, ['stack', 'playClip']);
        cornerstoneTools.addToolState(targetElement, 'stack', stack);

        cornerstoneTools.stackScrollWheel.activate(targetElement);
        cornerstoneTools.stackScrollKeyboard.activate(targetElement);
    });
}

function moveCanvas(e) {//移动画布
    let canvas = $("#aboveCanvas")[0];
    if (e.button == 0) {
        document.getElementById('slice-range').focus();
        let lastX = e.clientX;//记录上一点坐标
        let lastY = e.clientY;
        console.log("移动画布");
        document.onmousemove = function (e) {
            let deltaX = e.clientX - lastX,//坐标改变量 注意：此时单位为真实像素 即相对于电脑分辨率的像素
                deltaY = e.clientY - lastY;
            lastX = e.clientX;//更新上一点坐标
            lastY = e.clientY;
            setTimeout(function () {//一定要进行延时操作 否则viewport未来得及被更新
                let viewport = cornerstone.getViewport(targetElement);
                sumx += deltaX / viewport.scale;//相对于dicom图像的偏移量
                sumy += deltaY / viewport.scale;
                drawResult(deltaX, deltaY);//对画布进行平移操作并加载 注意：是对画布的坐标轴进行平移
            }, 10);

            // viewport.translation.x += (deltaX / viewport.scale);
            // viewport.translation.y += (deltaY / viewport.scale);
            // // viewport.translation.x += deltaX ;
            // viewportX = viewport.translation.x;
            //
            // // viewport.translation.y += deltaY ;
            // viewportY = viewport.translation.y;
            //
            // cornerstone.setViewport(element, viewport);
        };
        document.onmouseup = function () {
            document.onmousemove = null;
            canvas.onmouseup = null;
        };
    }
}
let oneDown = true;//使onkeydown方法只执行一次，否则长按会执行多次方法
document.onkeydown = function (event) {
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if (sliceFlag) {//如果图像已加载
        var slice = document.getElementById('slice-range');
        if (e && e.keyCode == 40 || e && e.keyCode == 37) {//上,左
            sliceFlag = false;
            slice.focus();
            slice.value = parseInt(slice.value) + 1;
        }
        if (e && e.keyCode == 38 || e && e.keyCode == 39) {//下,右
            sliceFlag = false;
            slice.focus();
        }
    }
    if (e && e.keyCode == 32) {//空格
        activate("enablePlay");
        autoDisplay();
    }
    if (e.ctrlKey && $("#toolsState").text() != "移动") {
        if (oneDown){
            cornerstoneTools.stackScrollWheel.deactivate(targetElement);//禁用多余操作
            cornerstoneTools.zoomWheel.activate(targetElement);
            cornerstoneTools.pan.activate(targetElement, 1);
            cornerstoneTools.wwwc.activate(targetElement, 2);
            let canvas = $("#aboveCanvas")[0];
            drawResult(0,0);
            console.log("crtl");
            canvas.onmousedown = null;
            canvas.onmousemove = null;
            canvas.onmousewheel = null;
            canvas.onmousedown = moveCanvas;
            canvas.onmousewheel = function (e) {
                let ee = e || window.event;
                let lastViewport = cornerstone.getViewport(targetElement);//记录缩放前viewport
                if (lastViewport.scale < 8) {//当scale小于8时 放大倍数可以自定义
                    setTimeout(function () {
                        let viewport = cornerstone.getViewport(targetElement);//记录缩放后viewport
                        let translateX = sumx * (viewport.scale - lastViewport.scale);//根据缩放前和缩放后的缩放倍数的差值乘上dicom偏移量得到真实偏移量
                        let translateY = sumy * (viewport.scale - lastViewport.scale);
                        drawResult(translateX, translateY);
                    }, 1);
                } else {//禁用滚动或还原缩放倍数
                    cornerstoneTools.zoomWheel.deactivate(targetElement);
                    if (ee.wheelDelta < 0) {
                        var tempScale = lastViewport.scale;
                        setTimeout(function () {
                            let viewport = cornerstone.getViewport(targetElement);
                            viewport.scale = 7;
                            cornerstone.setViewport(targetElement, viewport);
                            let translateX = sumx * (viewport.scale - tempScale);
                            let translateY = sumy * (viewport.scale - tempScale);
                            drawResult(translateX, translateY);
                            cornerstoneTools.zoomWheel.activate(targetElement);
                        }, 1);
                    }
                }
            };
            oneDown = false;
        }
    }
    if ((showCircle||showCircle1)&&e.altKey) {//只有进行调整或者勾画操作时才可对推拉半径进行调整
        console.log("alt");
        e.returnValue=false;
        cornerstoneTools.stackScrollWheel.deactivate(targetElement);
        cornerstoneTools.zoomWheel.deactivate(targetElement);
        let canvas = $("#aboveCanvas")[0];
        let context = canvas.getContext('2d');
        canvas.onmousewheel = function (e) {
            let ee = e || window.event;
            if (ee.wheelDelta > 0) {//限制r在1-10之间
                if (r<10){
                    r+=ee.wheelDelta/120;
                }
            }else {
                if (r>1){
                    r+=ee.wheelDelta/120;
                }
            }
            let scale = cornerstone.getViewport(targetElement).scale;
            if (r>=2){//画圆标
                context.clearRect(-5000, -5000, 9999, 9999);
                drawResult(0,0);
                context.beginPath();
                context.lineWidth = 2;
                context.strokeStyle = "#F5270B";
                context.arc(ox2, oy2, r * scale, 0, 2 * Math.PI);
                context.stroke();
                context.closePath();
            }else {//画十字光标
                context.clearRect(-5000, -5000, 9999, 9999);
                drawResult(0,0);
                context.beginPath();
                context.lineWidth = 1;
                context.strokeStyle = "#F5270B";
                context.moveTo(ox2-3*scale,oy2);
                context.lineTo(ox2+3*scale,oy2);
                context.stroke();
                context.closePath();
                context.beginPath();
                context.lineWidth = 1;
                context.strokeStyle = "#F5270B";
                context.moveTo(ox2,oy2-3*scale);
                context.lineTo(ox2,oy2+3*scale);
                context.stroke();
                context.closePath();
            }
        }
    }
    if ((showCircle||showCircle1)&&e.keyCode == 9) {//按tab进行切换操作 由推拉操作切换成勾画操作
        console.log("tab down");
        e.returnValue = false;
        let canvas = $("#aboveCanvas")[0];
        let context = canvas.getContext('2d');
        context.clearRect(-5000, -5000, 9999, 9999);
        drawResult(0,0);
        canvas.onmousemove = null;
        firstDrawFlag = true;
        if (showCircle)
            canvas.onmousedown = firstDraw;
        else if (showCircle1)
            canvas.onmousedown = firstDraw1;
        showCircle = false;
        showCircle1 = false;
    }
};
document.onkeyup = function (event) {
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if (e.keyCode == 17) {// 松开crtl键时
        if (!oneDown){
            // cornerstoneTools.zoomWheel.deactivate(targetElement);
            // cornerstoneTools.stackScrollWheel.activate(targetElement);
            if ($("#toolsState").text() != "移动") {
                cornerstoneTools.pan.deactivate(targetElement);
                cornerstoneTools.wwwc.deactivate(targetElement);
                cornerstoneTools.zoomWheel.deactivate(targetElement);
                cornerstoneTools.stackScrollWheel.activate(targetElement);
                let canvas = $("#aboveCanvas")[0];
                canvas.onmousedown = null;
                canvas.onmousewheel = null;
                if (showCircle){
                    if (r>=2){
                        canvas.onmousemove = pushMove;
                    }else {
                        canvas.onmousemove = dragMove;
                    }
                }else if (showCircle1){
                    if (r>=2){
                        canvas.onmousemove = pushMove1;
                    }else {
                        canvas.onmousemove = dragMove1;
                    }
                }else if (measureFlag){
                    canvas.onmousedown = measureDown;
                    canvas.onmouseup = measureUp
                }else if(firstDrawFlag) {
                    if (isDraw){
                        canvas.onmousedown = firstDraw1;
                    }else {
                        canvas.onmousedown = firstDraw;
                    }
                }
            }
            oneDown = true;
        }
    }
    if ((showCircle||showCircle1)&&e.keyCode == 18) {
        console.log("alt up");
        cornerstoneTools.stackScrollWheel.activate(targetElement);
        let canvas = $("#aboveCanvas")[0];
        if (r>=2){
            if (showCircle)
                canvas.onmousemove = pushMove;
            else if (showCircle1)
                canvas.onmousemove = pushMove1;
        }else {
            if (showCircle)
                canvas.onmousemove = dragMove;
            else if (showCircle1)
                canvas.onmousemove = dragMove1;
        }
        canvas.onmousewheel = null;
    };
    if (e.keyCode == 46){
        let canvas = $("#aboveCanvas")[0];
        for (let i=0;i<allPositionsInfo.length;i++){
            if (dcmIndex-1==allPositionsInfo[i].index) {
                console.log(currentDraw);
                if (currentDraw){//调整状态
                    console.log(123);
                    allPositionsInfo[i].positionsArray = [];
                    currentDraw.positionList = [];
                    if (edits.indexOf(allPositionsInfo[i].index)!=-1){
                        edits.splice(edits.indexOf(allPositionsInfo[i].index),1)
                    }
                }else {//勾画状态
                    allPositionsInfo.splice(i,1);
                }
                canvas.onmouseup = null;
                canvas.onmousemove = null;
                firstDrawFlag = true;
                if (isDraw)
                    canvas.onmousedown = firstDraw1;
                else
                    canvas.onmousedown = firstDraw;
            }
        }
        drawResult(0,0);
    }
};
document.getElementById('enablePan').addEventListener('click', function () {
    activate('enablePan');
    clearCornerstoneTools();
    cornerstoneTools.pan.activate(targetElement, 1);
    cornerstoneTools.zoomWheel.activate(targetElement);
    cornerstoneTools.wwwc.activate(targetElement, 2);
    cornerstoneTools.stackScrollWheel.deactivate(targetElement);
    $("#toolsState").text('移动');

    let canvas = $("#aboveCanvas")[0];
    let context = canvas.getContext("2d");

    /*加载画布*/
    drawResult(0, 0);
    /*重置鼠标事件*/
    canvas.onmousedown = null;
    canvas.onmousemove = null;
    document.onmouseup = null;
    /*移动画布*/
    canvas.onmousedown = moveCanvas;
    canvas.onmousewheel = function (e) {
        let ee = e || window.event;
        let lastViewport = cornerstone.getViewport(targetElement);
        if (lastViewport.scale < 8) {
            setTimeout(function () {
                let viewport = cornerstone.getViewport(targetElement);
                let translateX = sumx * (viewport.scale - lastViewport.scale);
                let translateY = sumy * (viewport.scale - lastViewport.scale);
                drawResult(translateX, translateY);
            }, 10);
        } else {
            cornerstoneTools.zoomWheel.deactivate(targetElement);
            if (ee.wheelDelta < 0) {
                var tempScale = lastViewport.scale;
                setTimeout(function () {
                    let viewport = cornerstone.getViewport(targetElement);
                    viewport.scale = 7;
                    cornerstone.setViewport(targetElement, viewport);
                    let translateX = sumx * (viewport.scale - tempScale);
                    let translateY = sumy * (viewport.scale - tempScale);
                    drawResult(translateX, translateY);
                    cornerstoneTools.zoomWheel.activate(targetElement);
                }, 10);
            }
        }
    };
});
document.getElementById('enableLength').addEventListener('click', function () {
    activate('enableLength');
    clearCornerstoneTools();
    cornerstoneTools.length.activate(targetElement, 1);
    cornerstoneTools.stackScrollWheel.activate(targetElement);
    $("#toolsState").text('测量');

    forbidMove(0);
});
document.getElementById('enableReset').addEventListener('click', function () {
    activate('enableReset');
    clearCornerstoneTools();
    cornerstoneTools.stackScrollWheel.activate(targetElement);
    cornerstone.reset(targetElement);
    sliceFlag = true;
    $("#toolsState").text('');

    sumx = 0;
    sumy = 0;
    forbidMove(1);
    drawResult();

});
document.getElementById('enableWwcc').addEventListener('click', function () {
    activate('enableWwcc');
    clearCornerstoneTools();
    cornerstoneTools.wwwc.activate(targetElement, 1);
    cornerstoneTools.stackScrollWheel.activate(targetElement);
    $("#toolsState").text('调窗');

    forbidMove(0);
});
document.getElementById('enableMeasure').addEventListener('click', function () {
    activate("enableMeasure");
    clearCornerstoneTools();
    forbidMove(0);
    $("#toolsState").text('CT测量');
    measureFlag = true;
    let canvas = $("#aboveCanvas")[0];
    let $canvas = $("#aboveCanvas");
    var canvasPic = new Image();
    canvasPic.src = canvas.toDataURL();
    let context = canvas.getContext("2d");
    context.strokeStyle = "#3f76ff";
    context.lineWidth = 2;
    let startX, startY, endX, endY;

    let positions = [];
    let moveFlag = false;

    measureDown = function (e) {
        e = e || window.event;
        var ox = e.clientX - $canvas.offset().left;
        var oy = e.clientY - $canvas.offset().top;

        let viewport = cornerstone.getViewport(targetElement);
        let scale = viewport.scale;
        let moveX = (canvas.width - imgWidth * scale) / 2;//计算dicom图像相对于canvas的偏移量
        let moveY = (imgHeight * scale - canvas.height) / 2;

        startX = ox - sumx * scale;//canvas上的真实坐标 鼠标指哪画哪
        startY = oy - sumy * scale;
        context.beginPath();
        context.strokeStyle = "#3f76ff";
        context.moveTo(startX, startY);
        positions.length = 0;
        let position = new Object();
        position.num = 0;
        position.x = Math.round((startX - moveX) / scale);//通过canvas上的真实坐标求dicom图像上标注结果坐标的计算公式
        position.y = Math.round((startY + moveY) / scale);
        positions.push(position);
        moveFlag = false;
        let frontX = startX, frontY = startY;
        canvas.onmousemove = function (e) {
            moveFlag = true;
            e = e || window.event;
            var ox1 = e.clientX - $canvas.offset().left;
            var oy1 = e.clientY - $canvas.offset().top;
            ox1 = ox1 - sumx * scale;
            oy1 = oy1 - sumy * scale;
            getLineAllPoint(frontX, frontY, ox1, oy1, moveX, moveY, scale, positions);
            frontX = ox1;
            frontY = oy1;
            endX = ox1;
            endY = oy1;
            // let px = Math.round((ox1-moveX)/scale);
            // let py = Math.round((oy1+moveY)/scale);
            // let position = new Object();
            // position.num = 0;
            // position.x = px;
            // position.y = py;
            // positions.push(position);
            // console.log(px,py);
            context.lineTo(ox1, oy1);
            context.stroke();
        };
    };
    canvas.onmousedown = measureDown;
    measureUp = function () {
        let viewport = cornerstone.getViewport(targetElement);
        let scale = viewport.scale;
        let moveX = (canvas.width - imgWidth * scale) / 2;
        let moveY = (imgHeight * scale - canvas.height) / 2;

        context.clearRect(-5000, -5000, 9999, 9999);
        context.drawImage(canvasPic, 0, 0);
        context.lineTo(startX, startY);
        context.stroke();
        context.closePath();

        if (moveFlag) {
            getLineAllPoint(startX, startY, endX, endY, moveX, moveY, scale, positions);
        }

        $.ajax({
            type: 'post',
            url: '/VSLC/sequence/D2NoduleInfo',
            contentType: "application/json",
            data: JSON.stringify({
                sequenceID: parseInt($sequenceID),
                positionList: positions,
                zIndex: dcmIndex
            }),
            success: function (data) {
                var lbMark3 = document.getElementById('avgCTSpan');
                lbMark3.textContent = '平均CT值 ' + data.avgCT.toFixed(2);
                var lbMark4 = document.getElementById('minCTSpan');
                lbMark4.textContent = '最小CT值 ' + data.minCT.toFixed(2);
                var lbMark5 = document.getElementById('maxCTSpan');
                lbMark5.textContent = '最大CT值 ' + data.maxCT.toFixed(2);
            }
        });
        canvas.onmousemove = null;
    }
    canvas.onmouseup = measureUp;
});
let positionsArray = [];
let r = 5,showCircle = false,showCircle1 = false,isDraw = false,ox2,oy2,firstDraw,firstDraw1,pushMove,pushMove1,dragMove,dragMove1,firstDrawFlag = true,apiIndex,
    measureFlag = false,measureDown,measureUp;
// 新增肺结节审核意见
$("#addSuggestion").off("click").click(function () {
    let maxNum = Math.max(...curObjNums);
    $("#addNumInput").val(maxNum+1);
    setTimeout(function () {
        $("#addNumInput").focus();
    },10);
    let num;
    $("#addNumButton").off("click").click(function () {
        num = $("#addNumInput").val();
        $("#addNumInput").val('');
        $('#add_modal').modal('hide');
        $("#suggestionList")
            .append('<tr><td style="width:12%">' + (num) + '</td>' +
                '<td style="width:12%"><input type="text"></td>' +
                '<td style="width:76%"><input type="text"></td></tr>');
        $("#toolsState").text('标注');
    });

    /**初始化**/
    clearCornerstoneTools();
    cornerstoneTools.stackScrollWheel.activate(targetElement);
    forbidMove(0);
    /**勾画部分**/
    let canvas = $("#aboveCanvas")[0];
    var canvasPic = new Image();
    canvasPic.src = canvas.toDataURL();
    let $canvas = $("#aboveCanvas");
    let context = canvas.getContext("2d");
    context.drawImage(canvasPic, 0, 0);

    firstDrawFlag = true;


    firstDraw = function (e) {
        e = e || window.event;
        let viewport = cornerstone.getViewport(targetElement);
        let scale = viewport.scale;
        let moveX = (canvas.width - imgWidth * scale) / 2;
        let moveY = (imgHeight * scale - canvas.height) / 2;
        if (e.button == 0) {
            var ox = e.clientX - $canvas.offset().left;
            var oy = e.clientY - $canvas.offset().top;

            ox = ox - sumx * scale;
            oy = oy - sumy * scale;
            let dx = calPositionX(ox, moveX, scale);
            let dy = calPositionY(oy, moveY, scale);
            if (firstDrawFlag){
                let positionsInfo = {
                    index : null,
                    positions : null,
                    subPositions : null,
                    positionsArray : []
                };
                let positions = [];
                positionsArray = [];
                let isAlive = false;
                apiIndex = 0;
                for (let i=0;i<allPositionsInfo.length;i++){
                    if (allPositionsInfo[i].index==dcmIndex-1){
                        isAlive = true;
                        apiIndex =  i;
                        break;
                    }
                }
                if (isAlive){
                    // positions = allPositionsInfo[apiIndex].positions;
                    positionsArray = allPositionsInfo[apiIndex].positionsArray;
                }
                context.beginPath();
                context.strokeStyle = "#00ff00";
                context.lineWidth = scale;
                context.moveTo(ox, oy);

                let position = new Object();
                position.num = num;
                position.x = dx;
                position.y = dy;
                positions.push(position);
                let frontX = ox, frontY = oy;
                canvas.onmousemove = function (e) {
                    e = e || window.event;
                    var ox1 = e.clientX - $canvas.offset().left;
                    var oy1 = e.clientY - $canvas.offset().top;
                    ox1 = ox1 - sumx * scale;
                    oy1 = oy1 - sumy * scale;
                    let dx1 = calPositionX(ox1, moveX, scale);
                    let dy1 = calPositionY(oy1, moveY, scale);
                    if (!(positions[positions.length - 1].x == dx1 && positions[positions.length - 1].y == dy1)||positions.length==0) {
                        getLineAllPoint(frontX, frontY, ox1, oy1, moveX, moveY, scale, positions);
                    }
                    frontX = ox1;
                    frontY = oy1;
                    context.lineTo(dx1 * scale + moveX, dy1 * scale - moveY);
                    context.stroke();
                    canvas.onmouseup = function () {
                        firstDrawFlag = false;
                        showCircle = true;
                        showCircle1 = false;
                        getLineAllPoint(frontX, frontY, ox, oy, moveX, moveY, scale, positions);
                        positions.splice(positions.length-1,1);
                        context.lineTo(ox, oy);
                        context.stroke();
                        context.closePath();
                        canvasPic.src = canvas.toDataURL();
                        if (isAlive){
                            allPositionsInfo[apiIndex].positions = positions;
                            allPositionsInfo[apiIndex].positionsArray.push(positions);
                            // currentResult[dcmIndex-1].positionList.pop();//
                            // currentResult[dcmIndex-1].positionList.push(positions);
                        }else {
                            positionsInfo.index = dcmIndex - 1;
                            positionsInfo.positions = positions;
                            positionsInfo.positionsArray.push(positions);
                            positionsArray.push(positions);
                            allPositionsInfo.push(positionsInfo);
                            apiIndex = allPositionsInfo.length-1;
                            // currentResult[dcmIndex-1].positionList.push(positions);
                        }
                        canvas.onmouseup = null;
                        pushMove = function (e) {
                            e = e || window.event;
                            ox2 = e.clientX - $canvas.offset().left;
                            oy2 = e.clientY - $canvas.offset().top;

                            let viewport = cornerstone.getViewport(targetElement);
                            let scale = viewport.scale;
                            moveX = (canvas.width - imgWidth * scale) / 2;
                            moveY = (imgHeight * scale - canvas.height) / 2;

                            ox2 = ox2 - sumx * scale;
                            oy2 = oy2 - sumy * scale;
                            let dx2 = calPositionX(ox2, moveX, scale);
                            let dy2 = calPositionY(oy2, moveY, scale);
                            context.clearRect(-5000, -5000, 9999, 9999);
                            drawResult(0,0);
                            context.beginPath();//画光标
                            context.lineWidth = 1;
                            context.strokeStyle = "#F5270B";
                            context.arc(ox2, oy2, r * scale, 0, 2 * Math.PI);
                            context.stroke();
                            context.closePath();
                            canvas.onmousedown = function (e) {
                                e = e || window.event;
                                let ox3 = e.clientX - $canvas.offset().left;
                                let oy3 = e.clientY - $canvas.offset().top;
                                ox3 = ox3 - sumx * scale;
                                oy3 = oy3 - sumy * scale;
                                let dx3 = calPositionX(ox3, moveX, scale);
                                let dy3 = calPositionY(oy3, moveY, scale);

                                if (e.button == 0) {
                                    let pushPositionsArray = [];
                                    positionsArray.forEach((positions,positionsIndex)=>{
                                        let pushPositions = [];
                                        positions.forEach((pi,i)=>{
                                            let d = Math.sqrt(Math.pow((pi.x-dx3),2)+Math.pow((pi.y-dy3),2));
                                            if (d<=r){
                                                let pushPosition = {
                                                    x : pi.x,
                                                    y : pi.y,
                                                    i : i,
                                                    d : d,
                                                };
                                                pushPositions.push(pushPosition);
                                            }
                                        });
                                        pushPositionsArray.push(pushPositions);
                                    });
                                    // console.log(JSON.parse(JSON.stringify(pushPositionsArray)));
                                    // let tempPushPositions = [...pushPositions];
                                    // let insertIndex = pushPositions[0].i;
                                    // let tailIndex = pushPositions[pushPositions.length-1].i;
                                    // console.log(JSON.parse(JSON.stringify(positions)));
                                    // for (let i=insertIndex;i<=tailIndex;i++){
                                    //     positions.splice(insertIndex,1);
                                    // }
                                    // console.log(positions);
                                    // let tempPositions = [...positions];

                                    let lastDX = dx3;
                                    let lastDY = dy3;
                                    canvas.onmousemove = function (e) {
                                        e = e || window.event;
                                        let ox4 = e.clientX - $canvas.offset().left;
                                        let oy4 = e.clientY - $canvas.offset().top;
                                        ox4 = ox4 - sumx * scale;
                                        oy4 = oy4 - sumy * scale;
                                        let dx4 = calPositionX(ox4, moveX, scale);
                                        let dy4 = calPositionY(oy4, moveY, scale);
                                        context.clearRect(-5000, -5000, 9999, 9999);
                                        drawResult(0,0);
                                        context.beginPath();//画光标
                                        context.lineWidth = 1;
                                        context.strokeStyle = "#F5270B";
                                        context.arc(ox4, oy4, r * scale, 0, 2 * Math.PI);
                                        context.stroke();
                                        context.closePath();
                                        let x1 = dx4,y1 = dy4,x0 = lastDX,y0 = lastDY;
                                        let a = Math.sqrt(Math.pow((x1-x0),2)+Math.pow((y1-y0),2));
                                        let k = (y1-y0)/(x1-x0);
                                        let cx0 = dx4-lastDX;
                                        let cy0 = dy4-lastDY;
                                        try {
                                            // let add = 0;
                                            pushPositionsArray.forEach((pushPositions,pushPositionsIndex)=>{
                                                pushPositions.forEach((pp,ppi)=>{
                                                    let x2,y2,b,c,cos;
                                                    x2 = pp.x;
                                                    y2 = pp.y;
                                                    // console.log(x2,y2,ppi);
                                                    b = Math.sqrt(Math.pow((x2-x0),2)+Math.pow((y2-y0),2));
                                                    c = Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2));
                                                    cos = (Math.pow(a,2)+Math.pow(b,2)-Math.pow(c,2))/(2*a*b);
                                                    let cx1,cy1,cx2,cy2,xc,ct;//cx1,cy1为相对于原圆心的像素点变形后坐标 cx2,cy2为相对于原圆心的像素点变形前坐标 xc为变形后坐标距原点距离
                                                    cx2 = pp.x-lastDX;
                                                    cy2 = pp.y-lastDY;
                                                    let uc =  Math.sqrt(Math.pow((pp.x-lastDX),2)+Math.pow((pp.y-lastDY),2));
                                                    let cm = Math.sqrt(Math.pow((dx4-lastDX),2)+Math.pow((dy4-lastDY),2));
                                                    // let ux = -(cm/Math.pow(r,2))*Math.pow(uc,2)+cm;
                                                    let ux = (cm/Math.pow(r,2))*Math.pow(uc,2)-((2*cm)/r)*uc+cm;
                                                    if (b==0){
                                                        xc = cm;
                                                        // console.log(cm)
                                                    }else if (a!=0){
                                                        xc = Math.sqrt(Math.pow(uc,2)+Math.pow(ux,2)+2*uc*ux*cos);
                                                    }else {//a==0
                                                        throw new Error("end");
                                                    }
                                                    // console.log("xc:"+xc,"uc:"+uc,"cm:"+cm,"ux:"+ux,"cos:"+cos);
                                                    ct = (Math.pow(r,2)-Math.pow(xc,2))/((Math.pow(r,2)-Math.pow(xc,2))+5*(Math.pow(cx0,2)+Math.pow(cy0,2)));
                                                    cx1 = cx2+Math.pow(ct,2)*cx0;
                                                    cy1 = cy2+Math.pow(ct,2)*cy0;
                                                    // console.log(cy2,cy1,cy0,ct);
                                                    // let ppx2 = pp.x + Math.round(cx1 - cx2);
                                                    // let ppy2 = pp.y + Math.round(cy1 - cy2);
                                                    // if (ppi!=0){
                                                    //     let absX = Math.abs(pushPositions[ppi-1+add].x-ppx2);
                                                    //     let absY = Math.abs(pushPositions[ppi-1+add].y-ppy2);
                                                    //     if (Math.pow(absX,2)+Math.pow(absY,2)>2){
                                                    //         add += drawLine(pushPositions[ppi-1+add].x,pushPositions[ppi-1+add].y,ppx2,ppy2,pushPositions,ppi-1+add);
                                                    //         pushPositions[ppi+add].x = ppx2;
                                                    //         pushPositions[ppi+add].y = ppy2;
                                                    //     }else {
                                                    //         pushPositions[ppi+add].x = ppx2;
                                                    //         pushPositions[ppi+add].y = ppy2;
                                                    //     }
                                                    // }else {
                                                    //     let absX = Math.abs(pushPositions[0].x-ppx2);
                                                    //     let absY = Math.abs(pushPositions[0].y-ppy2);
                                                    //     if (Math.pow(absX,2)+Math.pow(absY,2)>2){
                                                    //         add += drawLine(pushPositions[0].x,pushPositions[0].y,ppx2,ppy2,pushPositions,0);
                                                    //         pushPositions[ppi+add].x = ppx2;
                                                    //         pushPositions[ppi+add].y = ppy2;
                                                    //     }else {
                                                    //         pushPositions[ppi].x = ppx2;
                                                    //         pushPositions[ppi].y = ppy2;
                                                    //     }
                                                    // }
                                                    positionsArray[pushPositionsIndex][pp.i].x += Math.round(cx1-cx2);
                                                    positionsArray[pushPositionsIndex][pp.i].y += Math.round(cy1-cy2);
                                                    // positions[pp.i].x += cx1-cx2;
                                                    // positions[pp.i].y += cy1-cy2;
                                                });
                                            });
                                            // tempPushPositions = [...pushPositions];
                                            //
                                            // var tempPositions1 = [...tempPositions];
                                            // tempPositions1.splice(insertIndex,0,...pushPositions);
                                            // positions = tempPositions1;
                                            // allPositionsInfo[allPositionsInfo.length-1].positions = positions;
                                            // console.table(JSON.parse(JSON.stringify(pushPositions)));
                                            // console.table(JSON.parse(JSON.stringify(positions)));
                                        }catch (e) {
                                            if (e.message != "end") throw e;
                                        }
                                        pushPositionsArray = [];
                                        positionsArray.forEach((positions,positionsIndex)=>{
                                            let pushPositions = [];
                                            positions.forEach((pi,i)=>{
                                                let d = Math.sqrt(Math.pow((pi.x-dx4),2)+Math.pow((pi.y-dy4),2));
                                                if (d<=r){
                                                    let pushPosition = {
                                                        x : pi.x,
                                                        y : pi.y,
                                                        i : i,
                                                        d : d,
                                                    };
                                                    pushPositions.push(pushPosition);
                                                }
                                            });
                                            pushPositionsArray.push(pushPositions);
                                        });
                                        // let x1 = dx4,y1 = dy4,x0 = lastDX,y0 = lastDY;
                                        // let a = Math.sqrt(Math.pow((x1-x0),2)+Math.pow((y1-y0),2));
                                        // let k = (y1-y0)/(x1-x0);
                                        // let xFlag,yFlag;
                                        // if (x1-x0<0){
                                        //     xFlag = -1;
                                        // }else {
                                        //     xFlag = 1;
                                        // }
                                        // if (y1-y0>0){
                                        //     yFlag = 1;
                                        // }else {
                                        //     yFlag = -1;
                                        // }
                                        // pushPositions.forEach((pp,ppi)=>{
                                        //    let x2,y2,b,c,cos,t;
                                        //    x2 = pp.x;
                                        //    y2 = pp.y;
                                        //    b = Math.sqrt(Math.pow((x2-x0),2)+Math.pow((y2-y0),2));
                                        //    c = Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2));
                                        //    cos = (Math.pow(a,2)+Math.pow(b,2)-Math.pow(c,2))/(2*a*b);
                                        //    if (b==0||c==0){
                                        //        t = 1;
                                        //        if (!isNaN(k)){
                                        //            positions[pp.i].x += xFlag*t/Math.sqrt(Math.pow(k,2)+1);
                                        //            positions[pp.i].y += yFlag*t*k/Math.sqrt(Math.pow(k,2)+1);
                                        //        }
                                        //    }else if (a!=0){
                                        //        t = Math.abs(cos);
                                        //        if (!isFinite(k)){
                                        //            positions[pp.i].y += yFlag*t;
                                        //        }else {
                                        //            positions[pp.i].x += xFlag*t/Math.sqrt(Math.pow(k,2)+1);
                                        //            positions[pp.i].y += yFlag*t*k/Math.sqrt(Math.pow(k,2)+1);
                                        //        }
                                        //    }
                                        //    console.log(cos,a,b,c);
                                        // });
                                        lastDX = dx4;
                                        lastDY = dy4;
                                    };
                                    canvas.onmouseup = function (e) {
                                        // pushPositionsArray.forEach((pushPositions,pushPositionsIndex)=>{
                                        //     if (pushPositions.length!=0){
                                        //         let startIndex,endIndex;
                                        //         startIndex = pushPositions[0].i;
                                        //         endIndex = pushPositions[pushPositions.length-1].i;
                                        //         console.log(startIndex,endIndex);
                                        //         let positions = positionsArray[pushPositionsIndex];
                                        //         let add = 0;
                                        //         if (startIndex==0&&endIndex==positions.length-1){//所选区域含起点补充线段上的点
                                        //             let jump;
                                        //             let drop;
                                        //             let length = positions.length;
                                        //             for(let i=0;i<pushPositions.length-1;i++){
                                        //                 if (pushPositions[i+1].i-pushPositions[i].i>2){
                                        //                     jump = pushPositions[i].i;
                                        //                     drop = pushPositions[i+1].i;
                                        //                 }
                                        //             }
                                        //             for (let i=-1;i<=jump;i++){
                                        //                 let p1,p2;
                                        //                 if (i==-1){
                                        //                     p1 = positions[length-1];
                                        //                     p2 = positions[0];
                                        //                     drawLine(Math.round(p1.x),Math.round(p1.y),Math.round(p2.x),Math.round(p2.y),positions,length-1);
                                        //                 }else {
                                        //                     p1 = positions[i+add];
                                        //                     p2 = positions[i+1+add];
                                        //                     add += drawLine(Math.round(p1.x),Math.round(p1.y),Math.round(p2.x),Math.round(p2.y),positions,i+add);
                                        //                 }
                                        //
                                        //             }
                                        //             for (let i=drop;i<=length-2;i++){
                                        //                 let p1 = positions[i+add];
                                        //                 let p2 = positions[i+1+add];
                                        //                 add += drawLine(Math.round(p1.x),Math.round(p1.y),Math.round(p2.x),Math.round(p2.y),positions,i+add);
                                        //             }
                                        //         }else {//一般情况下补充线段上的点
                                        //             for (let i=startIndex-1;i<=endIndex;i++){
                                        //                 let p1,p2;
                                        //                 if (i+add==-1){
                                        //                     p1 = positions[positions.length-1];
                                        //                     p2 = positions[0];
                                        //                     add += drawLine(Math.round(p1.x),Math.round(p1.y),Math.round(p2.x),Math.round(p2.y),positions,positions.length-1);
                                        //                     console.log(1111111111111111111111111);
                                        //                 }else if (i+1+add==positions.length){
                                        //                     p1 = positions[positions.length-1];
                                        //                     p2 = positions[0];
                                        //                     add += drawLine(Math.round(p1.x),Math.round(p1.y),Math.round(p2.x),Math.round(p2.y),positions,positions.length-1);
                                        //                     console.log(222222222222222222222222);
                                        //                 }else {
                                        //                     p1 = positions[i+add];
                                        //                     p2 = positions[i+1+add];
                                        //                     add += drawLine(Math.round(p1.x),Math.round(p1.y),Math.round(p2.x),Math.round(p2.y),positions,i+add);
                                        //                     console.log(33333333333333333333333);
                                        //                 }
                                        //             }
                                        //         }
                                        //     }
                                        // });
                                        let tempPositionsArray = [];
                                        positionsArray.forEach((positions,positionsIndex)=> {
                                            let tempPositions = [...positions];
                                            let add = 0;
                                            for (let i = 0; i < positions.length; i++) {
                                                let position1, position2;
                                                if (i == positions.length - 1) {
                                                    position1 = positions[i];
                                                    position2 = positions[0];
                                                } else {
                                                    position1 = positions[i];
                                                    position2 = positions[i + 1];
                                                }
                                                add += drawLine(position1.x, position1.y, position2.x, position2.y, tempPositions, i + add);
                                            }
                                            tempPositionsArray.push(tempPositions);
                                        });
                                        positionsArray = [].concat(tempPositionsArray);
                                        canvas.onmousemove = pushMove;
                                        allPositionsInfo[apiIndex].positionsArray = positionsArray;
                                    }
                                }else if (e.button == 2){
                                    subMove(ox3,oy3,dx3,dy3,canvas,context,$canvas,moveX,moveY,scale,pushMove)
                                }
                            }
                        };
                        dragMove = function (e) {
                            e = e || window.event;
                            ox2 = e.clientX - $canvas.offset().left;
                            oy2 = e.clientY - $canvas.offset().top;

                            let viewport = cornerstone.getViewport(targetElement);
                            let scale = viewport.scale;
                            moveX = (canvas.width - imgWidth * scale) / 2;
                            moveY = (imgHeight * scale - canvas.height) / 2;

                            ox2 = ox2 - sumx * scale;
                            oy2 = oy2 - sumy * scale;
                            let dx2 = calPositionX(ox2, moveX, scale);
                            let dy2 = calPositionY(oy2, moveY, scale);
                            context.clearRect(-5000, -5000, 9999, 9999);
                            drawResult(0,0);
                            context.beginPath();//画光标
                            context.lineWidth = 1;
                            context.strokeStyle = "#F5270B";
                            context.moveTo(ox2-3*scale,oy2);
                            context.lineTo(ox2+3*scale,oy2);
                            context.stroke();
                            context.closePath();
                            context.beginPath();//画光标
                            context.lineWidth = 1;
                            context.strokeStyle = "#F5270B";
                            context.moveTo(ox2,oy2-3*scale);
                            context.lineTo(ox2,oy2+3*scale);
                            context.stroke();
                            context.closePath();

                            try {
                                positionsArray.forEach((positions,positionsIndex)=>{
                                    positions.forEach((pi,i)=>{
                                        if (pi.x==dx2&&pi.y==dy2){
                                            context.beginPath();//画光标
                                            context.lineWidth = 1;
                                            context.strokeStyle = "#f5e808";
                                            context.arc(ox2, oy2, 3 * scale, 0, 2 * Math.PI);
                                            context.stroke();
                                            context.closePath();
                                            canvas.onmousedown = function (e) {
                                                e = e || window.event;
                                                let oxDown = e.clientX - $canvas.offset().left;
                                                let oyDown = e.clientY - $canvas.offset().top;
                                                oxDown = oxDown - sumx * scale;
                                                oyDown = oyDown - sumy * scale;
                                                let dxDown = calPositionX(oxDown, moveX, scale);
                                                let dyDown = calPositionY(oyDown, moveY, scale);
                                                if (e.button == 0){
                                                    canvas.onmousemove = function (e) {
                                                        e = e || window.event;
                                                        let ox3 = e.clientX - $canvas.offset().left;
                                                        let oy3 = e.clientY - $canvas.offset().top;
                                                        ox3 = ox3 - sumx * scale;
                                                        oy3 = oy3 - sumy * scale;
                                                        let dx3 = calPositionX(ox3, moveX, scale);
                                                        let dy3 = calPositionY(oy3, moveY, scale);
                                                        positions[i].x = dx3;
                                                        positions[i].y = dy3;
                                                        positions[i].dragFlag = 1;
                                                        context.clearRect(-5000, -5000, 9999, 9999);
                                                        drawResult(0,0);
                                                        context.beginPath();//画光标
                                                        context.lineWidth = 1;
                                                        context.strokeStyle = "#F5270B";
                                                        context.moveTo(ox3-3*scale,oy3);
                                                        context.lineTo(ox3+3*scale,oy3);
                                                        context.stroke();
                                                        context.closePath();
                                                        context.beginPath();//画光标
                                                        context.lineWidth = 1;
                                                        context.strokeStyle = "#F5270B";
                                                        context.moveTo(ox3,oy3-3*scale);
                                                        context.lineTo(ox3,oy3+3*scale);
                                                        context.stroke();
                                                        context.closePath();
                                                        context.beginPath();//画光标
                                                        context.lineWidth = 1;
                                                        context.strokeStyle = "#f5e808";
                                                        context.arc(ox3, oy3, 3 * scale, 0, 2 * Math.PI);
                                                        context.stroke();
                                                        context.closePath();
                                                        canvas.onmouseup = function (e) {
                                                            canvas.onmousemove = dragMove;
                                                            allPositionsInfo[apiIndex].positions = positions;
                                                        }
                                                    }
                                                }else if (e.button == 2){
                                                    subMove(oxDown,oyDown,dxDown,dyDown,canvas,context,$canvas,moveX,moveY,scale,pushMove)
                                                }
                                            };
                                            throw new Error("end");
                                        }else {
                                            canvas.onmousedown = function (e) {
                                                e = e || window.event;
                                                let oxDown = e.clientX - $canvas.offset().left;
                                                let oyDown = e.clientY - $canvas.offset().top;
                                                oxDown = oxDown - sumx * scale;
                                                oyDown = oyDown - sumy * scale;
                                                let dxDown = calPositionX(oxDown, moveX, scale);
                                                let dyDown = calPositionY(oyDown, moveY, scale);
                                                if (e.button == 2){
                                                    subMove(oxDown,oyDown,dxDown,dyDown,canvas,context,$canvas,moveX,moveY,scale,pushMove)
                                                }
                                            };
                                            canvas.onmouseup = null;
                                        }
                                    });
                                });
                            }catch (e) {
                                if (e.message != "end") throw e;
                            }
                        };
                        if (r>=2){
                            canvas.onmousemove = pushMove;
                        }else {
                            canvas.onmousemove = dragMove;
                        }
                    }
                };
            }
        } else if (e.button == 2) {

        }
    };
    canvas.onmousedown = firstDraw;
    $("#save_sketch").off("click").click(function () {
        if (allPositionsInfo.length!=0){
            allPositionsInfo.forEach((positionsInfo,infoIndex)=>{
               positionsInfo.positionsArray.forEach((positions,positionsIndex)=>{
                   let tempPositions = [...positions];
                   let add=0;
                   positions.forEach((position,positionIndex)=>{
                      if (position.dragFlag){
                          let position1,position2;
                          if (positionIndex==positions.length-1){
                              position1 = positions[positionIndex-1];
                              position2 = positions[0];
                              add += drawLine(position1.x,position1.y,position.x,position.y,tempPositions,positionIndex-1+add);
                              add += drawLine(position.x,position.y,position2.x,position2.y,tempPositions,positionIndex+add);
                          }else if (positionIndex==0){
                              position1 = positions[positions.length-1];
                              position2 = positions[1];
                              add += drawLine(position.x,position.y,position2.x,position2.y,tempPositions,positionIndex+add);
                              drawLine(position1.x,position1.y,position.x,position.y,tempPositions,tempPositions.length-1);
                          }else {
                              position1 = positions[positionIndex-1];
                              position2 = positions[positionIndex+1];
                              add += drawLine(position1.x,position1.y,position.x,position.y,tempPositions,positionIndex-1+add);
                              add += drawLine(position.x,position.y,position2.x,position2.y,tempPositions,positionIndex+add);
                          }
                      }
                   });
                   allPositionsInfo[infoIndex].positionsArray[positionsIndex] = tempPositions;
               });
            });
            $.ajax({
                type :  "post",
                url : "/VSLC/sequence/saveSketch",
                contentType: "application/json",
                data : JSON.stringify({
                    sequenceID : parseInt($sequenceID),
                    sketchFile : $("#addSortInput option:selected").val(),
                    sketchNum : num,
                    allPositionsInfo : allPositionsInfo
                }),
                success : function (data) {
                    $('#sketchTree').tree('reload');
                    $("#toolsState").text('');
                    toastr.success('保存成功');

                    allPositionsInfo = [];
                    showCircle = false;
                    context.clearRect(-5000, -5000, 9999, 9999);
                    canvas.onmousedown = null;
                    canvas.onmousemove = null;
                    canvas.onmouseup = null;
                }
            });
        }
    });
});

// 减少肺结节审核意见
$("#removeSuggestion").click(function () {
    for (let i = 0; i < noduleCalcs.length; i++) {
        if (noduleCalcs[i] == $sketchNum) {
            toastr.error("正在计算该结节的信息，请稍后再试");
            return;
        }
    }
    let msg = '确定要删除';
    if ($sketchFile.indexOf("lung") != -1) {
        msg+='肺分割结果吗';
    } else if ($sketchFile.indexOf("nodule") != -1) {
        if ($sketchNum != null) {
            msg+='结节'+$sketchNum+'吗';
        } else {
            msg+='全部结节吗';
        }
    }
    if (confirm(msg)) {
        $.ajax({
            url: '/VSLC/sequence/deleteSketch',
            data: {
                sequenceID : $sequenceID,
                sketchFile : $sketchFile,
                sketchNum : $sketchNum
            },
            success : function() {
                $('#sketchTree').tree('reload');
                toastr.success('标注已删除');

                let len = $("#suggestionList").find("tr").length - 1;
                for (let i = len; i > 0; i--) {
                    if ($sketchNum == null) {
                        $("#suggestionList").find("tr").eq(i).remove();
                    } else {
                        let number = suggestionList.rows[i].cells[0].innerText;
                        if (number == $sketchNum) {
                            $("#suggestionList").find("tr").eq(i).remove();
                            i--;
                        }
                    }
                }
            }
        });
    }
});

// $(".picZone_02_01").dblclick(function () {
//     $(this).css("width","99.55%");
//     $(this).css("height","8.2rem");
//     // $(this).css("width","100%");
// });
let edits = [];
document.getElementById('enableDraw').addEventListener('click', function () {
    clearCornerstoneTools();
    cornerstoneTools.stackScrollWheel.activate(targetElement);
    $("#toolsState").text('调整');
    let canvas = $("#aboveCanvas")[0];
    let $canvas = $("#aboveCanvas");
    let context = canvas.getContext("2d");
    edits = [];
    currentResult.forEach((cr,cri)=>{
       if (cr.positionList.length!=0){
           let positionsInfo = {
               index : cri,
               positionsArray : cr.positionList
           };
           allPositionsInfo.push(positionsInfo);
       }
    });
    positionsArray = currentDraw.positionList;
    canvas.onmousedown = null;
    canvas.onmouseup = null;
    showCircle = false;
    isDraw = true;

    firstDraw1 = function (e) {
        e = e || window.event;
        let viewport = cornerstone.getViewport(targetElement);
        let scale = viewport.scale;
        let moveX = (canvas.width - imgWidth * scale) / 2;
        let moveY = (imgHeight * scale - canvas.height) / 2;
        if (e.button == 0) {
            var ox = e.clientX - $canvas.offset().left;
            var oy = e.clientY - $canvas.offset().top;

            ox = ox - sumx * scale;
            oy = oy - sumy * scale;
            let dx = calPositionX(ox, moveX, scale);
            let dy = calPositionY(oy, moveY, scale);
            if (firstDrawFlag){
                let positionsInfo = {
                    index : null,
                    positionsArray : []
                };
                let positions = [];
                positionsArray = [];
                let isAlive = false;
                apiIndex = 0;
                for (let i=0;i<allPositionsInfo.length;i++){
                    if (allPositionsInfo[i].index==dcmIndex-1){
                        isAlive = true;
                        apiIndex =  i;
                        break;
                    }
                }
                if (isAlive){
                    // positions = allPositionsInfo[apiIndex].positions;
                    positionsArray = allPositionsInfo[apiIndex].positionsArray;
                }
                context.beginPath();
                context.strokeStyle = "#00ff00";
                context.lineWidth = scale;
                context.moveTo(ox, oy);

                let position = new Object();
                position.num = 0;
                position.x = dx;
                position.y = dy;
                positions.push(position);
                let frontX = ox, frontY = oy;
                canvas.onmousemove = function (e) {
                    e = e || window.event;
                    var ox1 = e.clientX - $canvas.offset().left;
                    var oy1 = e.clientY - $canvas.offset().top;
                    ox1 = ox1 - sumx * scale;
                    oy1 = oy1 - sumy * scale;
                    let dx1 = calPositionX(ox1, moveX, scale);
                    let dy1 = calPositionY(oy1, moveY, scale);
                    if (!(positions[positions.length - 1].x == dx1 && positions[positions.length - 1].y == dy1)||positions.length==0) {
                        getLineAllPoint(frontX, frontY, ox1, oy1, moveX, moveY, scale, positions);
                    }
                    frontX = ox1;
                    frontY = oy1;
                    context.lineTo(dx1 * scale + moveX, dy1 * scale - moveY);
                    context.stroke();
                    canvas.onmouseup = function () {
                        firstDrawFlag = false;
                        showCircle = false;
                        showCircle1 = true;
                        getLineAllPoint(frontX, frontY, ox, oy, moveX, moveY, scale, positions);
                        positions.splice(positions.length-1,1);
                        context.lineTo(ox, oy);
                        context.stroke();
                        context.closePath();
                        if (isAlive){
                            allPositionsInfo[apiIndex].positions = positions;
                            allPositionsInfo[apiIndex].positionsArray.push(positions);
                            // currentResult[dcmIndex-1].positionList.pop();//
                            currentResult[dcmIndex-1].positionList.push(positions);
                        }else {
                            positionsInfo.index = dcmIndex - 1;
                            positionsInfo.positions = positions;
                            positionsInfo.positionsArray.push(positions);
                            positionsArray.push(positions);
                            allPositionsInfo.push(positionsInfo);
                            apiIndex = allPositionsInfo.length-1;
                            currentResult[dcmIndex-1].positionList.push(positions);
                        }
                        if (edits.indexOf(dcmIndex-1)==-1){
                            edits.push(dcmIndex-1);
                        }
                        canvas.onmousemove = pushMove1;
                    }
                }
            }
        }
    };
    pushMove1 = function (e) {
        e = e || window.event;
        ox2 = e.clientX - $canvas.offset().left;
        oy2 = e.clientY - $canvas.offset().top;

        let viewport = cornerstone.getViewport(targetElement);
        let scale = viewport.scale;
        let moveX = (canvas.width - imgWidth * scale) / 2;
        let moveY = (imgHeight * scale - canvas.height) / 2;

        ox2 = ox2 - sumx * scale;
        oy2 = oy2 - sumy * scale;
        let dx2 = calPositionX(ox2, moveX, scale);
        let dy2 = calPositionY(oy2, moveY, scale);
        context.clearRect(-5000, -5000, 9999, 9999);
        drawResult(0,0);
        context.beginPath();//画光标
        context.lineWidth = 1;
        context.strokeStyle = "#F5270B";
        context.arc(ox2, oy2, r * scale, 0, 2 * Math.PI);
        context.stroke();
        context.closePath();
        canvas.onmousedown = function (e) {
            e = e || window.event;
            let ox3 = e.clientX - $canvas.offset().left;
            let oy3 = e.clientY - $canvas.offset().top;
            ox3 = ox3 - sumx * scale;
            oy3 = oy3 - sumy * scale;
            let dx3 = calPositionX(ox3, moveX, scale);
            let dy3 = calPositionY(oy3, moveY, scale);

            if (e.button == 0){
                let pushPositionsArray = [];
                positionsArray.forEach((positions,positionsIndex)=>{
                    let pushPositions = [];
                    positions.forEach((pi,i)=>{
                        let d = Math.sqrt(Math.pow((pi.x-dx3),2)+Math.pow((pi.y-dy3),2));
                        if (d<=r){
                            let pushPosition = {
                                x : pi.x,
                                y : pi.y,
                                i : i,
                                d : d,
                            };
                            pushPositions.push(pushPosition);
                        }
                    });
                    pushPositionsArray.push(pushPositions);
                });

                let lastDX = dx3;
                let lastDY = dy3;
                canvas.onmousemove = function (e) {
                    e = e || window.event;
                    let ox4 = e.clientX - $canvas.offset().left;
                    let oy4 = e.clientY - $canvas.offset().top;
                    ox4 = ox4 - sumx * scale;
                    oy4 = oy4 - sumy * scale;
                    let dx4 = calPositionX(ox4, moveX, scale);
                    let dy4 = calPositionY(oy4, moveY, scale);
                    context.clearRect(-5000, -5000, 9999, 9999);
                    drawResult(0,0);
                    context.beginPath();//画光标
                    context.lineWidth = 1;
                    context.strokeStyle = "#F5270B";
                    context.arc(ox4, oy4, r * scale, 0, 2 * Math.PI);
                    context.stroke();
                    context.closePath();
                    let x1 = dx4,y1 = dy4,x0 = lastDX,y0 = lastDY;
                    let a = Math.sqrt(Math.pow((x1-x0),2)+Math.pow((y1-y0),2));
                    let k = (y1-y0)/(x1-x0);
                    let cx0 = dx4-lastDX;
                    let cy0 = dy4-lastDY;
                    try {
                        pushPositionsArray.forEach((pushPositions,pushPositionsIndex)=>{
                            pushPositions.forEach((pp,ppi)=>{
                                let x2,y2,b,c,cos;
                                x2 = pp.x;
                                y2 = pp.y;
                                b = Math.sqrt(Math.pow((x2-x0),2)+Math.pow((y2-y0),2));
                                c = Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2));
                                cos = (Math.pow(a,2)+Math.pow(b,2)-Math.pow(c,2))/(2*a*b);
                                let cx1,cy1,cx2,cy2,xc,ct;//x1,y1为相对于原圆心的像素点变形后坐标 x2,y2为相对于原圆心的像素点变形前坐标 xc为变形后坐标距原点距离
                                cx2 = pp.x-lastDX;
                                cy2 = pp.y-lastDY;
                                let uc =  Math.sqrt(Math.pow((pp.x-lastDX),2)+Math.pow((pp.y-lastDY),2));
                                let cm = Math.sqrt(Math.pow((dx4-lastDX),2)+Math.pow((dy4-lastDY),2));
                                // let ux = -(cm/Math.pow(r,2))*Math.pow(uc,2)+cm;
                                let ux = (cm/Math.pow(r,2))*Math.pow(uc,2)-((2*cm)/r)*uc+cm;
                                if (b==0||c==0){
                                    xc = cm;
                                }else if (a!=0){
                                    xc = Math.sqrt(Math.pow(uc,2)+Math.pow(ux,2)+2*uc*ux*cos);
                                }else {//a==0
                                    throw new Error("end");
                                }
                                ct = (Math.pow(r,2)-Math.pow(xc,2))/((Math.pow(r,2)-Math.pow(xc,2))+5*(Math.pow(cx0,2)+Math.pow(cy0,2)));
                                cx1 = cx2+Math.pow(ct,2)*cx0;
                                cy1 = cy2+Math.pow(ct,2)*cy0;
                                positionsArray[pushPositionsIndex][pp.i].x += Math.round(cx1-cx2);
                                positionsArray[pushPositionsIndex][pp.i].y += Math.round(cy1-cy2);
                            });
                        });
                    }catch (e) {
                        if (e.message != "end") throw e;
                    }
                    pushPositionsArray = [];
                    positionsArray.forEach((positions,positionsIndex)=>{
                        let pushPositions = [];
                        positions.forEach((pi,i)=>{
                            let d = Math.sqrt(Math.pow((pi.x-dx4),2)+Math.pow((pi.y-dy4),2));
                            if (d<=r){
                                let pushPosition = {
                                    x : pi.x,
                                    y : pi.y,
                                    i : i,
                                    d : d,
                                };
                                pushPositions.push(pushPosition);
                            }
                        });
                        pushPositionsArray.push(pushPositions);
                    });
                    lastDX = dx4;
                    lastDY = dy4;
                };
                canvas.onmouseup = function (e) {
                    let tempPositionsArray = [];
                    positionsArray.forEach((positions,positionsIndex)=> {
                        let tempPositions = [...positions];
                        let add = 0;
                        for (let i = 0; i < positions.length; i++) {
                            let position1, position2;
                            if (i == positions.length - 1) {
                                position1 = positions[i];
                                position2 = positions[0];
                            } else {
                                position1 = positions[i];
                                position2 = positions[i + 1];
                            }
                            add += drawLine(position1.x, position1.y, position2.x, position2.y, tempPositions, i + add);
                        }
                        tempPositionsArray.push(tempPositions);
                    });
                    positionsArray = tempPositionsArray;
                    for (let i=0;i<allPositionsInfo.length;i++) {
                        if (dcmIndex - 1 == allPositionsInfo[i].index) {
                            apiIndex = i;
                            allPositionsInfo[i].positionsArray = [].concat(tempPositionsArray);
                        }
                    }
                    if (edits.indexOf(dcmIndex-1)==-1){
                        edits.push(dcmIndex-1);
                    }
                    canvas.onmousemove = pushMove1;
                    currentResult[dcmIndex - 1].positionList = positionsArray;
                }
            }else if (e.button == 2){
                subMove(ox3,oy3,dx3,dy3,canvas,context,$canvas,moveX,moveY,scale,pushMove1,edits);
            }
        }
    };
    dragMove1 = function (e) {
        e = e || window.event;
        ox2 = e.clientX - $canvas.offset().left;
        oy2 = e.clientY - $canvas.offset().top;

        let viewport = cornerstone.getViewport(targetElement);
        let scale = viewport.scale;
        let moveX = (canvas.width - imgWidth * scale) / 2;
        let moveY = (imgHeight * scale - canvas.height) / 2;

        ox2 = ox2 - sumx * scale;
        oy2 = oy2 - sumy * scale;
        let dx2 = calPositionX(ox2, moveX, scale);
        let dy2 = calPositionY(oy2, moveY, scale);
        context.clearRect(-5000, -5000, 9999, 9999);
        drawResult(0,0);
        context.beginPath();//画光标
        context.lineWidth = 1;
        context.strokeStyle = "#F5270B";
        context.moveTo(ox2-3*scale,oy2);
        context.lineTo(ox2+3*scale,oy2);
        context.stroke();
        context.closePath();
        context.beginPath();//画光标
        context.lineWidth = 1;
        context.strokeStyle = "#F5270B";
        context.moveTo(ox2,oy2-3*scale);
        context.lineTo(ox2,oy2+3*scale);
        context.stroke();
        context.closePath();

        try {
            positionsArray.forEach((positions,positionsIndex)=>{
                positions.forEach((pi,i)=>{
                    if (pi.x==dx2&&pi.y==dy2){
                        context.beginPath();//画光标
                        context.lineWidth = 1;
                        context.strokeStyle = "#f5e808";
                        context.arc(ox2, oy2, 3 * scale, 0, 2 * Math.PI);
                        context.stroke();
                        context.closePath();
                        canvas.onmousedown = function (e) {
                            e = e || window.event;
                            let oxDown = e.clientX - $canvas.offset().left;
                            let oyDown = e.clientY - $canvas.offset().top;
                            oxDown = oxDown - sumx * scale;
                            oyDown = oyDown - sumy * scale;
                            let dxDown = calPositionX(oxDown, moveX, scale);
                            let dyDown = calPositionY(oyDown, moveY, scale);
                            if (e.button == 0){
                                canvas.onmousemove = function (e) {
                                    e = e || window.event;
                                    let ox3 = e.clientX - $canvas.offset().left;
                                    let oy3 = e.clientY - $canvas.offset().top;
                                    ox3 = ox3 - sumx * scale;
                                    oy3 = oy3 - sumy * scale;
                                    let dx3 = calPositionX(ox3, moveX, scale);
                                    let dy3 = calPositionY(oy3, moveY, scale);
                                    positions[i].x = dx3;
                                    positions[i].y = dy3;
                                    positions[i].dragFlag = 1;
                                    context.clearRect(-5000, -5000, 9999, 9999);
                                    drawResult(0,0);
                                    context.beginPath();//画光标
                                    context.lineWidth = 1;
                                    context.strokeStyle = "#F5270B";
                                    context.moveTo(ox3-3*scale,oy3);
                                    context.lineTo(ox3+3*scale,oy3);
                                    context.stroke();
                                    context.closePath();
                                    context.beginPath();//画光标
                                    context.lineWidth = 1;
                                    context.strokeStyle = "#F5270B";
                                    context.moveTo(ox3,oy3-3*scale);
                                    context.lineTo(ox3,oy3+3*scale);
                                    context.stroke();
                                    context.closePath();
                                    context.beginPath();//画光标
                                    context.lineWidth = 1;
                                    context.strokeStyle = "#f5e808";
                                    context.arc(ox3, oy3, 3 * scale, 0, 2 * Math.PI);
                                    context.stroke();
                                    context.closePath();
                                    canvas.onmouseup = function (e) {
                                        if (edits.indexOf(dcmIndex-1)==-1){
                                            edits.push(dcmIndex-1);
                                        }
                                        canvas.onmousemove = dragMove1;
                                        // allPositionsInfo[apiIndex].positions = positions;
                                    }
                                }
                            }else if (e.button == 2){
                                subMove(oxDown,oyDown,dxDown,dyDown,canvas,context,$canvas,moveX,moveY,scale,pushMove1,edits);
                            }

                        };
                        throw new Error("end");
                    }else {
                        canvas.onmousedown = function (e) {
                            e = e || window.event;
                            let oxDown = e.clientX - $canvas.offset().left;
                            let oyDown = e.clientY - $canvas.offset().top;
                            oxDown = oxDown - sumx * scale;
                            oyDown = oyDown - sumy * scale;
                            let dxDown = calPositionX(oxDown, moveX, scale);
                            let dyDown = calPositionY(oyDown, moveY, scale);
                            if (e.button == 2){
                                subMove(oxDown,oyDown,dxDown,dyDown,canvas,context,$canvas,moveX,moveY,scale,pushMove1,edits);
                            }
                        };
                        canvas.onmouseup = null;
                    }
                });
            });
        }catch (e) {
            if (e.message != "end") throw e;
        }
    };
    showCircle1 = true;
    if (r>=2){
        canvas.onmousemove = pushMove1;
    }else {
        canvas.onmousemove = dragMove1;
    }

    $("#save_sketch").off("click").click(function () {
        if (allPositionsInfo.length!=0){
            allPositionsInfo.forEach((positionsInfo,infoIndex)=>{
                positionsInfo.positionsArray.forEach((positions,positionsIndex)=>{
                    let tempPositions = [...positions];
                    let add=0;
                    positions.forEach((position,positionIndex)=>{
                        if (position.dragFlag){
                            let position1,position2;
                            if (positionIndex==positions.length-1){
                                position1 = positions[positionIndex-1];
                                position2 = positions[0];
                                add += drawLine(position1.x,position1.y,position.x,position.y,tempPositions,positionIndex-1+add);
                                add += drawLine(position.x,position.y,position2.x,position2.y,tempPositions,positionIndex+add);
                            }else if (positionIndex==0){
                                position1 = positions[positions.length-1];
                                position2 = positions[1];
                                add += drawLine(position.x,position.y,position2.x,position2.y,tempPositions,positionIndex+add);
                                drawLine(position1.x,position1.y,position.x,position.y,tempPositions,tempPositions.length-1);
                            }else {
                                position1 = positions[positionIndex-1];
                                position2 = positions[positionIndex+1];
                                add += drawLine(position1.x,position1.y,position.x,position.y,tempPositions,positionIndex-1+add);
                                add += drawLine(position.x,position.y,position2.x,position2.y,tempPositions,positionIndex+add);
                            }
                        }
                    });
                    allPositionsInfo[infoIndex].positionsArray[positionsIndex] = tempPositions;
                });
            });
            $.ajax({
                type :  "post",
                url : "/VSLC/sequence/saveSketch",
                contentType: "application/json",
                data : JSON.stringify({
                    sequenceID : parseInt($sequenceID),
                    sketchFile : $sketchFile,
                    sketchNum : $sketchNum.toString(),
                    edits : edits,
                    allPositionsInfo : allPositionsInfo
                }),
                success : function (data) {
                    $('#sketchTree').tree('reload');
                    $("#toolsState").text('');
                    toastr.success('保存成功');
                    console.log(allPositionsInfo);
                    allPositionsInfo = [];
                    showCircle1 = false;
                    // context.clearRect(-5000, -5000, 9999, 9999);
                    canvas.onmousedown = null;
                    canvas.onmousemove = null;
                    canvas.onmouseup = null;
                }
            });
        }
    });
});
// document.getElementById('enableDraw').addEventListener('click', function () {
//     clearCornerstoneTools();
//     cornerstoneTools.stackScrollWheel.activate(targetElement);
//     $("#toolsState").text('调整');
//
//     let canvas = $("#aboveCanvas")[0];
//     let $canvas = $("#aboveCanvas");
//     let context = canvas.getContext("2d");
//
//     var canvasPic = new Image();
//     canvasPic.src = canvas.toDataURL();
//     var lines = new Array();
//     var lineId = 0;
//     var line = {
//         id: 0,
//         startX: 0,
//         startY: 0,
//         endX: 0,
//         endY: 0,
//         index1: 0,
//         index2: 0,
//         listIndex: 0
//     };
//     var dragLongPointInfoList = new Array();
//     var originLines = new Array();
//     var lineFlag = 0, dragBesFlag = 0, dragBesIndex = 0, dragPointFlag = 0, dragPointIndex = -1, dragPointNum = -1,
//         dragPointX = 0, dragPointY = 0, dragLongPointFlag = 0, cutFlag = 0;
//     var lastIndex, nextIndex, listIndex, positionIndex1, positionIndex2;
//
//     var positionList = new Array();
//     var pointList = new Array();
//     var oldPointList = new Array();
//     var dragPositionList = new Array();//拖动关键点的临时点集
//     var dragLongPositionList = new Array();
//
//     var besList = new Array();
//
//     function point(x, y) //声明对象
//     {
//         this.x = x;
//         this.y = y;
//     }
//
//     context.beginPath();
//     context.lineWidth = 2;
//     context.strokeStyle = "#F5270B";
//
//     function quadraticBezier(p0, p1, p2, t) {//p0起点，p1控制点，p2终点，t比例系数
//         var k = 1 - t;
//         return k * k * p0 + 2 * (1 - t) * t * p1 + t * t * p2;    // 这个方程就是二次贝赛尔曲线方程
//     }
//
//     function indicateLine(ox, oy) {//判断光标是否在所化直线上，若有则指示
//         for (var i = 0; i < lines.length; i++) {
//             let startX = lines[i].startX;
//             let startY = lines[i].startY;
//             let endX = lines[i].endX;
//             let endY = lines[i].endY;
//             let a = endY - startY;
//             let b = startX - endX;
//             let c = endX * startY - startX * endY;
//             let d = Math.abs((a * ox + b * oy + c) / Math.sqrt(a * a + b * b));
//             let k1 = b / a;
//             let b1 = oy - k1 * ox;
//             let xx = -(b * b1 + c) / (a + b * k1);
//             let yy = k1 * xx + b1;
//
//             let min = startX < endX ? startX : endX;
//             let max = startX > endX ? startX : endX;
//             if (xx >= min && xx <= max && d <= 10) {
//                 context.beginPath();
//                 context.moveTo(xx, yy);
//                 context.lineTo(ox, oy);
//                 context.closePath();
//                 context.stroke();
//                 var point = {"x": xx, "y": yy};
//                 return point;
//             }
//         }
//     }
//
//     function drawCircle(x, y) {
//         context.beginPath();//画光标
//         context.lineWidth = 2;
//         context.strokeStyle = "#F5270B";
//         context.arc(x, y, 5, 0, 2 * Math.PI);
//         context.fillStyle = "green";
//         context.fill();
//         context.stroke();
//         context.closePath();
//     }
//
//     function loadResult() {
//         drawResult(0, 0, originLines);
//         for (var i = 0; i < lines.length; i++) {
//             context.beginPath();
//             context.lineWidth = 2;
//             context.strokeStyle = "#F5270B";
//             var line = lines[i];
//             drawCircle(line.startX, line.startY);//画起始光标
//             drawCircle(line.endX, line.endY);//画结束光标
//             context.arc(line.endX, line.endY, 5, 0, 2 * Math.PI);
//             context.moveTo(line.startX, line.startY);
//             context.lineTo(line.endX, line.endY);
//             context.stroke();
//             context.closePath();
//         }
//         for (var i = 0; i < besList.length; i++) {
//             var bes = besList[i];
//             context.beginPath();//画贝塞尔曲线
//             context.lineWidth = 2;
//             context.strokeStyle = "#F5270B";
//             for (var j = 0; j < bes.positionList.length; j++) {
//                 var position = bes.positionList[j];
//                 if (j == 0) {
//                     context.moveTo(position.x, position.y);
//                 } else {
//                     context.lineTo(position.x, position.y);
//                 }
//             }
//             context.stroke();
//             context.closePath();
//
//
//             for (var k = 0; k < bes.pointList.length; k++) {
//                 var point = bes.pointList[k];
//                 drawCircle(point.x, point.y);
//             }
//
//         }
//         /*明天于下方写加载原有标注结果*/
//     }
//
//     var bulidLine = function (e) {//构造直线
//         e = e || window.event;
//         if (e.button == 0) {
//             var ox = e.clientX - $canvas.offset().left;
//             var oy = e.clientY - $canvas.offset().top;//对ox oy进行适配
//             let viewport = cornerstone.getViewport(targetElement);
//             ox = ox - sumx * viewport.scale;
//             oy = oy - sumy * viewport.scale;
//
//             context.beginPath();
//             context.moveTo(ox, oy);
//
//             lineFlag = 0;
//             indicateLine(ox, oy);
//
//             var tempLine = {
//                 startX: 0,
//                 startY: 0,
//                 endX: 0,
//                 endY: 0
//             };
//
//             tempLine['startX'] = ox;
//             tempLine['startY'] = oy;
//
//             canvas.onmousemove = function (e) {//拖动直线
//                 var ox1 = e.clientX - $canvas.offset().left;
//                 var oy1 = e.clientY - $canvas.offset().top;
//                 // console.log(ox1,oy1);
//                 let viewport = cornerstone.getViewport(targetElement);
//                 // let moveX = (canvas.width-imgWidth*viewport.scale)/2;
//                 // let moveY =(imgHeight*viewport.scale-canvas.height)/2;
//                 ox1 = ox1 - sumx * viewport.scale;
//                 oy1 = oy1 - sumy * viewport.scale;
//
//                 lineFlag = 1;
//                 tempLine['id'] = lineId;
//                 tempLine['endX'] = ox1;
//                 tempLine['endY'] = oy1;
//                 line = tempLine;
//                 // var position = {
//                 //     x : 0,
//                 //     y : 0
//                 // }
//                 // position['x'] = ox1;
//                 // position['y'] = oy1;
//                 // positionList.push(position);
//                 context.clearRect(-5000, -5000, 9999, 9999);
//                 context.drawImage(canvasPic, 0, 0);
//                 loadResult();
//
//                 drawCircle(ox, oy);//画起始光标
//                 drawCircle(ox1, oy1);//画结束光标
//
//                 context.beginPath();//画直线
//                 context.moveTo(ox, oy);
//                 context.lineTo(ox1, oy1);
//                 context.lineWidth = 2;
//                 context.strokeStyle = "#F5270B";
//                 context.stroke();
//                 context.closePath();
//
//             }
//         } else if (e.button == 2) {
//
//         }
//     }
//     canvas.onmousedown = bulidLine;
//     document.onmouseup = function () {
//         if (lineFlag == 1) {//保存直线
//             lines.push(line);
//             lineId++;
//             lineFlag = 0;
//         }
//         if (dragBesFlag == 1) {
//             if (lastIndex == 0) {
//                 besList[dragBesIndex].positionList.splice(lastIndex, nextIndex - lastIndex);
//             } else {
//                 besList[dragBesIndex].positionList.splice(lastIndex + 1, nextIndex - lastIndex);
//             }
//             if (besList[dragBesIndex].positionList.length == 1) {
//                 besList.splice(dragBesIndex, 1);
//             } else {
//                 besList[dragBesIndex].pointList = oldPointList;
//             }
//             dragBesFlag = 0;
//             lastIndex = 0;
//             nextIndex = 0;
//         }
//         if (dragPointFlag == 1) {
//             if (dragPointIndex != -1) {
//                 besList[dragPointIndex].positionList = dragPositionList;
//                 besList[dragPointIndex].pointList[dragPointNum].x = dragPointX;
//                 besList[dragPointIndex].pointList[dragPointNum].y = dragPointY;
//                 dragPointFlag = 0;
//                 dragPointIndex = -1;
//             }
//         }
//         if (dragLongPointFlag == 1) {
//             for (let i = 0; i < dragLongPointInfoList.length; i++) {
//                 besList[dragLongPointInfoList[i].dragLongPointIndex].positionList = dragLongPointInfoList[i].dragLongPositionList;
//                 besList[dragLongPointInfoList[i].dragLongPointIndex].pointList[dragLongPointInfoList[i].dragLongPointNum].x = dragLongPointInfoList[i].dragLongPointX;
//                 besList[dragLongPointInfoList[i].dragLongPointIndex].pointList[dragLongPointInfoList[i].dragLongPointNum].y = dragLongPointInfoList[i].dragLongPointY;
//                 console.log(besList);
//             }
//             dragLongPointFlag = 0;
//         }
//         if (cutFlag == 1) {
//             console.log(cutFlag);
//             let min = positionIndex1 <= positionIndex2 ? positionIndex1 : positionIndex2;
//             let max = positionIndex1 >= positionIndex2 ? positionIndex1 : positionIndex2;
//             console.log(min, max, listIndex);
//             if (max - min + 1 < currentDraw.positionList[listIndex].length / 2) {
//                 currentDraw.positionList[listIndex].splice(min + 1, max - min - 1);
//             } else {
//                 currentDraw.positionList[listIndex].splice(0, min);
//                 currentDraw.positionList[listIndex].splice(max - min + 1, currentDraw.positionList[listIndex].length - max + min + 1);
//                 line.index1 = max - min;
//             }
//             originLines.push(line);
//             cutFlag = 0;
//         }
//         // canvasPic.src = canvas.toDataURL();
//         if (positionList.length != 0) {
//             var bes = {
//                 "positionList": null,
//                 "pointList": null
//             };
//             bes.positionList = positionList;
//             bes.pointList = pointList;
//             besList.push(bes);
//             console.log("存进去" + dragBesIndex + " 总共有" + besList.length, besList);
//             positionList = [];
//             pointList = [];
//         }
//         console.log(lines);
//         console.log(besList);
//         loadResult();
//
//         canvas.onmousemove = function (e) {
//             e = e || window.event;
//             var ox = e.clientX - $canvas.offset().left;
//             var oy = e.clientY - $canvas.offset().top;
//             let viewport = cornerstone.getViewport(targetElement);
//             ox = ox - sumx * viewport.scale;
//             oy = oy - sumy * viewport.scale;
//             var flag1 = false, flag2 = false;
//             for (var i = 0; i < lines.length; i++) {//判断是否可以拖动直线
//                 let startX, startY, endX, endY, a, b, c, d, k1, b1, xx, yy;
//                 let id = lines[i].id;
//                 startX = lines[i].startX;
//                 startY = lines[i].startY;
//                 endX = lines[i].endX;
//                 endY = lines[i].endY;
//                 a = endY - startY;
//                 b = startX - endX;
//                 c = endX * startY - startX * endY;
//                 d = Math.abs((a * ox + b * oy + c) / Math.sqrt(a * a + b * b));
//                 k1 = b / a;
//                 b1 = oy - k1 * ox;
//                 xx = -(b * b1 + c) / (a + b * k1);
//                 yy = k1 * xx + b1;
//
//                 let min = startX < endX ? startX : endX;
//                 let max = startX > endX ? startX : endX;
//                 if (xx >= min && xx <= max && d <= 10) {
//                     context.clearRect(-5000, -5000, 9999, 9999);
//                     loadResult();
//                     drawCircle(xx, yy);
//                     canvas.onmousedown = function (e) {
//                         if (lines.length != 0) {
//                             lines.splice(i, 1);
//                         }
//                         canvas.onmousemove = function (e) {
//                             e = e || window.event;
//                             var ox = e.clientX - $canvas.offset().left;
//                             var oy = e.clientY - $canvas.offset().top;
//                             let viewport = cornerstone.getViewport(targetElement);
//                             ox = ox - sumx * viewport.scale;
//                             oy = oy - sumy * viewport.scale;
//                             lineFlag = 0;
//                             context.clearRect(-5000, -5000, 9999, 9999);
//                             loadResult();
//                             drawCircle(ox, oy);//画光标
//                             drawCircle(startX, startY);//画光标
//                             drawCircle(endX, endY);//画光标
//                             var tempPointList = new Array();
//                             tempPointList.push(new point(Math.round(startX), Math.round(startY)));
//                             tempPointList.push(new point(Math.round(endX), Math.round(endY)));
//                             tempPointList.push(new point(Math.round(ox), Math.round(oy)));
//                             pointList = tempPointList;
//                             context.beginPath();//画贝塞尔曲线
//                             context.lineWidth = 2;
//                             context.strokeStyle = "#F5270B";
//                             context.moveTo(startX, startY);
//                             let x0, y0;
//                             let t = 0.5;
//                             x0 = (ox - (1 - t) * (1 - t) * startX - t * t * endX) / (2 * t * (1 - t));//控制点坐标
//                             y0 = (oy - (1 - t) * (1 - t) * startY - t * t * endY) / (2 * t * (1 - t));
//                             var tempPositionList = new Array();
//                             for (var tempT = 0; tempT <= 100; tempT += 1) {
//                                 var x = Math.round(quadraticBezier(startX, x0, endX, tempT / 100));
//                                 var y = Math.round(quadraticBezier(startY, y0, endY, tempT / 100));
//                                 context.lineTo(x, y);
//                                 var position = {
//                                     x: 0,
//                                     y: 0
//                                 };
//                                 position['x'] = x;
//                                 position['y'] = y;
//                                 tempPositionList.push(position);
//                             }
//                             positionList = tempPositionList;
//
//                             context.stroke();
//                             context.closePath();
//
//                         };
//                         // canvas.onmousedown = bulidLine;
//                     };
//                     flag1 = true;
//                     break;
//                 } else {
//                     // canvas.onmousedown = bulidLine;
//                 }
//             }
//             if (!flag1) {
//                 context.clearRect(-5000, -5000, 9999, 9999);
//                 loadResult();
//             }
//             outer:
//                 for (var m = 0; m < besList.length; m++) {//拖动贝塞尔曲线
//                     var bes = besList[m];
//                     inner:
//                         for (var n = 0; n < bes.positionList.length; n++) {
//                             var position = bes.positionList[n];
//                             if ((ox >= position.x - 5 && ox <= position.x + 5) && (oy >= position.y - 5 && oy <= position.y + 5)) {
//                                 context.clearRect(-5000, -5000, 9999, 9999);
//                                 loadResult();
//                                 drawCircle(position.x, position.y);//画光标
//                                 flag2 = true;
//                                 canvas.onmousedown = function () {
//                                     dragBesIndex = m;
//                                     let nowPositonList = bes.positionList;
//                                     let nowPointList = bes.pointList;
//                                     let dragPoints = new Array();//记录各个关键点索引
//                                     let clickPointIndex;//鼠标点击点索引
//                                     for (let i = 0; i < nowPositonList.length; i++) {
//                                         let nowPosition = nowPositonList[i];
//                                         if (position.x == nowPosition.x && position.y == nowPosition.y) {
//                                             clickPointIndex = i;
//                                         }
//                                         for (let j = 0; j < nowPointList.length; j++) {
//                                             let nowPoint = nowPointList[j];
//                                             if (nowPosition.x == nowPoint.x && nowPosition.y == nowPoint.y) {
//                                                 dragPoints.push(i);
//                                                 break;
//                                             }
//                                         }
//                                     }
//                                     for (let i = 0; i < dragPoints.length - 1; i++) {
//                                         if (clickPointIndex > dragPoints[i] && clickPointIndex < dragPoints[i + 1]) {
//                                             lastIndex = dragPoints[i];
//                                             nextIndex = dragPoints[i + 1];
//                                         }
//                                     }
//
//                                     var tempPointList = new Array();
//                                     tempPointList.push(new point(nowPositonList[nowPositonList.length - 1 - nextIndex].x, nowPositonList[nowPositonList.length - 1 - nextIndex].y));
//                                     tempPointList.push(new point(nowPositonList[nowPositonList.length - 1 - lastIndex].x, nowPositonList[nowPositonList.length - 1 - lastIndex].y));
//                                     oldPointList = tempPointList;
//                                     console.log(besList, m, besList[m].pointList);
//                                     canvas.onmousemove = function (e) {
//                                         e = e || window.event;
//                                         var ox = e.clientX - $canvas.offset().left;
//                                         var oy = e.clientY - $canvas.offset().top;
//                                         let viewport = cornerstone.getViewport(targetElement);
//                                         ox = ox - sumx * viewport.scale;
//                                         oy = oy - sumy * viewport.scale;
//                                         let x0, y0, startX, startY, endX, endY;
//                                         let t = 0.5;
//                                         startX = nowPositonList[lastIndex].x;
//                                         startY = nowPositonList[lastIndex].y;
//                                         endX = nowPositonList[nextIndex].x;
//                                         endY = nowPositonList[nextIndex].y;
//                                         x0 = (ox - (1 - t) * (1 - t) * startX - t * t * endX) / (2 * t * (1 - t));//控制点坐标
//                                         y0 = (oy - (1 - t) * (1 - t) * startY - t * t * endY) / (2 * t * (1 - t));
//                                         context.clearRect(-5000, -5000, 9999, 9999);
//                                         loadResult();
//                                         drawCircle(ox, oy);//画光标
//                                         context.beginPath();
//                                         context.lineWidth = 2;
//                                         context.strokeStyle = "#F5270B";
//                                         context.moveTo(startX, startY);
//                                         var tempPositionList = new Array();
//                                         for (var tempT = 0; tempT <= 100; tempT += 1) {
//                                             var position = {
//                                                 x: 0,
//                                                 y: 0
//                                             };
//                                             if (tempT == 100) {
//                                                 position['x'] = endX;
//                                                 position['y'] = endY;
//                                                 context.lineTo(endX, endY);
//                                             } else {
//                                                 var x = Math.round(quadraticBezier(startX, x0, endX, tempT / 100));
//                                                 var y = Math.round(quadraticBezier(startY, y0, endY, tempT / 100));
//                                                 context.lineTo(x, y);
//                                                 position['x'] = x;
//                                                 position['y'] = y;
//                                             }
//                                             tempPositionList.push(position);
//                                         }
//                                         positionList = tempPositionList;
//                                         var newTempPointList = new Array();
//                                         newTempPointList.push(new point(startX, startY));
//                                         newTempPointList.push(new point(endX, endY));
//                                         newTempPointList.push(new point(Math.round(ox), Math.round(oy)));
//                                         pointList = newTempPointList;
//                                         context.stroke();
//                                         context.closePath();
//
//                                         dragBesFlag = 1;
//                                         // canvas.onmousedown = bulidLine;
//                                     };
//                                 };
//                                 break outer;
//                             } else {
//                                 if (!flag1) {
//                                     // canvas.onmousedown = bulidLine;
//                                 }
//                             }
//                         }
//                 }
//             var shortPoints = new Array();
//             var longs = new Array();
//             var longPoints = new Array();
//             var dragBeginShort = false, dragCtrlShort = false, dragEndShort = false, dragBeginLong = false,
//                 dragCtrlLong = false, dragEndLong = false;
//             outer:
//                 for (var p = 0; p < besList.length; p++) {//拖动点
//                     var dragBes = besList[p];
//                     inner:
//                         for (var q = 0; q < dragBes.pointList.length; q++) {
//                             var dragPoint = dragBes.pointList[q];
//                             if ((ox >= dragPoint.x - 5 && ox <= dragPoint.x + 5) && (oy >= dragPoint.y - 5 && oy <= dragPoint.y + 5)) {
//                                 if (dragBes.pointList.length == 2) {
//                                     if (q == 0) {//拖起点
//                                         if (besList[p + 1].pointList[0].x != dragBes.pointList[1].x) {
//                                             var start = new point(besList[p + 1].pointList[0].x, besList[p + 1].pointList[0].y);
//                                             var end = new point(dragBes.pointList[1].x, dragBes.pointList[1].y);
//                                             var ctrl = new point(ox, oy);
//                                             shortPoints = [];
//                                             shortPoints.push(start);
//                                             shortPoints.push(end);
//                                             shortPoints.push(ctrl);
//                                             shortPoints.push(p);
//                                             shortPoints.push(q);
//                                             console.log(shortPoints);//另一端点即终点
//                                             dragCtrlShort = true;
//                                         } else {
//                                             var start = new point(ox, oy);
//                                             var end = new point(besList[p + 1].pointList[1].x, besList[p + 1].pointList[1].y);
//                                             var ctrl = new point(dragBes.pointList[1].x, dragBes.pointList[1].y);
//                                             shortPoints = [];
//                                             shortPoints.push(start);
//                                             shortPoints.push(end);
//                                             shortPoints.push(ctrl);
//                                             shortPoints.push(p);
//                                             shortPoints.push(q);
//                                             dragBeginShort = true;
//                                         }
//                                         break inner;
//                                     } else if (q == 1) {//相当于拖控制点
//                                         if (besList[p + 1].pointList[1].x != dragBes.pointList[0].x) {
//                                             console.log(besList[p + 1].pointList[1]);
//                                             var start = new point(dragBes.pointList[0].x, dragBes.pointList[0].y);
//                                             var end = new point(besList[p + 1].pointList[1].x, besList[p + 1].pointList[1].y);
//                                             var ctrl = new point(dragBes.pointList[1].x, dragBes.pointList[1].y);
//                                             shortPoints = [];
//                                             shortPoints.push(start);
//                                             shortPoints.push(end);
//                                             shortPoints.push(ctrl);
//                                             shortPoints.push(p);
//                                             shortPoints.push(q);
//                                             dragCtrlShort = true;
//                                         } else {
//                                             console.log(besList[p + 1].pointList[0]);
//                                             var start = new point(besList[p + 1].pointList[0].x, besList[p + 1].pointList[0].y);
//                                             var end = new point(dragBes.pointList[1].x, dragBes.pointList[1].y);
//                                             var ctrl = new point(dragBes.pointList[0].x, dragBes.pointList[0].y);
//                                             shortPoints = [];
//                                             shortPoints.push(start);
//                                             shortPoints.push(end);
//                                             shortPoints.push(ctrl);
//                                             shortPoints.push(p);
//                                             shortPoints.push(q);
//                                             dragEndShort = true;
//                                         }
//                                     }
//                                     break inner;
//                                 } else if (dragBes.pointList.length == 3) {
//                                     if (q == 0) {//拖起点
//                                         var start = new point(ox, oy);
//                                         var end = new point(dragBes.pointList[1].x, dragBes.pointList[1].y);
//                                         var ctrl = new point(dragBes.pointList[2].x, dragBes.pointList[2].y);
//                                         longPoints = [];
//                                         longPoints.push(start);
//                                         longPoints.push(end);
//                                         longPoints.push(ctrl);
//                                         longPoints.push(p);
//                                         longPoints.push(q);
//                                         dragBeginLong = true;
//                                     } else if (q == 1) {//拖终点
//                                         var start = new point(dragBes.pointList[0].x, dragBes.pointList[0].y);
//                                         var end = new point(ox, oy);
//                                         var ctrl = new point(dragBes.pointList[2].x, dragBes.pointList[2].y);
//                                         longPoints = [];
//                                         longPoints.push(start);
//                                         longPoints.push(end);
//                                         longPoints.push(ctrl);
//                                         longPoints.push(p);
//                                         longPoints.push(q);
//                                         dragEndLong = true;
//                                     } else {//拖控制点
//                                         var start = new point(dragBes.pointList[0].x, dragBes.pointList[0].y);
//                                         var end = new point(dragBes.pointList[1].x, dragBes.pointList[1].y);
//                                         var ctrl = new point(ox, oy);
//                                         longPoints = [];
//                                         longPoints.push(start);
//                                         longPoints.push(end);
//                                         longPoints.push(ctrl);
//                                         longPoints.push(p);
//                                         longPoints.push(q);
//                                         dragCtrlLong = true;
//                                     }
//                                     longs.push(longPoints);
//                                     break inner;
//                                 }
//                             }
//                         }
//                 }
//             // console.log(dragBeginShort,dragCtrlShort,dragEndShort,dragBeginLong,dragCtrlLong,dragEndLong);
//             // console.log(shortPoints,longPoints);
//             // console.log(longs);
//             if (dragBeginShort || dragCtrlShort || dragEndShort || dragBeginLong || dragCtrlLong || dragEndLong) {
//                 canvas.onmousedown = function () {//7.21
//                     canvas.onmousemove = function (e) {
//                         e = e || window.event;
//                         var ox = e.clientX - $canvas.offset().left;
//                         var oy = e.clientY - $canvas.offset().top;
//                         let viewport = cornerstone.getViewport(targetElement);
//                         ox = ox - sumx * viewport.scale;
//                         oy = oy - sumy * viewport.scale;
//                         context.clearRect(-5000, -5000, 9999, 9999);
//                         loadResult();
//                         drawCircle(ox, oy);//画光标
//                         context.beginPath();
//                         context.lineWidth = 2;
//                         context.strokeStyle = "#F5270B";
//                         let t = 0.5, cx, cy, lcx, lcy;
//                         if (dragBeginShort) {
//                             cx = (shortPoints[2].x - (1 - t) * (1 - t) * ox - t * t * shortPoints[1].x) / (2 * t * (1 - t));//控制点坐标
//                             cy = (shortPoints[2].y - (1 - t) * (1 - t) * oy - t * t * shortPoints[1].y) / (2 * t * (1 - t));
//                             context.moveTo(ox, oy);
//                         } else if (dragEndShort) {
//                             cx = (shortPoints[2].x - (1 - t) * (1 - t) * shortPoints[0].x - t * t * ox) / (2 * t * (1 - t));//控制点坐标
//                             cy = (shortPoints[2].y - (1 - t) * (1 - t) * shortPoints[0].y - t * t * oy) / (2 * t * (1 - t));
//                             context.moveTo(shortPoints[2].x, shortPoints[2].y);
//                         } else if (dragCtrlShort) {
//                             cx = (ox - (1 - t) * (1 - t) * shortPoints[0].x - t * t * shortPoints[1].x) / (2 * t * (1 - t));//控制点坐标
//                             cy = (oy - (1 - t) * (1 - t) * shortPoints[0].y - t * t * shortPoints[1].y) / (2 * t * (1 - t));
//                             if (shortPoints[4] == 0) {
//                                 context.moveTo(shortPoints[2].x, shortPoints[2].y);
//                             } else {
//                                 context.moveTo(shortPoints[0].x, shortPoints[0].y);
//                             }
//                         }
//                         if (dragBeginShort || dragEndShort || dragCtrlShort) {
//                             var tempDragPositionList1 = new Array();
//                             for (var tempT = 0; tempT <= 50; tempT += 1) {
//                                 var position = {
//                                     x: 0,
//                                     y: 0
//                                 };
//                                 if (tempT == 50) {
//                                     let px, py;
//                                     if (dragBeginShort) {
//                                         px = shortPoints[2].x;
//                                         py = shortPoints[2].y;
//                                     } else if (dragEndShort) {
//                                         px = Math.round(ox);
//                                         py = Math.round(oy);
//                                     } else if (dragCtrlShort) {
//                                         if (shortPoints[4] == 0) {
//                                             px = shortPoints[1].x;
//                                             py = shortPoints[1].y;
//                                         } else {
//                                             px = Math.round(ox);
//                                             py = Math.round(oy);
//                                         }
//                                     }
//                                     position['x'] = px;
//                                     position['y'] = py;
//                                     context.lineTo(px, py);
//                                 } else {
//                                     let x, y;
//                                     if (dragBeginShort) {
//                                         x = Math.round(quadraticBezier(ox, cx, shortPoints[1].x, tempT / 100));
//                                         y = Math.round(quadraticBezier(oy, cy, shortPoints[1].y, tempT / 100));
//                                     } else if (dragEndShort) {
//                                         x = Math.round(quadraticBezier(shortPoints[0].x, cx, ox, (tempT + 50) / 100));
//                                         y = Math.round(quadraticBezier(shortPoints[0].y, cy, oy, (tempT + 50) / 100));
//                                     } else if (dragCtrlShort) {
//                                         if (shortPoints[4] == 0) {
//                                             x = Math.round(quadraticBezier(shortPoints[0].x, cx, shortPoints[1].x, (tempT + 50) / 100));
//                                             y = Math.round(quadraticBezier(shortPoints[0].y, cy, shortPoints[1].y, (tempT + 50) / 100));
//                                         } else {
//                                             x = Math.round(quadraticBezier(shortPoints[0].x, cx, shortPoints[1].x, tempT / 100));
//                                             y = Math.round(quadraticBezier(shortPoints[0].y, cy, shortPoints[1].y, tempT / 100));
//                                         }
//                                     }
//                                     context.lineTo(x, y);
//                                     position['x'] = x;
//                                     position['y'] = y;
//                                 }
//                                 tempDragPositionList1.push(position);
//                             }
//                             context.stroke();
//                             context.closePath();
//                             /*储存图像信息*/
//                             dragPointFlag = 1;
//                             dragPointIndex = shortPoints[3];
//                             dragPointNum = shortPoints[4];
//                             dragPointX = Math.round(ox);
//                             dragPointY = Math.round(oy);
//                             dragPositionList = tempDragPositionList1;
//                         }
//
//                         if (dragBeginLong || dragEndLong || dragCtrlLong) {
//                             var tempDragLongPointInfoList = new Array();
//                             for (let k = 0; k < longs.length; k++) {//问题出在拖动点的不同 起始点与结束点
//                                 let tempLongPoints = longs[k];
//                                 context.beginPath();
//                                 context.lineWidth = 2;
//                                 context.strokeStyle = "#F5270B";
//                                 if (tempLongPoints[4] == 0) {
//                                     lcx = (tempLongPoints[2].x - (1 - t) * (1 - t) * ox - t * t * tempLongPoints[1].x) / (2 * t * (1 - t));//控制点坐标
//                                     lcy = (tempLongPoints[2].y - (1 - t) * (1 - t) * oy - t * t * tempLongPoints[1].y) / (2 * t * (1 - t));
//                                     context.moveTo(ox, oy);
//                                 } else if (tempLongPoints[4] == 1) {
//                                     lcx = (tempLongPoints[2].x - (1 - t) * (1 - t) * tempLongPoints[0].x - t * t * ox) / (2 * t * (1 - t));//控制点坐标
//                                     lcy = (tempLongPoints[2].y - (1 - t) * (1 - t) * tempLongPoints[0].y - t * t * oy) / (2 * t * (1 - t));
//                                     context.moveTo(tempLongPoints[0].x, tempLongPoints[0].y);
//                                 } else if (tempLongPoints[4] == 2) {
//                                     lcx = (ox - (1 - t) * (1 - t) * tempLongPoints[0].x - t * t * tempLongPoints[1].x) / (2 * t * (1 - t));//控制点坐标
//                                     lcy = (oy - (1 - t) * (1 - t) * tempLongPoints[0].y - t * t * tempLongPoints[1].y) / (2 * t * (1 - t));
//                                     context.moveTo(tempLongPoints[0].x, tempLongPoints[0].y);
//                                 }
//                                 var tempDragPositionList2 = new Array();
//                                 for (var tempT = 0; tempT <= 100; tempT += 1) {
//                                     var position = {
//                                         x: 0,
//                                         y: 0
//                                     };
//                                     if (tempT == 100) {
//                                         let px, py;
//                                         if (tempLongPoints[4] == 0) {
//                                             px = tempLongPoints[1].x;
//                                             py = tempLongPoints[1].y;
//                                         } else if (tempLongPoints[4] == 1) {
//                                             px = Math.round(ox);
//                                             py = Math.round(oy);
//                                         } else if (tempLongPoints[4] == 2) {
//                                             px = tempLongPoints[1].x;
//                                             py = tempLongPoints[1].y;
//                                         }
//                                         position['x'] = px;
//                                         position['y'] = py;
//                                         context.lineTo(px, py);
//                                     } else {
//                                         let x, y;
//                                         if (tempLongPoints[4] == 0) {
//                                             x = Math.round(quadraticBezier(ox, lcx, tempLongPoints[1].x, tempT / 100));
//                                             y = Math.round(quadraticBezier(oy, lcy, tempLongPoints[1].y, tempT / 100));
//                                         } else if (tempLongPoints[4] == 1) {
//                                             x = Math.round(quadraticBezier(tempLongPoints[0].x, lcx, ox, tempT / 100));
//                                             y = Math.round(quadraticBezier(tempLongPoints[0].y, lcy, oy, tempT / 100));
//                                         } else if (tempLongPoints[4] == 2) {
//                                             x = Math.round(quadraticBezier(tempLongPoints[0].x, lcx, tempLongPoints[1].x, tempT / 100));
//                                             y = Math.round(quadraticBezier(tempLongPoints[0].y, lcy, tempLongPoints[1].y, tempT / 100));
//                                         }
//                                         context.lineTo(x, y);
//                                         position['x'] = x;
//                                         position['y'] = y;
//                                     }
//                                     tempDragPositionList2.push(position);
//                                 }
//                                 context.stroke();
//                                 context.closePath();
//
//                                 /*储存图像信息*/
//                                 var dragLongPointInfo = {
//                                     dragLongPointIndex: tempLongPoints[3],
//                                     dragLongPointNum: tempLongPoints[4],
//                                     dragLongPointX: Math.round(ox),
//                                     dragLongPointY: Math.round(oy),
//                                     dragLongPositionList: tempDragPositionList2
//                                 };
//                                 tempDragLongPointInfoList.push(dragLongPointInfo);
//                                 dragLongPointFlag = 1;
//                             }
//                             dragLongPointInfoList = tempDragLongPointInfoList;
//                         }
//                     }
//                 };
//             } else {
//                 // if (!flag1&&flag2){
//                 //     canvas.onmousedown = bulidLine;
//                 // }
//             }
//             dragOrigin(viewport, ox, oy);
//         }
//     }
//     canvas.onmousemove = function (e) {
//         e = e || window.event;
//         var ox = e.clientX - $canvas.offset().left;
//         var oy = e.clientY - $canvas.offset().top;
//         let viewport = cornerstone.getViewport(targetElement);
//         ox = ox - sumx * viewport.scale;
//         oy = oy - sumy * viewport.scale;
//         /*拖动原图像*/
//         context.clearRect(-5000, -5000, 9999, 9999);
//         loadResult();
//         dragOrigin(viewport, ox, oy);
//     };
//
//     let clickFlag = 0;
//     let resetClickFlag;
//     document.onkeydown = function (event) {//双击储存修改标注结果
//         var e = event || window.event || arguments.callee.caller.arguments[0];
//         if (e && e.keyCode == 83) {
//             clearTimeout(resetClickFlag);
//             clickFlag++;
//         }
//         if (clickFlag == 2) {
//             console.log("doubleClick");
//             console.log(currentDraw, besList);
//             let viewport = cornerstone.getViewport(targetElement);
//             let scale = viewport.scale;
//             let moveX = (canvas.width - imgWidth * scale) / 2;
//             let moveY = (imgHeight * scale - canvas.height) / 2;
//             for (let m = 0; m < originLines.length; m++) {
//                 let startX, startY, endX, endY, index1, index2, listIndex;
//                 startX = calPositionX(originLines[m].startX, moveX, scale);
//                 startY = calPositionY(originLines[m].startY, moveY, scale);
//                 endX = calPositionX(originLines[m].endX, moveX, scale);
//                 endY = calPositionY(originLines[m].endY, moveY, scale);
//                 index1 = originLines[m].index1;
//                 index2 = originLines[m].index2;
//                 console.log("index1:" + index1, "index2:" + index2);
//                 listIndex = originLines[m].listIndex;
//                 let add = 0;
//                 let startIndex, endIndex;
//                 for (let i = 0; i < besList.length; i++) {
//                     for (let j = 0; j < besList[i].pointList.length; j++) {
//                         let pointX = calPositionX(besList[i].pointList[j].x, moveX, scale);
//                         let pointY = calPositionY(besList[i].pointList[j].y, moveY, scale);
//                         if ((pointX == startX && pointY == startY) || (pointX == endX && pointY == endY)) {
//                             if (pointX == startX) {
//                                 startIndex = i;
//                             } else if (pointX == endX) {
//                                 endIndex = i;
//                             }
//                             break;
//                         }
//                     }
//                 }
//                 // console.log(startIndex,endIndex);
//                 // console.log(besList);
//                 // console.log(currentDraw);
//                 if (startIndex <= endIndex) {
//                     let n = startIndex;
//                     let length = besList[n].positionList.length;
//                     for (let k = 0; k < length; k++) {
//                         let point = {
//                             num: null,
//                             x: (besList[n].positionList[k].x - moveX) / scale,
//                             y: (besList[n].positionList[k].y + moveY) / scale
//                         };
//
//                         currentDraw.positionList[listIndex].splice(Math.min(index1, index2) + 1 + add, 0, point);
//                         add++;
//                     }
//                     let tempIndex = n;
//                     for (let h = 0; h < besList.length; h++) {
//                         let nextBesList = besList[h];
//                         console.log(h);
//                         if ((besList[tempIndex].positionList[besList[tempIndex].positionList.length - 1].x >= nextBesList.positionList[0].x - 1) && (besList[tempIndex].positionList[besList[tempIndex].positionList.length - 1].x <= nextBesList.positionList[0].x + 1)) {
//                             console.log(h, nextBesList.positionList[0].x);
//                             for (let p = 0; p < nextBesList.positionList.length; p++) {
//                                 let point = {
//                                     num: null,
//                                     x: (nextBesList.positionList[p].x - moveX) / scale,
//                                     y: (nextBesList.positionList[p].y + moveY) / scale
//                                 };
//                                 currentDraw.positionList[listIndex].splice(Math.min(index1, index2) + 1 + add, 0, point);
//                                 add++;
//                             }
//                             tempIndex = h;
//                             h = -1;
//                         }
//                     }
//                 } else {
//                     let n = endIndex;
//                     let tempIndex = n;
//                     for (let h = besList.length - 1; h >= 0; h--) {
//                         let nextBesList = besList[h];
//                         console.log(h);
//                         if ((besList[tempIndex].positionList[0].x >= nextBesList.positionList[nextBesList.positionList.length - 1].x - 1) && (besList[tempIndex].positionList[0].x <= nextBesList.positionList[nextBesList.positionList.length - 1].x + 1)) {
//                             console.log(h, nextBesList.positionList[0].x);
//                             for (let p = nextBesList.positionList.length - 1; p >= 0; p--) {
//                                 let point = {
//                                     num: null,
//                                     x: (nextBesList.positionList[p].x - moveX) / scale,
//                                     y: (nextBesList.positionList[p].y + moveY) / scale
//                                 };
//                                 currentDraw.positionList[listIndex].splice(Math.min(index1, index2) + 1, 0, point);
//                             }
//                             add += nextBesList.positionList.length;
//                             tempIndex = h;
//                             h = besList.length;
//                         }
//                     }
//                     let length = besList[n].positionList.length;
//                     for (let k = length - 1; k >= 0; k--) {
//                         let point = {
//                             num: null,
//                             x: (besList[n].positionList[k].x - moveX) / scale,
//                             y: (besList[n].positionList[k].y + moveY) / scale
//                         };
//                         currentDraw.positionList[listIndex].splice(Math.min(index1, index2) + 1 + add, 0, point);
//                     }
//                 }
//             }
//         }
//         resetClickFlag = setTimeout(function () {
//             clickFlag = 0;
//         }, 500);
//
//     };
//
//
//     function dragOrigin(viewport, ox, oy) {
//         let moveX = (canvas.width - imgWidth * viewport.scale) / 2;
//         let moveY = (imgHeight * viewport.scale - canvas.height) / 2;
//         let scale = viewport.scale;
//         try {
//             currentDraw.positionList.forEach((outline, tempListIndex) => {
//                 outline.forEach((position, tempPositionIndex1) => {
//                     let x = position.x * scale + moveX;
//                     let y = position.y * scale - moveY;
//                     if ((ox >= x - 5 && ox <= x + 5) && (oy >= y - 5 && oy <= y + 5) && $sketchNum == position.num) {
//                         drawCircle(x, y);
//                         canvas.onmousedown = function () {
//                             //这里加判断
//
//                             canvas.onmousemove = function (e) {
//                                 e = e || window.event;
//                                 var ox1 = e.clientX - $canvas.offset().left;
//                                 var oy1 = e.clientY - $canvas.offset().top;
//                                 ox1 = ox1 - sumx * viewport.scale;
//                                 oy1 = oy1 - sumy * viewport.scale;
//                                 context.clearRect(-5000, -5000, 9999, 9999);
//                                 loadResult();
//                                 context.beginPath();
//                                 context.lineWidth = 2;
//                                 context.strokeStyle = "#F5270B";
//                                 drawCircle(ox, oy);
//                                 context.moveTo(ox, oy);
//                                 context.lineTo(ox1, oy1);
//                                 context.stroke();
//                                 context.closePath();
//                                 try {
//                                     outline.forEach((position, tempPositionIndex2) => {
//                                         let x1 = position.x * scale + moveX;
//                                         let y1 = position.y * scale - moveY;
//                                         if ((ox1 >= x1 - 5 && ox1 <= x1 + 5) && (oy1 >= y1 - 5 && oy1 <= y1 + 5)) {
//                                             drawCircle(x1, y1);
//                                             lineFlag = 1;
//                                             var tempLine = {
//                                                 startX: 0,
//                                                 startY: 0,
//                                                 endX: 0,
//                                                 endY: 0
//                                             };
//                                             tempLine['startX'] = x;
//                                             tempLine['startY'] = y;
//                                             tempLine['id'] = lineId;
//                                             tempLine['endX'] = x1;
//                                             tempLine['endY'] = y1;
//                                             tempLine['index1'] = tempPositionIndex1;
//                                             tempLine['index2'] = tempPositionIndex2;
//                                             tempLine['listIndex'] = tempListIndex;
//                                             line = tempLine;
//
//                                             cutFlag = 1;
//                                             listIndex = tempListIndex;
//                                             positionIndex1 = tempPositionIndex1;
//                                             positionIndex2 = tempPositionIndex2;
//                                             throw new Error("end");
//                                         } else {
//                                             lineFlag = 0;
//                                             cutFlag = 0;
//                                         }
//                                     })
//                                 } catch (e) {
//                                     if (e.message != "end") throw e;
//                                 }
//                             }
//                         };
//                         throw new Error("end");
//                     }
//                 });
//             });
//         } catch (e) {
//             if (e.message != "end") throw e;
//         }
//     }
// });

document.getElementById('enablePlay').addEventListener('click', function () {
    activate("enablePlay");
    autoDisplay();
});

function activate(id) {
    document.querySelectorAll('a').forEach(function (elem) {
        elem.classList.remove('active');
    });
    document.getElementById(id).classList.add('active');
}

function clearCornerstoneTools() {
    cornerstoneTools.pan.deactivate(targetElement);
    cornerstoneTools.zoomWheel.deactivate(targetElement);
    cornerstoneTools.length.deactivate(targetElement);
    cornerstoneTools.wwwc.deactivate(targetElement);
}

function subMove(ox3,oy3,dx3,dy3,canvas,context,$canvas,moveX,moveY,scale,pushMove,edits) {
    let subPositions = [];
    let isAlive = false;
    let apiIndex;
    for (let i=0;i<allPositionsInfo.length;i++){
        if (allPositionsInfo[i].index==dcmIndex-1){
            isAlive = true;
            apiIndex = i;
            break;
        }
    }
    let startX = ox3, startY = oy3;
    context.beginPath();
    context.strokeStyle = "#0800ff";
    context.lineWidth = scale;
    context.moveTo(ox3, oy3);
    let position = {};
    position.num = null;
    position.x = dx3;
    position.y = dy3;
    subPositions.push(position);
    let frontX = ox3, frontY = oy3;
    canvas.onmousemove = function (e) {
        e = e || window.event;
        var ox4 = e.clientX - $canvas.offset().left;
        var oy4 = e.clientY - $canvas.offset().top;
        ox4 = ox4 - sumx * scale;
        oy4 = oy4 - sumy * scale;

        let dx4 = calPositionX(ox4, moveX, scale);
        let dy4 = calPositionY(oy4, moveY, scale);
        if (!(subPositions[subPositions.length - 1].x == dx4 && subPositions[subPositions.length - 1].y == dy4)) {
            getLineAllPoint(frontX, frontY, ox4, oy4, moveX, moveY, scale, subPositions);
        }
        frontX = ox4;
        frontY = oy4;
        context.lineTo(dx4 * scale + moveX, dy4 * scale - moveY);
        context.stroke();
    };
    canvas.onmouseup = function () {
        // getLineAllPoint(frontX, frontY, ox3, oy3, moveX, moveY, scale, subPositions); //蓝线首尾相连插值
        context.lineTo(startX, startY);
        context.stroke();
        context.closePath();
        let crossPointsArray = [];
        let lastSi=0;
        let tempPositionsArray = [];
        positionsArray.forEach((positions,positionsIndex)=>{
            let crossPoints = [];
            let tempPositions = [...positions];
            let add = 0;
            for (let i=0;i<positions.length;i++){
                let position1,position2;
                if (i==positions.length-1){
                    position1 = positions[i];
                    position2 = positions[0];
                }else {
                    position1 = positions[i];
                    position2 = positions[i+1];
                }
                add += drawLine(position1.x,position1.y,position2.x,position2.y,tempPositions,i+add);
            }
            tempPositionsArray.push(tempPositions);
            tempPositions.forEach((p, pi) => {
                subPositions.forEach((sp, si) => {
                    if (Math.abs(si-lastSi)>2||lastSi==0){
                        if (p.x==sp.x&&p.y==sp.y){
                            lastSi=si;
                            let crossPoint = {
                                x : p.x,
                                y : p.y,
                                pi : pi,
                                si : si,
                            };
                            crossPoints.push(crossPoint);
                        }
                    }
                });
            });
            if (crossPoints.length<2){
                crossPoints = [];
                tempPositions.forEach((p, pi) => {
                    subPositions.forEach((sp, si) => {
                        if (Math.abs(si-lastSi)>2||lastSi==0){
                            if (p.x==sp.x&&(p.y>=sp.y-1&&p.y<=sp.y+1)){
                                lastSi=si;
                                let crossPoint = {
                                    x : p.x,
                                    y : p.y,
                                    pi : pi,
                                    si : si,
                                };
                                crossPoints.push(crossPoint);
                            }
                        }
                    });
                });
            }
            crossPointsArray.push(crossPoints);
        });
        crossPointsArray.forEach((crossPoints,crossPointsIndex)=>{
            let positions = tempPositionsArray[crossPointsIndex];
            if (crossPoints.length>=2){
                let fp = crossPoints[0],lp = crossPoints[crossPoints.length-1];
                if (fp.pi<=lp.pi){
                    if ((fp.pi-lp.pi+positions.length+1)<=positions.length/2){
                        positions.splice(lp.pi,positions.length-lp.pi);
                        positions.splice(0,fp.pi+1);
                        if (fp.si<=lp.si){
                            for (let j=lp.si;j>=fp.si;j--){
                                positions.push(subPositions[j]);
                            }
                        }else {
                            for (let j=lp.si;j<=fp.si;j++){
                                positions.push(subPositions[j]);
                            }
                        }
                    }else {
                        positions.splice(fp.pi,Math.abs(fp.pi-lp.pi)+1);
                        let add=0;
                        if (fp.si<=lp.si){
                            for (let j=fp.si;j<=lp.si;j++){
                                positions.splice(fp.pi+add,0,subPositions[j]);
                                add++;
                            }
                        }else {
                            for (let j=fp.si;j>=lp.si;j--){
                                positions.splice(fp.pi+add,0,subPositions[j]);
                                add++;
                            }
                        }
                    }
                }else {
                    if ((lp.pi-fp.pi+positions.length+1)<=positions.length/2){
                        positions.splice(fp.pi,positions.length-lp.pi);
                        positions.splice(0,lp.pi+1);
                        if (lp.si<=fp.si){
                            for (let j=fp.si;j>=lp.si;j--){
                                positions.push(subPositions[j])
                            }
                        }else {
                            for (let j=fp.si;j<=lp.si;j++){
                                positions.push(subPositions[j]);
                            }
                        }
                    }else {
                        positions.splice(lp.pi,Math.abs(fp.pi-lp.pi)+1);
                        let add=0;
                        if (fp.si<=lp.si){
                            for (let j=fp.si;j<=lp.si;j++){
                                positions.splice(lp.pi+add,0,subPositions[j]);
                                add++;
                            }
                        }else {
                            for (let j=fp.si;j>=lp.si;j--){
                                positions.splice(fp.pi+add,0,subPositions[j]);
                                add++;
                            }
                        }
                    }
                }
            }
        });
        positionsArray = tempPositionsArray;
        if (isAlive){
            if (currentDraw){
                currentResult[dcmIndex - 1].positionList = positionsArray;
            }
            allPositionsInfo[apiIndex].positionsArray = positionsArray;
            if (edits.indexOf(dcmIndex-1)==-1){
                edits.push(dcmIndex-1);
            }
        }
        context.clearRect(-5000, -5000, 9999, 9999);
        drawResult(0,0);
        canvas.onmousemove = pushMove;
    }
}

//中点画线法
function drawLine(x1,y1,x2,y2,positions,insert){
    let x,y,d,d1,d2,a,b,cx,cy,add;
    x=x1;
    y=y1;
    a=y1-y2;          //直线方程中的a的算法
    b=x2-x1;          //直线方程中的b的算法
    cx = (b >= 0 ? 1 : (b = -b, -1));
    cy = (a <= 0 ? 1 : (a = -a, -1));
    add=0;
    if (-a <= b)		// 斜率绝对值 <= 1
    {
        d = 2 * a + b;
        d1 = 2 * a;
        d2 = 2 * (a + b);
        while(x != x2)
        {
            if (d < 0)
                y += cy, d += d2;
            else
                d += d1;
            let position = {
                x : x,
                y : y,
                x1 : x1,
                y1 : y1,
                x2 : x2,
                y2 : y2
            };
            if (x!=x1&&x!=x2){
                positions.splice(insert+1+add,0,position);
                add++;
            }
            x += cx;
        }
    }
    else				// 斜率绝对值 > 1
    {
        d = 2 * b + a;
        d1 = 2 * b;
        d2 = 2 * (a + b);
        while(y != y2)
        {
            if(d < 0)
                d += d1;
            else
                x += cx, d += d2;
            let position = {
                x : x,
                y : y,
                x1 : x1,
                y1 : y1,
                x2 : x2,
                y2 : y2
            };
            if (y!=y1&&y!=y2){
                positions.splice(insert+1+add,0,position);
                add++;
            }
            y += cy;
        }
    }
    return add;
}

//计算直线上的点集
function getLineAllPoint(startX, startY, endX, endY, moveX, moveY, scale, positions) {
    let x1 = Math.round((startX - moveX) / scale);
    let y1 = Math.round((startY + moveY) / scale);
    let x2 = Math.round((endX - moveX) / scale);
    let y2 = Math.round((endY + moveY) / scale);
    let k = (y2 - y1) / (x2 - x1);
    let b = y1 - (k * x1);
    let minX = x1 <= x2 ? x1 : x2;
    let maxX = x1 >= x2 ? x1 : x2;
    let minY = y1 <= y2 ? y1 : y2;
    let maxY = y1 >= y2 ? y1 : y2;
    if (x1 == x2) {
        if (y1 < y2) {
            for (let i = y1; i <= y2; i++) {
                if (!(positions[positions.length - 1].x == x1 && positions[positions.length - 1].y == i)) {
                    let position = new Object();
                    position.num = 0;
                    position.x = x1;
                    position.y = i;
                    positions.push(position);
                }
            }
        } else {
            for (let i = y1; i >= y2; i--) {
                if (!(positions[positions.length - 1].x == x1 && positions[positions.length - 1].y == i)) {
                    let position = new Object();
                    position.num = 0;
                    position.x = x1;
                    position.y = i;
                    positions.push(position);
                }
            }
        }
    } else if (maxX - minX >= maxY - minY) {
        if (x1 < x2) {
            for (let i = x1; i <= x2; i++) {
                let y = Math.round(k * i + b);
                if (!(positions[positions.length - 1].x == i && positions[positions.length - 1].y == y)) {
                    let position = new Object();
                    position.num = 0;
                    position.x = i;
                    position.y = y;
                    positions.push(position);
                }
            }
        } else {
            for (let i = x1; i >= x2; i--) {
                let y = Math.round(k * i + b);
                if (!(positions[positions.length - 1].x == i && positions[positions.length - 1].y == y)) {
                    let position = new Object();
                    position.num = 0;
                    position.x = i;
                    position.y = y;
                    positions.push(position);
                }
            }
        }
    } else if (maxX - minX < maxY - minY) {
        if (y1 < y2) {
            for (let i = y1; i <= y2; i++) {
                let x = Math.round((i - b) / k);
                if (!(positions[positions.length - 1].x == x && positions[positions.length - 1].y == i)) {
                    let position = new Object();
                    position.num = 0;
                    position.x = x;
                    position.y = i;
                    positions.push(position);
                }
            }
        } else {
            for (let i = y1; i >= y2; i--) {
                let x = Math.round((i - b) / k);
                if (!(positions[positions.length - 1].x == x && positions[positions.length - 1].y == i)) {
                    let position = new Object();
                    position.num = 0;
                    position.x = x;
                    position.y = i;
                    positions.push(position);
                }
            }
        }
    }
}

/*计算dcm坐标*/
function calPositionX(x, moveX, scale) {
    return Math.round((x - moveX) / scale);
}

function calPositionY(y, moveY, scale) {
    return Math.round((y + moveY) / scale);
}

/*
* 禁用移动canvas
* isClear=1表示清空画布
* */
function forbidMove(isClear) {//isClear=1表示清空画布
    let canvas = $("#aboveCanvas")[0];
    ;
    if (isClear == 1) {
        canvas.height = canvas.height;
    }
    canvas.onmousewheel = canvasImgChange;
    canvas.onmousedown = null;
}

/*
* 绘制点集坐标
* context由canvas.getContext("2d")而来
* scale表示viewport缩放大小
* moveX,moveY分别表示xy偏移量
* */
function drawPositionList(context, scale, moveX, moveY, originLines) {
    let hadDraw = [];
    currentDraw.positionList.forEach((outline, index) => {
        let startX, startY, endX, endY;
        outline.forEach((position, index) => {
            if (index == 0) {
                context.moveTo(position.x * scale + moveX, position.y * scale - moveY);
                startX = position.x * scale + moveX;
                startY = position.y * scale - moveY;
            } else {
                context.lineTo(position.x * scale + moveX, position.y * scale - moveY);
                if (originLines != null) {
                    for (let i = 0; i < originLines.length; i++) {
                        let line = originLines[i];
                        if (hadDraw.indexOf(i) == -1) {
                            if (position.x * scale + moveX == line.startX && position.y * scale - moveY == line.startY) {
                                context.moveTo(line.endX, line.endY);
                                hadDraw.push(i);
                            } else if (position.x * scale + moveX == line.endX && position.y * scale - moveY == line.endY) {
                                context.moveTo(line.startX, line.startY);
                                hadDraw.push(i);
                            }
                        }
                        // originLines.startX,originLines.startY
                        // originLines.endX,originLines.endY
                    }
                }

                endX = position.x * scale + moveX;
                endY = position.y * scale - moveY;
            }
        });
        context.lineTo(startX, startY);
    });
}

/*
* 绘制结果
* sumx x总偏移量
* sumy y总偏移量
* lastViewport 上一个viewport
* */
function drawResult(translateX, translateY, originLines) {
    let canvas = $("#aboveCanvas")[0];
    let context = canvas.getContext("2d");
    let viewport = cornerstone.getViewport(targetElement);
    let scale = viewport.scale;
    let moveX = (canvas.width - imgWidth * scale) / 2;
    let moveY = (imgHeight * scale - canvas.height) / 2;
    context.clearRect(-5000, -5000, 9999, 9999);
    context.translate(translateX, translateY);
    context.beginPath();
    context.lineJoin = "round";
    context.strokeStyle = "#00ff00";
    context.lineWidth = scale;
    if (alreadySign&&!showCircle1) {
        drawPositionList(context, scale, moveX, moveY, originLines);
    }
    context.stroke();
    context.closePath();
    if (alreadySign && $sketchFile.indexOf("nodule") != -1) {
        context.beginPath();
        let curNumList = sketchNumResult[dcmIndex - 1];
        for (let i = 0; i < curNumList.length; i++) {
            context.font = ".2rem Airal";
            context.fillStyle = "#ffffff";
            context.fillText(curNumList[i].num, curNumList[i].x * scale + moveX + 7, curNumList[i].y * scale - moveY);
        }
        context.stroke();
        context.closePath();
    }
    if (allPositionsInfo.length!=0){
        if (!alreadySign){
            context.clearRect(-5000, -5000, 9999, 9999);
        }
        for (let i=0;i<allPositionsInfo.length;i++){
            if (dcmIndex-1==allPositionsInfo[i].index){
                allPositionsInfo[i].positionsArray.forEach((positions,positionsIndex)=>{
                    context.beginPath();
                    context.lineJoin = "round";
                    context.strokeStyle = "#00ff00";
                    context.lineWidth = scale;
                    positions.forEach((p,i)=>{
                        if (i==0){
                            context.moveTo(p.x * scale + moveX,p.y * scale - moveY);
                        }else {
                            context.lineTo(p.x * scale + moveX,p.y * scale - moveY);
                        }
                    });
                    context.lineTo(positions[0].x * scale + moveX,positions[0].y * scale - moveY);
                    context.stroke();
                    context.closePath();
                });
            }
        }
    }

}

function autoDisplay() {
    let state = $("#playIcon").attr("state");
    if (state == 'pause') {
        cornerstoneTools.playClip(targetElement, 10);
        $("#playIcon").attr('src', '../img/icon/pause.png');
        $("#playIcon").attr('state', 'play');
    } else if (state == 'play') {
        cornerstoneTools.stopClip(targetElement);
        $("#playIcon").attr('src', '../img/icon/play.png');
        $("#playIcon").attr('state', 'pause');
    }
}