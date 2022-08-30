package com.vslc.enums;

/**
 * Created by chenlele
 * 2018/4/15 16:27
 */
public enum  TransferMethodEnum {
    UPLOAD(0, "上传"),
    DOWNLOAD(1, "下载")
    ;

    private Integer code;

    private String message;

    TransferMethodEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
