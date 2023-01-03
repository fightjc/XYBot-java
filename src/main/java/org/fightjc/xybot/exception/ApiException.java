package org.fightjc.xybot.exception;

import lombok.Getter;
import org.fightjc.xybot.enums.ResultCode;

@Getter
public class ApiException extends RuntimeException {

    private final ResultCode resultCode;

    private final String message;

    public ApiException(String message) {
        this(ResultCode.FAILED, message);
    }

    public ApiException(ResultCode resultCode) {
        this(resultCode, resultCode.getMsg());
    }

    public ApiException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
        this.message = message;
    }
}
