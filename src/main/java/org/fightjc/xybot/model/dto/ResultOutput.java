package org.fightjc.xybot.model.dto;

import lombok.Getter;
import org.fightjc.xybot.enums.ResultCode;

@Getter
public class ResultOutput<T> {

    private int status;

    private String msg;

    private T data;

    public ResultOutput(ResultCode resultCode) {
        this(resultCode, resultCode.getMsg());
    }

    public ResultOutput(ResultCode resultCode, String msg) {
        this(resultCode, msg, null);
    }

    public ResultOutput(ResultCode resultCode, String msg, T data) {
        this(resultCode.getCode(), msg, data);
    }

    public ResultOutput(int status, String msg) {
        this(status, msg, null);
    }

    public ResultOutput(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
