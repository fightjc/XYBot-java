package org.fightjc.xybot.config;

import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.exception.ApiException;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResultOutput<String> handleApiException(ApiException e) {
        return new ResultOutput<>(ResultCode.FAILED, e.getMessage());
    }

    public ResultOutput<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return new ResultOutput<>(ResultCode.VALIDATE_FAILED, objectError.getDefaultMessage());
    }
}
