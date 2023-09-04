package com.vz.encrypt.demo.common;

import lombok.Data;

@Data
public final class SysMessage {
    public enum Status {
        ERROR, SUCCESS
    }

    private int status;
    private String code;
    private Object data;
    private String message;

    public SysMessage() { }

    public SysMessage(Status status, String message, Object data, String code) {
        this.status = status.ordinal();
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public static SysMessage failure(String message) {
        return failure(ErrorCode.ERROR.getCode(), message);
    }
    public static SysMessage failure(String code, String message) {
        return new SysMessage(Status.ERROR, message, null, code);
    }
    public static SysMessage failure(String code, String message, Object obj) {
        return new SysMessage(Status.ERROR, message, obj, code);
    }

    public static SysMessage success() {
        return success(null);
    }
    public static SysMessage success(Object data) {
        return success(data, ErrorCode.SUCCESS.getMessage());
    }
    public static SysMessage success(Object data, String message) {
        return new SysMessage(Status.SUCCESS, message, data, ErrorCode.SUCCESS.getCode());
    }
}