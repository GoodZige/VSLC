function imageLoad(imageIds,element,sliceText,sliceElement) {
	(function (cs) {
		
	    "use strict";

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
	
	    function getPixelData(base64PixelData) {
	        var pixelDataAsString = window.atob(base64PixelData);
	        var pixelData = str2ab(pixelDataAsString);
	        return pixelData;
	    }
	    
	    
	    function getExampleImage(imageId) {
	        var tmp2 = imageId.split("//");
	    	var tmp = tmp2[1].split("_");

	        var sqId = tmp[0];
	        var zbz = tmp[1];
	        var imgId = tmp[2];
            var dcmIndex = tmp[3];
	        var image = null;
	        
            if(zbz == "x") {
	        	image = getMatrix(imageId,sqId,0,imgId,dcmIndex);
            } else if (zbz == "y") {
	        	image = getMatrix(imageId,sqId,1,imgId,dcmIndex);
			} else if (zbz == "z") {
				image = getMatrix(imageId,sqId,2,imgId,dcmIndex);
			}

	        return {
	            promise: new Promise((resolve) => {
	              resolve(image);
	            }),
	            cancelFn: undefined
	        };
	    }
	    
	 	// 获得图像矩阵
	    function getMatrix(id,$sequenceID,type,matrixIndex,dcmIndex) {
	    	var image = null;
            $.ajax({
                type: 'post',
                url: '/VSLC/sequence/getMatrix.action',
				async : false,
                data: {
                	sequenceID : $sequenceID,
					type : type,
					matrixIndex : matrixIndex,
					dcmIndex : dcmIndex
				},
                success: function (data) {
                    image = {
                        imageId: id,
                        minPixelValue : data.imgMinPixelValue,
                        maxPixelValue : data.imgMaxPixelValue,
                        slope: data.imgSlope,
                        intercept: data.imgIntercept,
                        windowCenter : data.imgWindowCenter,
                        windowWidth : data.imgWindowWidth,
                        render: cs.renderGrayscaleImage,
                        getPixelData: getPixelData3,
                        rows: data.imgHeight,
                        columns: data.imgWidth,
                        height: data.imgHeight,
                        width: data.imgWidth,
                        color: false,
                        columnPixelSpacing: data.imgColumnPixelSpacing,
                        rowPixelSpacing: data.imgRowPixelSpacing,
                        sizeInBytes: data.imgWidth * data.imgWidth * 2
                    };

                    function getPixelData3(){
                        return getPixelData(data.imgMatrix);
                    }
                }
            });
			return image;
	    }

	    cs.registerImageLoader('example', getExampleImage);
	    
	}(cornerstone));
	loadImg(imageIds,element,sliceText,sliceElement);
}
	