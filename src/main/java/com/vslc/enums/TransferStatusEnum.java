package com.vslc.enums;

/**
 * Created by chenlele
 * 2018/4/15 16:24
 */
public enum TransferStatusEnum {
    ERROR(0, "错误"),
    SUCCESS(1, "成功")
    ;

    private Integer code;

    private String message;

    TransferStatusEnum(Integer code, String message) {
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
