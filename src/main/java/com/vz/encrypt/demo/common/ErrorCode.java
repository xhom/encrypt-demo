package com.vz.encrypt.demo.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SUCCESS("1", "操作成功"),
    ERROR("0", "操作失败");

    private final String code ;
    private final String message ;
}
