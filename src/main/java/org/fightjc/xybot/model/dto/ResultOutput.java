package org.fightjc.xybot.model.dto;

import lombok.Getter;
import org.fightjc.xybot.enums.ResultCode;

@Getter
public class ResultOutput<T> {

    private int code;

    private String msg;

    private T object;

    public ResultOutput(ResultCode resultCode, String info) {
        this(resultCode, info, null);
    }

    public ResultOutput(ResultCode resultCode, String msg, T object) {
        this.code = resultCode.getCode();
        this.msg = msg;
        this.object = object;
    }
}
