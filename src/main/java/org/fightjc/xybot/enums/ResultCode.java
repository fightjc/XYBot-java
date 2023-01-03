package org.fightjc.xybot.enums;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(0000, "success"),

    UNAUTHORIZED(1001, "unauthorized"),

    FORBIDDEN(1002, "no permission"),

    VALIDATE_FAILED(1003, "invalid parameter"),

    FAILED(1004, "failed"),

    ERROR(5000, "unknown error");

    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
