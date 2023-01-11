package org.fightjc.xybot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fightjc.xybot.enums.ResultCode;
import org.fightjc.xybot.exception.ApiException;
import org.fightjc.xybot.model.dto.ResultOutput;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = { "org.fightjc.xybot.controller" })
public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // if class type is not ResultOutput then return true and do more...
        return !methodParameter.getParameterType().equals(ResultOutput.class);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (methodParameter.getGenericParameterType().equals(String.class)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(new ResultOutput<>(ResultCode.SUCCESS, o));
            } catch (JsonProcessingException e) {
                throw new ApiException("返回 String 类型错误");
            }
        }
        return new ResultOutput<>(ResultCode.SUCCESS, o);
    }
}
